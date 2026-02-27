package vip.xiaonuo.biz.modular.bhmanage.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import vip.xiaonuo.auth.core.pojo.SaBaseLoginUser;
import vip.xiaonuo.auth.core.util.StpLoginUserUtil;
import vip.xiaonuo.biz.modular.afterloan.entity.BizAfterLoanAnomaly;
import vip.xiaonuo.biz.modular.afterloan.mapper.BizAfterLoanAnomalyMapper;
import vip.xiaonuo.biz.modular.afterloan.param.BizAfterLoanAnomalyPageParam;
import vip.xiaonuo.biz.modular.afterloan.param.BizAfterLoanSendNoticeParam;
import vip.xiaonuo.biz.modular.afterloan.param.BizAfterLoanStatsParam;
import vip.xiaonuo.biz.modular.afterloan.result.BizAfterLoanAnomalyPageResult;
import vip.xiaonuo.biz.modular.afterloan.result.BizAfterLoanStatsResult;
import vip.xiaonuo.biz.modular.afterloan.service.BizAfterLoanAdministrationService;
import vip.xiaonuo.biz.modular.bhmanage.param.BizBhTrackDetailParam;
import vip.xiaonuo.biz.modular.bhmanage.result.BizBhTrackDetailResult;
import vip.xiaonuo.biz.modular.bhmanage.result.BizBhTrackPointResult;
import vip.xiaonuo.common.annotation.CommonLog;
import vip.xiaonuo.common.exception.CommonException;
import vip.xiaonuo.common.pojo.CommonResult;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 保后管理控制器
 */
@Api(tags = "保后管理控制器")
@ApiSupport(author = "SNOWY_TEAM", order = 19)
@RestController
@Validated
public class BizBhManageController {

    @Resource
    private BizAfterLoanAdministrationService bizAfterLoanAdministrationService;

    @Resource
    private BizAfterLoanAnomalyMapper bizAfterLoanAnomalyMapper;

    @ApiOperationSupport(order = 1)
    @ApiOperation("保后统计")
    @GetMapping("/biz/bhManage/stats")
    public CommonResult<BizAfterLoanStatsResult> stats(BizAfterLoanStatsParam statsParam) {
        return CommonResult.data(bizAfterLoanAdministrationService.stats(statsParam));
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation("保后异常分页")
    @GetMapping("/biz/bhManage/anomalyPage")
    public CommonResult<Page<BizAfterLoanAnomalyPageResult>> anomalyPage(BizAfterLoanAnomalyPageParam pageParam) {
        return CommonResult.data(bizAfterLoanAdministrationService.anomalyPage(pageParam));
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation("保后轨迹详情")
    @GetMapping("/biz/bhManage/trackDetail")
    public CommonResult<BizBhTrackDetailResult> trackDetail(@Valid BizBhTrackDetailParam trackDetailParam) {
        BizAfterLoanAnomaly anomaly = bizAfterLoanAnomalyMapper.selectById(trackDetailParam.getAnomalyId());
        if (ObjectUtil.isEmpty(anomaly)) {
            throw new CommonException("异常记录不存在，id：{}", trackDetailParam.getAnomalyId());
        }
        boolean allFarm = isAllFarmVisible();
        if (!allFarm) {
            List<String> visibleFarmIds = getVisibleFarmIds();
            if (CollUtil.isEmpty(visibleFarmIds) || !visibleFarmIds.contains(anomaly.getFarmId())) {
                throw new CommonException("您没有权限查看该异常轨迹，养殖场id：{}", anomaly.getFarmId());
            }
        }
        BizBhTrackDetailResult result = new BizBhTrackDetailResult();
        result.setAnomalyId(anomaly.getId());
        result.setPoints(buildTrackPoints(anomaly));
        return CommonResult.data(result);
    }

    @ApiOperationSupport(order = 4)
    @ApiOperation("发送整改通知")
    @CommonLog("发送保后整改通知")
    @PostMapping("/biz/bhManage/sendRectifyNotice")
    public CommonResult<String> sendRectifyNotice(@RequestBody @Valid BizAfterLoanSendNoticeParam sendNoticeParam) {
        bizAfterLoanAdministrationService.sendRectifyNotice(sendNoticeParam);
        return CommonResult.ok();
    }

    private boolean isAllFarmVisible() {
        SaBaseLoginUser loginUser = StpLoginUserUtil.getLoginUser();
        return ObjectUtil.isNotEmpty(loginUser) && CollUtil.contains(loginUser.getRoleCodeList(), "superAdmin");
    }

    private List<String> getVisibleFarmIds() {
        List<String> dataScopeFarmIds = StpLoginUserUtil.getLoginUserDataScope();
        if (CollUtil.isNotEmpty(dataScopeFarmIds)) {
            return dataScopeFarmIds.stream().distinct().collect(Collectors.toList());
        }
        String farmId = StpLoginUserUtil.getLoginUser().getOrgId();
        return StrUtil.isNotBlank(farmId) ? Collections.singletonList(farmId) : Collections.emptyList();
    }

    private List<BizBhTrackPointResult> buildTrackPoints(BizAfterLoanAnomaly anomaly) {
        List<BizBhTrackPointResult> points = new ArrayList<>();
        long baseHash = Math.abs(StrUtil.emptyToDefault(anomaly.getId(), "0").hashCode());
        double baseLng = 114.26 + (baseHash % 45) * 0.001;
        double baseLat = 30.53 + (baseHash % 35) * 0.001;

        points.add(trackPoint("P1", baseLng, baseLat,
                DateUtil.format(DateUtil.offsetHour(anomaly.getTriggerTime(), -2), DatePattern.NORM_DATETIME_PATTERN), "围栏内活动"));
        points.add(trackPoint("P2", baseLng + 0.003, baseLat + 0.002,
                DateUtil.format(DateUtil.offsetHour(anomaly.getTriggerTime(), -1), DatePattern.NORM_DATETIME_PATTERN), "接近异常区域"));
        points.add(trackPoint("P3", baseLng + 0.006, baseLat + 0.004,
                DateUtil.format(anomaly.getTriggerTime(), DatePattern.NORM_DATETIME_PATTERN), "触发" + anomaly.getAnomalyType()));
        return points;
    }

    private BizBhTrackPointResult trackPoint(String id, double lng, double lat, String eventTime, String eventDesc) {
        BizBhTrackPointResult item = new BizBhTrackPointResult();
        item.setId(id);
        item.setLng(lng);
        item.setLat(lat);
        item.setEventTime(eventTime);
        item.setEventDesc(eventDesc);
        return item;
    }
}
