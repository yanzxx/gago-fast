package vip.xiaonuo.biz.modular.loan.controller;

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
import vip.xiaonuo.biz.modular.loan.entity.BizLoanApply;
import vip.xiaonuo.biz.modular.loan.param.BizLoanApplyAddParam;
import vip.xiaonuo.biz.modular.loan.param.BizLoanApplyEditParam;
import vip.xiaonuo.biz.modular.loan.param.BizLoanApplyIdParam;
import vip.xiaonuo.biz.modular.loan.param.BizLoanApplyPageParam;
import vip.xiaonuo.biz.modular.loan.result.BizLoanApplyPageResult;
import vip.xiaonuo.biz.modular.loan.service.BizLoanApplyService;
import vip.xiaonuo.common.annotation.CommonLog;
import vip.xiaonuo.common.pojo.CommonResult;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * 贷款申请控制器
 */
@Api(tags = "贷款申请控制器")
@ApiSupport(author = "SNOWY_TEAM", order = 17)
@RestController
@Validated
public class BizLoanApplyController {

    @Resource
    private BizLoanApplyService bizLoanApplyService;

    @ApiOperationSupport(order = 1)
    @ApiOperation("新增贷款申请")
    @CommonLog("新增贷款申请")
    @PostMapping("/biz/loanAdministration/add")
    public CommonResult<String> add(@RequestBody @Valid BizLoanApplyAddParam addParam) {
        bizLoanApplyService.add(addParam);
        return CommonResult.ok();
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation("编辑贷款申请")
    @CommonLog("编辑贷款申请")
    @PostMapping("/biz/loanAdministration/edit")
    public CommonResult<String> edit(@RequestBody @Valid BizLoanApplyEditParam editParam) {
        bizLoanApplyService.edit(editParam);
        return CommonResult.ok();
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation("贷款申请详情")
    @GetMapping("/biz/loanAdministration/detail")
    public CommonResult<BizLoanApply> detail(@Valid BizLoanApplyIdParam idParam) {
        return CommonResult.data(bizLoanApplyService.detail(idParam));
    }

    @ApiOperationSupport(order = 4)
    @ApiOperation("贷款申请分页")
    @GetMapping("/biz/loanAdministration/page")
    public CommonResult<Page<BizLoanApplyPageResult>> page(BizLoanApplyPageParam pageParam) {
        return CommonResult.data(bizLoanApplyService.page(pageParam));
    }

    @ApiOperationSupport(order = 5)
    @ApiOperation("删除贷款申请")
    @CommonLog("删除贷款申请")
    @PostMapping("/biz/loanAdministration/delete")
    public CommonResult<String> delete(@RequestBody @Valid BizLoanApplyIdParam idParam) {
        bizLoanApplyService.delete(idParam);
        return CommonResult.ok();
    }
}
