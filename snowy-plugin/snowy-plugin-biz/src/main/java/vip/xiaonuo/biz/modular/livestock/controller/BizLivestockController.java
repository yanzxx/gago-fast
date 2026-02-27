package vip.xiaonuo.biz.modular.livestock.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import vip.xiaonuo.biz.modular.livestock.entity.BizLivestock;
import vip.xiaonuo.biz.modular.livestock.param.BizLivestockAddParam;
import vip.xiaonuo.biz.modular.livestock.param.BizLivestockEditParam;
import vip.xiaonuo.biz.modular.livestock.param.BizLivestockIdParam;
import vip.xiaonuo.biz.modular.livestock.param.BizLivestockPageParam;
import vip.xiaonuo.biz.modular.livestock.param.BizLivestockSpeciesOptionParam;
import vip.xiaonuo.biz.modular.livestock.result.BizLivestockPageResult;
import vip.xiaonuo.biz.modular.livestock.result.BizLivestockSpeciesOptionResult;
import vip.xiaonuo.biz.modular.livestock.service.BizLivestockService;
import vip.xiaonuo.common.annotation.CommonLog;
import vip.xiaonuo.common.pojo.CommonResult;

import java.util.List;
import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * 牲畜登记控制器
 */
@Api(tags = "牲畜登记控制器")
@ApiSupport(author = "SNOWY_TEAM", order = 15)
@RestController
@Validated
public class BizLivestockController {

    @Resource
    private BizLivestockService bizLivestockService;

    @ApiOperationSupport(order = 1)
    @ApiOperation("新增牲畜登记")
    @CommonLog("新增牲畜登记")
    @PostMapping("/biz/livestock/add")
    public CommonResult<String> add(@RequestBody @Valid BizLivestockAddParam addParam) {
        bizLivestockService.add(addParam);
        return CommonResult.ok();
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation("编辑牲畜登记")
    @CommonLog("编辑牲畜登记")
    @PostMapping("/biz/livestock/edit")
    public CommonResult<String> edit(@RequestBody @Valid BizLivestockEditParam editParam) {
        bizLivestockService.edit(editParam);
        return CommonResult.ok();
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation("牲畜登记详情")
    @GetMapping("/biz/livestock/detail")
    public CommonResult<BizLivestock> detail(@Valid BizLivestockIdParam idParam) {
        return CommonResult.data(bizLivestockService.detail(idParam));
    }

    @ApiOperationSupport(order = 4)
    @ApiOperation("牲畜登记分页")
    @GetMapping("/biz/livestock/page")
    public CommonResult<Page<BizLivestockPageResult>> page(BizLivestockPageParam pageParam) {
        return CommonResult.data(bizLivestockService.page(pageParam));
    }

    @ApiOperationSupport(order = 5)
    @ApiOperation("畜种下拉选项")
    @GetMapping("/biz/livestock/species/options")
    public CommonResult<List<BizLivestockSpeciesOptionResult>> speciesOptions(
            BizLivestockSpeciesOptionParam optionParam) {
        return CommonResult.data(bizLivestockService.speciesOptions(optionParam));
    }
}
