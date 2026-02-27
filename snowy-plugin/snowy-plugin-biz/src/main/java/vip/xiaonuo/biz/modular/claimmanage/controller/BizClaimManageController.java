package vip.xiaonuo.biz.modular.claimmanage.controller;

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
import vip.xiaonuo.biz.modular.claimmanage.entity.BizClaimManage;
import vip.xiaonuo.biz.modular.claimmanage.param.BizClaimManageAddParam;
import vip.xiaonuo.biz.modular.claimmanage.param.BizClaimManageHandleParam;
import vip.xiaonuo.biz.modular.claimmanage.param.BizClaimManageIdParam;
import vip.xiaonuo.biz.modular.claimmanage.param.BizClaimManagePageParam;
import vip.xiaonuo.biz.modular.claimmanage.param.BizClaimManageSupplementParam;
import vip.xiaonuo.biz.modular.claimmanage.result.BizClaimManagePageResult;
import vip.xiaonuo.biz.modular.claimmanage.service.BizClaimManageService;
import vip.xiaonuo.common.annotation.CommonLog;
import vip.xiaonuo.common.pojo.CommonResult;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * 理赔管理控制器
 */
@Api(tags = "理赔管理控制器")
@ApiSupport(author = "SNOWY_TEAM", order = 23)
@RestController
@Validated
public class BizClaimManageController {

    @Resource
    private BizClaimManageService bizClaimManageService;

    @ApiOperationSupport(order = 1)
    @ApiOperation("理赔分页")
    @GetMapping("/biz/claimManage/page")
    public CommonResult<Page<BizClaimManagePageResult>> page(BizClaimManagePageParam pageParam) {
        return CommonResult.data(bizClaimManageService.page(pageParam));
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation("理赔详情")
    @GetMapping("/biz/claimManage/detail")
    public CommonResult<BizClaimManage> detail(@Valid BizClaimManageIdParam idParam) {
        return CommonResult.data(bizClaimManageService.detail(idParam));
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation("新增理赔")
    @CommonLog("新增理赔")
    @PostMapping("/biz/claimManage/add")
    public CommonResult<String> add(@RequestBody @Valid BizClaimManageAddParam addParam) {
        bizClaimManageService.add(addParam);
        return CommonResult.ok();
    }

    @ApiOperationSupport(order = 4)
    @ApiOperation("补充材料")
    @CommonLog("理赔补充材料")
    @PostMapping("/biz/claimManage/supplement")
    public CommonResult<String> supplement(@RequestBody @Valid BizClaimManageSupplementParam supplementParam) {
        bizClaimManageService.supplement(supplementParam);
        return CommonResult.ok();
    }

    @ApiOperationSupport(order = 5)
    @ApiOperation("处理结果登记")
    @CommonLog("理赔处理结果登记")
    @PostMapping("/biz/claimManage/handle")
    public CommonResult<String> handle(@RequestBody @Valid BizClaimManageHandleParam handleParam) {
        bizClaimManageService.handle(handleParam);
        return CommonResult.ok();
    }

    @ApiOperationSupport(order = 6)
    @ApiOperation("导出理赔报告")
    @GetMapping("/biz/claimManage/exportReport")
    public CommonResult<String> exportReport(@Valid BizClaimManageIdParam idParam) {
        return CommonResult.data(bizClaimManageService.exportReport(idParam));
    }
}
