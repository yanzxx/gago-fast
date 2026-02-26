package vip.xiaonuo.mobile.modular.resource.controller;


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
import vip.xiaonuo.mobile.modular.resource.entity.MobileGlobalResource;
import vip.xiaonuo.mobile.modular.resource.param.resource.MobileGlobalResourceAddParam;
import vip.xiaonuo.mobile.modular.resource.param.resource.MobileGlobalResourceEditParam;
import vip.xiaonuo.mobile.modular.resource.param.resource.MobileGlobalResourceIdParam;
import vip.xiaonuo.mobile.modular.resource.param.resource.MobileGlobalResourcePageParam;
import vip.xiaonuo.mobile.modular.resource.service.MobileGlobalResourceService;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * 全局资源控制器
 *
 * @author wanglin
 * @date 2023/10/26 13:56
 **/
@Api(tags = "全局资源控制器")
@ApiSupport(author = "SNOWY_TEAM", order = 7)
@RestController
@Validated
public class MobileGlobalResourceController {

    @Resource
    private MobileGlobalResourceService mobileGlobalResourceService;

    /**
     * 获取移动端按钮
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 1)
    @ApiOperation("获取移动端全局资源")
    @GetMapping("/mobile/global-resource/page")
    public CommonResult<Page<MobileGlobalResource>> page(MobileGlobalResourcePageParam mobileGlobalResourcePageParam) {
        return CommonResult.data(mobileGlobalResourceService.page(mobileGlobalResourcePageParam));
    }


    /**
     * 添加移动端按钮
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:47
     */
    @ApiOperationSupport(order = 2)
    @ApiOperation("添加移动端全局资源")
    @CommonLog("添加移动端全局资源")
    @PostMapping("/mobile/global-resource/add")
    public CommonResult<String> add(@RequestBody @Valid MobileGlobalResourceAddParam mobileGlobalResourceAddParam) {
        mobileGlobalResourceService.add(mobileGlobalResourceAddParam);
        return CommonResult.ok();
    }

    /**
     * 编辑移动端按钮
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:47
     */
    @ApiOperationSupport(order = 3)
    @ApiOperation("编辑移动端全局资源")
    @CommonLog("编辑移动端全局资源")
    @PostMapping("/mobile/global-resource/edit")
    public CommonResult<String> edit(@RequestBody @Valid MobileGlobalResourceEditParam mobileGlobalResourceEditParam) {
        mobileGlobalResourceService.edit(mobileGlobalResourceEditParam);
        return CommonResult.ok();
    }

    /**
     * 删除移动端按钮
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 4)
    @ApiOperation("删除移动端全局资源")
    @CommonLog("删除移动端全局资源")
    @PostMapping("/mobile/global-resource/delete")
    public CommonResult<String> delete(@RequestBody @Valid List<MobileGlobalResourceIdParam> mobileGlobalResourceIdParamList) {
        mobileGlobalResourceService.delete(mobileGlobalResourceIdParamList);
        return CommonResult.ok();
    }











}
