package vip.xiaonuo.biz.modular.afterloan.controller;

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
import vip.xiaonuo.biz.modular.afterloan.param.BizAfterLoanAnomalyPageParam;
import vip.xiaonuo.biz.modular.afterloan.param.BizAfterLoanSendNoticeParam;
import vip.xiaonuo.biz.modular.afterloan.param.BizAfterLoanStatsParam;
import vip.xiaonuo.biz.modular.afterloan.result.BizAfterLoanAnomalyPageResult;
import vip.xiaonuo.biz.modular.afterloan.result.BizAfterLoanStatsResult;
import vip.xiaonuo.biz.modular.afterloan.service.BizAfterLoanAdministrationService;
import vip.xiaonuo.common.annotation.CommonLog;
import vip.xiaonuo.common.pojo.CommonResult;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * 贷后管理控制器
 */
@Api(tags = "贷后管理控制器")
@ApiSupport(author = "SNOWY_TEAM", order = 18)
@RestController
@Validated
public class BizAfterLoanAdministrationController {

    @Resource
    private BizAfterLoanAdministrationService bizAfterLoanAdministrationService;

    @ApiOperationSupport(order = 1)
    @ApiOperation("贷后统计")
    @GetMapping("/biz/afterLoanAdministration/stats")
    public CommonResult<BizAfterLoanStatsResult> stats(BizAfterLoanStatsParam statsParam) {
        return CommonResult.data(bizAfterLoanAdministrationService.stats(statsParam));
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation("贷后异常分页")
    @GetMapping("/biz/afterLoanAdministration/anomalyPage")
    public CommonResult<Page<BizAfterLoanAnomalyPageResult>> anomalyPage(BizAfterLoanAnomalyPageParam pageParam) {
        return CommonResult.data(bizAfterLoanAdministrationService.anomalyPage(pageParam));
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation("发送整改通知")
    @CommonLog("发送整改通知")
    @PostMapping("/biz/afterLoanAdministration/sendRectifyNotice")
    public CommonResult<String> sendRectifyNotice(@RequestBody @Valid BizAfterLoanSendNoticeParam sendNoticeParam) {
        bizAfterLoanAdministrationService.sendRectifyNotice(sendNoticeParam);
        return CommonResult.ok();
    }
}
