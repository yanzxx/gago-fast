
package vip.xiaonuo.gen.modular.basic.controller;

import cn.hutool.json.JSONObject;
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
import vip.xiaonuo.common.annotation.CommonLog;
import vip.xiaonuo.common.pojo.CommonResult;
import vip.xiaonuo.common.pojo.CommonValidList;
import vip.xiaonuo.gen.modular.basic.entity.GenBasic;
import vip.xiaonuo.gen.modular.basic.param.*;
import vip.xiaonuo.gen.modular.basic.result.GenBasicDbsSelectorResult;
import vip.xiaonuo.gen.modular.basic.result.GenBasicPreviewResult;
import vip.xiaonuo.gen.modular.basic.result.GenBasicTableColumnResult;
import vip.xiaonuo.gen.modular.basic.result.GenBasicTableResult;
import vip.xiaonuo.gen.modular.basic.service.GenBasicService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.io.IOException;
import java.util.List;

/**
 * 代码生成基础控制器
 *
 * @author yubaoshan
 * @date 2022/10/25 22:33
 **/
@Api(tags = "代码生成基础控制器")
@ApiSupport(author = "SNOWY_TEAM", order = 1)
@RestController
@Validated
public class GenBasicController {

    @Resource
    private GenBasicService genBasicService;

    /**
     * 获取代码生成基础分页
     *
     * @author yubaoshan
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 1)
    @ApiOperation("获取代码生成基础分页")
    @GetMapping("/gen/basic/page")
    public CommonResult<Page<GenBasic>> page(GenBasicPageParam genBasicPageParam) {
        return CommonResult.data(genBasicService.page(genBasicPageParam));
    }

    /**
     * 添加代码生成基础
     *
     * @author yubaoshan
     * @date 2022/4/24 20:47
     */
    @ApiOperationSupport(order = 2)
    @ApiOperation("添加代码生成基础")
    @CommonLog("添加代码生成基础")
    @PostMapping("/gen/basic/add")
    public CommonResult<GenBasic> add(@RequestBody @Valid GenBasicAddParam genBasicAddParam) {
        return CommonResult.data(genBasicService.add(genBasicAddParam));
    }

    /**
     * 编辑代码生成基础
     *
     * @author yubaoshan
     * @date 2022/4/24 20:47
     */
    @ApiOperationSupport(order = 3)
    @ApiOperation("编辑代码生成基础")
    @CommonLog("编辑代码生成基础")
    @PostMapping("/gen/basic/edit")
    public CommonResult<GenBasic> edit(@RequestBody @Valid GenBasicEditParam genBasicEditParam) {
        return CommonResult.data(genBasicService.edit(genBasicEditParam));
    }

