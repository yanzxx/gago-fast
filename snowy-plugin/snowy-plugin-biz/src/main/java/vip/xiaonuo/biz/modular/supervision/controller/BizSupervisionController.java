package vip.xiaonuo.biz.modular.supervision.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import vip.xiaonuo.biz.modular.supervision.param.BizSupervisionHomeParam;
import vip.xiaonuo.biz.modular.supervision.result.BizSupervisionHomeResult;
import vip.xiaonuo.biz.modular.supervision.service.BizSupervisionService;
import vip.xiaonuo.common.pojo.CommonResult;

import javax.annotation.Resource;

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
}
