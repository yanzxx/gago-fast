package vip.xiaonuo.biz.modular.supervision.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import vip.xiaonuo.biz.modular.supervision.param.BizSupervisionFarmStatsParam;
import vip.xiaonuo.biz.modular.supervision.param.BizSupervisionHomeParam;
import vip.xiaonuo.biz.modular.supervision.param.BizSupervisionRiskThresholdParam;
import vip.xiaonuo.biz.modular.supervision.param.BizSupervisionRiskThresholdSaveParam;
import vip.xiaonuo.biz.modular.supervision.result.BizSupervisionFarmStatsResult;
import vip.xiaonuo.biz.modular.supervision.result.BizSupervisionHomeResult;
import vip.xiaonuo.biz.modular.supervision.result.BizSupervisionRiskThresholdResult;
import vip.xiaonuo.biz.modular.supervision.service.BizSupervisionService;
import vip.xiaonuo.common.annotation.CommonLog;
import vip.xiaonuo.common.pojo.CommonResult;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * 综合监管控制器
 */
@Api(tags = "综合监管控制器")
@ApiSupport(author = "SNOWY_TEAM", order = 24)
@RestController
@Validated
public class BizSupervisionController {

    @Resource
    private BizSupervisionService bizSupervisionService;

    @ApiOperationSupport(order = 1)
    @ApiOperation("综合监管首页")
    @GetMapping("/biz/supervision/home")
    public CommonResult<BizSupervisionHomeResult> home(BizSupervisionHomeParam homeParam) {
        return CommonResult.data(bizSupervisionService.home(homeParam));
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation("农场经营健康与贷款统计")
    @GetMapping("/biz/supervision/farmStats")
    public CommonResult<BizSupervisionFarmStatsResult> farmStats(BizSupervisionFarmStatsParam param) {
        return CommonResult.data(bizSupervisionService.farmStats(param));
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation("风险阈值详情")
    @GetMapping("/biz/supervision/riskThreshold/detail")
    public CommonResult<BizSupervisionRiskThresholdResult> riskThreshold(BizSupervisionRiskThresholdParam param) {
        return CommonResult.data(bizSupervisionService.riskThreshold(param));
    }

    @ApiOperationSupport(order = 4)
    @ApiOperation("保存风险阈值")
    @CommonLog("保存风险阈值")
    @PostMapping("/biz/supervision/riskThreshold/save")
    public CommonResult<String> saveRiskThreshold(@RequestBody @Valid BizSupervisionRiskThresholdSaveParam param) {
        bizSupervisionService.saveRiskThreshold(param);
        return CommonResult.ok();
    }
}