    /**
     * 删除代码生成基础
     *
     * @author yubaoshan
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 4)
    @ApiOperation("删除代码生成基础")
    @CommonLog("删除代码生成基础")
    @PostMapping("/gen/basic/delete")
    public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                               CommonValidList<GenBasicIdParam> genBasicIdParamList) {
        genBasicService.delete(genBasicIdParamList);
        return CommonResult.ok();
    }

    /**
     * 获取代码生成基础详情
     *
     * @author yubaoshan
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 5)
    @ApiOperation("获取代码生成基础详情")
    @GetMapping("/gen/basic/detail")
    public CommonResult<GenBasic> detail(@Valid GenBasicIdParam genBasicIdParam) {
        return CommonResult.data(genBasicService.detail(genBasicIdParam));
    }

    /**
     * 获取所有数据源信息
     *
     * @author yubaoshan
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 6)
    @ApiOperation("获取所有数据源信息")
    @GetMapping("/gen/basic/dbsSelector")
    public CommonResult<List<GenBasicDbsSelectorResult>> dbsSelector() {
        return CommonResult.data(genBasicService.dbsSelector());
    }

    /**
     * 根据数据源id获取对应库所有表信息
     *
     * @author yubaoshan
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 7)
    @ApiOperation("根据数据源id获取对应库所有表信息")
    @GetMapping("/gen/basic/tablesByDbsId")
    public CommonResult<List<GenBasicTableResult>> tablesByDbsId(@Valid GenBasicDbsTableParam genBasicDbsTableParam) {
        return CommonResult.data(genBasicService.tablesByDbsId(genBasicDbsTableParam));
    }

    /**
     * 根据数据源id获取对应库数据表内所有字段信息
     *
     * @author yubaoshan
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 8)
    @ApiOperation("根据数据源id获取对应库数据表内所有字段信息")
    @GetMapping("/gen/basic/tableColumnsByDbsId")
    public CommonResult<List<GenBasicTableColumnResult>> tableColumnsByDbsId(@Valid GenBasicDbsTableColumnParam dbsTableColumnParam) {
        return CommonResult.data(genBasicService.tableColumnsByDbsId(dbsTableColumnParam));
    }

    /**
     * 获取当前库所有表信息
     *
     * @author yubaoshan
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 9)
    @ApiOperation("获取当前库所有表信息")
    @GetMapping("/gen/basic/tables")
    public CommonResult<List<GenBasicTableResult>> tables() {
        return CommonResult.data(genBasicService.tables());
    }

    /**
     * 获取当前库数据表内所有字段信息
     *
     * @author yubaoshan
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 10)
    @ApiOperation("获取当前库数据表内所有字段信息")
    @GetMapping("/gen/basic/tableColumns")
    public CommonResult<List<GenBasicTableColumnResult>> tableColumns(@Valid GenBasicTableColumnParam genBasicTableColumnParam) {
        return CommonResult.data(genBasicService.tableColumns(genBasicTableColumnParam));
    }

    /**
     * 执行代码生成
     *
     * @author xuyuxiang
     * @date 2022/6/21 15:44
     **/
    @ApiOperationSupport(order = 11)
    @ApiOperation("获取可以生成的文件名")
    @GetMapping(value = "/gen/basic/genFileNames")
    public CommonResult<JSONObject> getGenFileNames() {
        return CommonResult.data(genBasicService.getGenFileNames());
    }

    /**
     * 执行代码生成
     *
     * @author xuyuxiang
     * @date 2022/6/21 15:44
     **/
    @ApiOperationSupport(order = 11)
    @ApiOperation("执行代码生成（压缩包）")
    @CommonLog("执行代码生成（压缩包）")
    @PostMapping(value = "/gen/basic/execGenZip")
    public void execGenZip(@RequestBody @Valid GenBasicFileParam genBasicFileParam, HttpServletResponse response) throws IOException {
        genBasicService.execGenZip(genBasicFileParam, response);
    }

    /**
     * 执行代码生成
     *
     * @author yubaoshan
     * @date 2022/10/31 02:17
     **/
    @ApiOperationSupport(order = 12)
    @ApiOperation("执行代码生成（项目内）")
    @CommonLog("执行代码生成（项目内）")
    @PostMapping(value = "/gen/basic/execGenPro")
    public CommonResult<String> execGenPro(@RequestBody @Valid GenBasicFileParam genBasicFileParam, HttpServletResponse response) throws IOException {
        genBasicService.execGenPro(genBasicFileParam, response);
        return CommonResult.ok();
    }

    /**
     * 预览代码生成
     *
     * @author xuyuxiang
     * @date 2022/6/21 15:44
     **/
    @ApiOperationSupport(order = 13)
    @ApiOperation("预览代码生成")
    @CommonLog("预览代码生成")
    @GetMapping(value = "/gen/basic/previewGen")
    public CommonResult<GenBasicPreviewResult> previewGen(@Valid GenBasicFileParam genBasicFileParam) {
        return CommonResult.data(genBasicService.previewGen(genBasicFileParam));
    }
}

