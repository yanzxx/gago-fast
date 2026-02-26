package vip.xiaonuo.sys.modular.region.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.lang.tree.Tree;
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
import org.springframework.web.bind.annotation.RequestMapping;
import vip.xiaonuo.common.annotation.CommonLog;
import vip.xiaonuo.common.pojo.CommonResult;
import vip.xiaonuo.common.pojo.CommonValidList;
import vip.xiaonuo.sys.entity.SysOrg;
import vip.xiaonuo.sys.entity.SysUser;
import vip.xiaonuo.sys.modular.org.param.SysOrgAddParam;
import vip.xiaonuo.sys.modular.org.param.SysOrgEditParam;
import vip.xiaonuo.sys.modular.org.param.SysOrgIdParam;
import vip.xiaonuo.sys.modular.org.param.SysOrgSelectorUserParam;
import vip.xiaonuo.sys.modular.region.entity.Region;
import vip.xiaonuo.sys.modular.region.param.RegionAddParam;
import vip.xiaonuo.sys.modular.region.param.RegionEditParam;
import vip.xiaonuo.sys.modular.region.param.RegionIdParam;
import vip.xiaonuo.sys.modular.region.param.RegionPageParam;
import vip.xiaonuo.sys.modular.region.service.RegionService;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 行政区划控制器
 *
 * @author gago
 * @date  2025/08/26 15:08
 */
@Api(tags = "行政区划控制器")
@ApiSupport(author = "SNOWY_TEAM", order = 1)
@RequestMapping("/sys/region")
@RestController
@Validated
public class RegionController {

    @Resource
    private RegionService regionService;

    /**
     * 获取行政区划分页
     *
     * @author gago
     * @date  2025/08/26 15:08
     */
    @ApiOperationSupport(order = 1)
    @ApiOperation("获取行政区划分页")
    @GetMapping("/page")
    public CommonResult<Page<Region>> page(RegionPageParam regionPageParam) {
        return CommonResult.data(regionService.page(regionPageParam));
    }

    /**
     * 添加行政区划
     *
     * @author gago
     * @date  2025/08/26 15:08
     */
    @ApiOperationSupport(order = 2)
    @ApiOperation("添加行政区划")
    @CommonLog("添加行政区划")
    @PostMapping("/add")
    public CommonResult<String> add(@RequestBody @Valid RegionAddParam regionAddParam) {
        regionService.add(regionAddParam);
        return CommonResult.ok();
    }

    /**
     * 编辑行政区划
     *
     * @author gago
     * @date  2025/08/26 15:08
     */
    @ApiOperationSupport(order = 3)
    @ApiOperation("编辑行政区划")
    @CommonLog("编辑行政区划")
    @PostMapping("/edit")
    public CommonResult<String> edit(@RequestBody @Valid RegionEditParam regionEditParam) {
        regionService.edit(regionEditParam);
        return CommonResult.ok();
    }

    /**
     * 删除行政区划
     *
     * @author gago
     * @date  2025/08/26 15:08
     */
    @ApiOperationSupport(order = 4)
    @ApiOperation("删除行政区划")
    @CommonLog("删除行政区划")
    @PostMapping("/delete")
    public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                                   CommonValidList<RegionIdParam> regionIdParamList) {
        regionService.delete(regionIdParamList);
        return CommonResult.ok();
    }

    /**
     * 获取行政区划详情
     *
     * @author gago
     * @date  2025/08/26 15:08
     */
    @ApiOperationSupport(order = 5)
    @ApiOperation("获取行政区划详情")
    @GetMapping("/detail")
    public CommonResult<Region> detail(@Valid RegionIdParam regionIdParam) {
        return CommonResult.data(regionService.detail(regionIdParam));
    }

    /**
     * 导出行政区划数据
     *
     * @author gago
     * @date  2025/08/26 15:08
     */
    @ApiOperationSupport(order = 6)
    @ApiOperation("导出行政区划数据")
    @GetMapping("/excel-out")
    public void excelOut(HttpServletResponse response, RegionPageParam regionPageParam) {
        regionService.excelOut(response, regionPageParam);
    }


    /**
     * 导入行政区划数据
     *
     * @author gago
     * @date  2025/08/26 15:08
     */
    @ApiOperationSupport(order = 7)
    @ApiOperation("导入行政区划数据")
    @PostMapping("/excel-in")
    public CommonResult<String> excelIn(@RequestBody MultipartFile file) {
        regionService.excelIn(file);
        return CommonResult.ok();
    }

    /**
     * 获取组织树
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 2)
    @ApiOperation("获取组织树")
    @GetMapping("/tree")
    public CommonResult<List<Tree<Long>>> tree() {
        return CommonResult.data(regionService.tree());
    }

    /* ====组织部分所需要用到的选择器==== */

    /**
     * 获取组织树选择器
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 7)
    @ApiOperation("获取组织树选择器")
    @GetMapping("/orgTreeSelector")
    public CommonResult<List<Tree<String>>> orgTreeSelector() {
        return CommonResult.data(regionService.orgTreeSelector());
    }

}
