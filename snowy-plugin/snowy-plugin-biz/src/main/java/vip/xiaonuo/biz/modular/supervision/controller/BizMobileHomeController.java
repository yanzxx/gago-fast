package vip.xiaonuo.biz.modular.supervision.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import vip.xiaonuo.biz.modular.supervision.param.BizMobileHomeParam;
import vip.xiaonuo.biz.modular.supervision.result.BizMobileHomeHeaderResult;
import vip.xiaonuo.biz.modular.supervision.result.BizMobileHomeMetricsResult;
import vip.xiaonuo.biz.modular.supervision.result.BizMobileHomeTodoItemResult;
import vip.xiaonuo.biz.modular.supervision.result.BizMobileHomeTodoSummaryResult;
import vip.xiaonuo.biz.modular.supervision.service.BizMobileHomeService;
import vip.xiaonuo.common.pojo.CommonResult;

import javax.annotation.Resource;
import java.util.List;

/**
 * 移动端首页控制器
 */
@Api(tags = "移动端首页控制器")
@ApiSupport(author = "SNOWY_TEAM", order = 25)
@RestController
@Validated
public class BizMobileHomeController {

    @Resource
    private BizMobileHomeService bizMobileHomeService;

    @ApiOperationSupport(order = 1)
    @ApiOperation("移动端首页头部信息")
    @GetMapping("/biz/mobile/home/header")
    public CommonResult<BizMobileHomeHeaderResult> header(BizMobileHomeParam param) {
        return CommonResult.data(bizMobileHomeService.header(param));
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation("移动端首页核心指标")
    @GetMapping("/biz/mobile/home/metrics")
    public CommonResult<BizMobileHomeMetricsResult> metrics(BizMobileHomeParam param) {
        return CommonResult.data(bizMobileHomeService.metrics(param));
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation("移动端首页待办聚合")
    @GetMapping("/biz/mobile/home/todos")
    public CommonResult<List<BizMobileHomeTodoSummaryResult>> todos(BizMobileHomeParam param, Integer topN) {
        return CommonResult.data(bizMobileHomeService.todos(param, topN));
    }

    @ApiOperationSupport(order = 4)
    @ApiOperation("移动端首页待办详情")
    @GetMapping("/biz/mobile/home/todoDetails")
    public CommonResult<List<BizMobileHomeTodoItemResult>> todoDetails(BizMobileHomeParam param, String todoType) {
        return CommonResult.data(bizMobileHomeService.todoDetails(param, todoType));
    }
}
