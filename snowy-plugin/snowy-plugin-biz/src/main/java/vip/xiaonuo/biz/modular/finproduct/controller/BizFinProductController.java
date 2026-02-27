package vip.xiaonuo.biz.modular.finproduct.controller;

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
import vip.xiaonuo.biz.modular.finproduct.entity.BizFinProduct;
import vip.xiaonuo.biz.modular.finproduct.param.BizFinProductAddParam;
import vip.xiaonuo.biz.modular.finproduct.param.BizFinProductEditParam;
import vip.xiaonuo.biz.modular.finproduct.param.BizFinProductIdParam;
import vip.xiaonuo.biz.modular.finproduct.param.BizFinProductPageParam;
import vip.xiaonuo.biz.modular.finproduct.result.BizFinProductPageResult;
import vip.xiaonuo.biz.modular.finproduct.service.BizFinProductService;
import vip.xiaonuo.common.annotation.CommonLog;
import vip.xiaonuo.common.pojo.CommonResult;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * 金融产品控制器
 */
@Api(tags = "金融产品控制器")
@ApiSupport(author = "SNOWY_TEAM", order = 16)
@RestController
@Validated
public class BizFinProductController {

    @Resource
    private BizFinProductService bizFinProductService;

    @ApiOperationSupport(order = 1)
    @ApiOperation("新增金融产品")
    @CommonLog("新增金融产品")
    @PostMapping("/biz/finProduct/add")
    public CommonResult<String> add(@RequestBody @Valid BizFinProductAddParam addParam) {
        bizFinProductService.add(addParam);
        return CommonResult.ok();
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation("编辑金融产品")
    @CommonLog("编辑金融产品")
    @PostMapping("/biz/finProduct/edit")
    public CommonResult<String> edit(@RequestBody @Valid BizFinProductEditParam editParam) {
        bizFinProductService.edit(editParam);
        return CommonResult.ok();
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation("金融产品详情")
    @GetMapping("/biz/finProduct/detail")
    public CommonResult<BizFinProduct> detail(@Valid BizFinProductIdParam idParam) {
        return CommonResult.data(bizFinProductService.detail(idParam));
    }

    @ApiOperationSupport(order = 4)
    @ApiOperation("金融产品分页")
    @GetMapping("/biz/finProduct/page")
    public CommonResult<Page<BizFinProductPageResult>> page(BizFinProductPageParam pageParam) {
        return CommonResult.data(bizFinProductService.page(pageParam));
    }

    @ApiOperationSupport(order = 5)
    @ApiOperation("上架金融产品")
    @CommonLog("上架金融产品")
    @PostMapping("/biz/finProduct/onShelf")
    public CommonResult<String> onShelf(@RequestBody @Valid BizFinProductIdParam idParam) {
        bizFinProductService.onShelf(idParam);
        return CommonResult.ok();
    }

    @ApiOperationSupport(order = 6)
    @ApiOperation("下架金融产品")
    @CommonLog("下架金融产品")
    @PostMapping("/biz/finProduct/offShelf")
    public CommonResult<String> offShelf(@RequestBody @Valid BizFinProductIdParam idParam) {
        bizFinProductService.offShelf(idParam);
        return CommonResult.ok();
    }

    @ApiOperationSupport(order = 7)
    @ApiOperation("删除金融产品")
    @CommonLog("删除金融产品")
    @PostMapping("/biz/finProduct/delete")
    public CommonResult<String> delete(@RequestBody @Valid BizFinProductIdParam idParam) {
        bizFinProductService.delete(idParam);
        return CommonResult.ok();
    }
}
