package vip.xiaonuo.biz.modular.insproduct.controller;

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
import vip.xiaonuo.biz.modular.insproduct.entity.BizInsProduct;
import vip.xiaonuo.biz.modular.insproduct.param.BizInsProductAddParam;
import vip.xiaonuo.biz.modular.insproduct.param.BizInsProductEditParam;
import vip.xiaonuo.biz.modular.insproduct.param.BizInsProductIdParam;
import vip.xiaonuo.biz.modular.insproduct.param.BizInsProductPageParam;
import vip.xiaonuo.biz.modular.insproduct.result.BizInsProductPageResult;
import vip.xiaonuo.biz.modular.insproduct.service.BizInsProductService;
import vip.xiaonuo.common.annotation.CommonLog;
import vip.xiaonuo.common.pojo.CommonResult;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * 保险产品控制器
 */
@Api(tags = "保险产品控制器")
@ApiSupport(author = "SNOWY_TEAM", order = 19)
@RestController
@Validated
public class BizInsProductController {

    @Resource
    private BizInsProductService bizInsProductService;

    @ApiOperationSupport(order = 1)
    @ApiOperation("新增保险产品")
    @CommonLog("新增保险产品")
    @PostMapping("/biz/insProduct/add")
    public CommonResult<String> add(@RequestBody @Valid BizInsProductAddParam addParam) {
        bizInsProductService.add(addParam);
        return CommonResult.ok();
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation("编辑保险产品")
    @CommonLog("编辑保险产品")
    @PostMapping("/biz/insProduct/edit")
    public CommonResult<String> edit(@RequestBody @Valid BizInsProductEditParam editParam) {
        bizInsProductService.edit(editParam);
        return CommonResult.ok();
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation("保险产品详情")
    @GetMapping("/biz/insProduct/detail")
    public CommonResult<BizInsProduct> detail(@Valid BizInsProductIdParam idParam) {
        return CommonResult.data(bizInsProductService.detail(idParam));
    }

    @ApiOperationSupport(order = 4)
    @ApiOperation("保险产品分页")
    @GetMapping("/biz/insProduct/page")
    public CommonResult<Page<BizInsProductPageResult>> page(BizInsProductPageParam pageParam) {
        return CommonResult.data(bizInsProductService.page(pageParam));
    }

    @ApiOperationSupport(order = 5)
    @ApiOperation("上架保险产品")
    @CommonLog("上架保险产品")
    @PostMapping("/biz/insProduct/onShelf")
    public CommonResult<String> onShelf(@RequestBody @Valid BizInsProductIdParam idParam) {
        bizInsProductService.onShelf(idParam);
        return CommonResult.ok();
    }

    @ApiOperationSupport(order = 6)
    @ApiOperation("下架保险产品")
    @CommonLog("下架保险产品")
    @PostMapping("/biz/insProduct/offShelf")
    public CommonResult<String> offShelf(@RequestBody @Valid BizInsProductIdParam idParam) {
        bizInsProductService.offShelf(idParam);
        return CommonResult.ok();
    }

    @ApiOperationSupport(order = 7)
    @ApiOperation("删除保险产品")
    @CommonLog("删除保险产品")
    @PostMapping("/biz/insProduct/delete")
    public CommonResult<String> delete(@RequestBody @Valid BizInsProductIdParam idParam) {
        bizInsProductService.delete(idParam);
        return CommonResult.ok();
    }
}
