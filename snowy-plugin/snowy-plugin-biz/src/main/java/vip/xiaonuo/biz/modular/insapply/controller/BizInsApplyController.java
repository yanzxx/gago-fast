package vip.xiaonuo.biz.modular.insapply.controller;

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
import vip.xiaonuo.biz.modular.insapply.entity.BizInsApply;
import vip.xiaonuo.biz.modular.insapply.param.BizInsApplyAddParam;
import vip.xiaonuo.biz.modular.insapply.param.BizInsApplyEditParam;
import vip.xiaonuo.biz.modular.insapply.param.BizInsApplyIdParam;
import vip.xiaonuo.biz.modular.insapply.param.BizInsApplyPageParam;
import vip.xiaonuo.biz.modular.insapply.result.BizInsApplyPageResult;
import vip.xiaonuo.biz.modular.insapply.service.BizInsApplyService;
import vip.xiaonuo.common.annotation.CommonLog;
import vip.xiaonuo.common.pojo.CommonResult;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * 投保记录控制器
 */
@Api(tags = "投保记录控制器")
@ApiSupport(author = "SNOWY_TEAM", order = 20)
@RestController
@Validated
public class BizInsApplyController {

    @Resource
    private BizInsApplyService bizInsApplyService;

    @ApiOperationSupport(order = 1)
    @ApiOperation("新增投保记录")
    @CommonLog("新增投保记录")
    @PostMapping("/biz/insApply/add")
    public CommonResult<String> add(@RequestBody @Valid BizInsApplyAddParam addParam) {
        bizInsApplyService.add(addParam);
        return CommonResult.ok();
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation("编辑投保记录")
    @CommonLog("编辑投保记录")
    @PostMapping("/biz/insApply/edit")
    public CommonResult<String> edit(@RequestBody @Valid BizInsApplyEditParam editParam) {
        bizInsApplyService.edit(editParam);
        return CommonResult.ok();
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation("投保记录详情")
    @GetMapping("/biz/insApply/detail")
    public CommonResult<BizInsApply> detail(@Valid BizInsApplyIdParam idParam) {
        return CommonResult.data(bizInsApplyService.detail(idParam));
    }

    @ApiOperationSupport(order = 4)
    @ApiOperation("投保记录分页")
    @GetMapping("/biz/insApply/page")
    public CommonResult<Page<BizInsApplyPageResult>> page(BizInsApplyPageParam pageParam) {
        return CommonResult.data(bizInsApplyService.page(pageParam));
    }

    @ApiOperationSupport(order = 5)
    @ApiOperation("删除投保记录")
    @CommonLog("删除投保记录")
    @PostMapping("/biz/insApply/delete")
    public CommonResult<String> delete(@RequestBody @Valid BizInsApplyIdParam idParam) {
        bizInsApplyService.delete(idParam);
        return CommonResult.ok();
    }
}
