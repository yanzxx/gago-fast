
package vip.xiaonuo.sys.modular.role.controller;

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
import vip.xiaonuo.common.annotation.CommonLog;
import vip.xiaonuo.common.pojo.CommonResult;
import vip.xiaonuo.common.pojo.CommonValidList;
import vip.xiaonuo.sys.entity.SysRole;
import vip.xiaonuo.sys.modular.role.param.*;
import vip.xiaonuo.sys.modular.role.result.*;
import vip.xiaonuo.sys.modular.role.service.SysRoleService;
import vip.xiaonuo.sys.entity.SysUser;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 角色控制器
 *
 * @author xuyuxiang
 * @date 2022/4/25 20:19
 */
@Api(tags = "角色控制器")
@ApiSupport(author = "SNOWY_TEAM", order = 8)
@RestController
@Validated
public class SysRoleController {

    @Resource
    private SysRoleService sysRoleService;

    /**
     * 获取角色分页
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 1)
    @ApiOperation("获取角色分页")
    @GetMapping("/sys/role/page")
    public CommonResult<Page<SysRole>> page(SysRolePageParam sysRolePageParam) {
        return CommonResult.data(sysRoleService.page(sysRolePageParam));
    }

    /**
     * 添加角色
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:47
     */
    @ApiOperationSupport(order = 2)
    @ApiOperation("添加角色")
    @CommonLog("添加角色")
    @PostMapping("/sys/role/add")
    public CommonResult<String> add(@RequestBody @Valid SysRoleAddParam sysRoleAddParam) {
        sysRoleService.add(sysRoleAddParam);
        return CommonResult.ok();
    }

    /**
     * 编辑角色
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:47
     */
    @ApiOperationSupport(order = 3)
    @ApiOperation("编辑角色")
    @CommonLog("编辑角色")
    @PostMapping("/sys/role/edit")
    public CommonResult<String> edit(@RequestBody @Valid SysRoleEditParam sysRoleEditParam) {
        sysRoleService.edit(sysRoleEditParam);
        return CommonResult.ok();
    }

    /**
     * 删除角色
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 4)
    @ApiOperation("删除角色")
    @CommonLog("删除角色")
    @PostMapping("/sys/role/delete")
    public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                                   CommonValidList<SysRoleIdParam> sysRoleIdParamList) {
        sysRoleService.delete(sysRoleIdParamList);
        return CommonResult.ok();
    }

    /**
     * 获取角色详情
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 5)
    @ApiOperation("获取角色详情")
    @GetMapping("/sys/role/detail")
    public CommonResult<SysRole> detail(@Valid SysRoleIdParam sysRoleIdParam) {
        return CommonResult.data(sysRoleService.detail(sysRoleIdParam));
    }

    /**
     * 给角色授权单页
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 6)
    @ApiOperation("给角色授权单页")
    @PostMapping("/sys/role/grantSpa")
    public CommonResult<String> grantSpa(@RequestBody @Valid SysRoleGrantSpaParam sysRoleGrantSpaParam) {
        sysRoleService.grantSpa(sysRoleGrantSpaParam);
        return CommonResult.ok();
    }


    /**
     * 给角色授权登录终端
     *
     * @author xuyuxiang
     * @date 2022/11/07 20:00
     */
    @ApiOperationSupport(order = 6)
    @ApiOperation("给角色授权登录终端")
    @PostMapping("/sys/role/grantTerminal")
    public CommonResult<String> grantTerminal(@RequestBody @Valid SysRoleGrantTerminalParam sysRoleGrantTerminalParam) {
        sysRoleService.grantTerminal(sysRoleGrantTerminalParam);
        return CommonResult.ok();
    }


    /**
     * 获取角色拥有资源
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 6)
    @ApiOperation("获取角色拥有资源")
    @GetMapping("/sys/role/ownResource")
    public CommonResult<SysRoleOwnResourceResult> ownResource(@Valid SysRoleIdParam sysRoleIdParam) {
        return CommonResult.data(sysRoleService.ownResource(sysRoleIdParam));
    }

    /**
     * 给角色授权资源
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 7)
    @ApiOperation("给角色授权资源")
    @CommonLog("给角色授权资源")
    @PostMapping("/sys/role/grantResource")
    public CommonResult<String> grantResource(@RequestBody @Valid SysRoleGrantResourceParam sysRoleGrantResourceParam) {
        sysRoleService.grantResource(sysRoleGrantResourceParam);
        return CommonResult.ok();
    }

    /**
     * 获取角色拥有移动端菜单
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 8)
    @ApiOperation("获取角色拥有移动端菜单")
    @GetMapping("/sys/role/ownMobileMenu")
    public CommonResult<SysRoleOwnMobileMenuResult> ownMobileMenu(@Valid SysRoleIdParam sysRoleIdParam) {
        return CommonResult.data(sysRoleService.ownMobileMenu(sysRoleIdParam));
    }

    /**
     * 获取角色拥有的移动端全局权限
     *
     * @author wanglin
     * @date 2023/11/26 20:00
     */
    @ApiOperationSupport(order = 8)
    @ApiOperation("获取角色拥有的移动端全局权限")
    @GetMapping("/sys/role/ownMobileGlobalResource")
    public CommonResult<SysRoleOwnMobileGlobalResourceTreeResult> ownMobileGlobalResource(@Valid SysRoleIdParam sysRoleIdParam) {
        return CommonResult.data(sysRoleService.ownMobileGlobalResource(sysRoleIdParam));
    }

    /**
     * 获取角色拥有的终端登录权限
     *
     * @author wanglin
     * @date 2023/11/07 20:00
     */
    @ApiOperationSupport(order = 8)
    @ApiOperation("获取角色拥有的终端登录权限")
    @GetMapping("/sys/role/ownTerminal")
    public CommonResult<SysRoleOwnTerminalResult> ownTerminal(@Valid SysRoleIdParam sysRoleIdParam) {
        return CommonResult.data(sysRoleService.ownTerminal(sysRoleIdParam));
    }

    /**
     * 给角色授权移动端菜单
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 9)
    @ApiOperation("给角色授权移动端菜单")
    @CommonLog("给角色授权移动端菜单")
    @PostMapping("/sys/role/grantMobileMenu")
    public CommonResult<String> grantMobileMenu(@RequestBody @Valid SysRoleGrantMobileMenuParam sysRoleGrantMobileMenuParam) {
        sysRoleService.grantMobileMenu(sysRoleGrantMobileMenuParam);
        return CommonResult.ok();
    }

    /**
     * 获取角色拥有权限
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 10)
    @ApiOperation("获取角色拥有权限")
    @GetMapping("/sys/role/ownPermission")
    public CommonResult<SysRoleOwnPermissionResult> ownPermission(@Valid SysRoleIdParam sysRoleIdParam) {
        return CommonResult.data(sysRoleService.ownPermission(sysRoleIdParam));
    }

    /**
     * 给角色授权权限
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 11)
    @ApiOperation("给角色授权权限")
    @CommonLog("给角色授权权限")
    @PostMapping("/sys/role/grantPermission")
    public CommonResult<String> grantPermission(@RequestBody @Valid SysRoleGrantPermissionParam sysRoleGrantPermissionParam) {
        sysRoleService.grantPermission(sysRoleGrantPermissionParam);
        return CommonResult.ok();
    }

    /**
     * 获取角色下的用户
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 12)
    @ApiOperation("获取角色下的用户")
    @GetMapping("/sys/role/ownUser")
    public CommonResult<List<String>> ownUser(@Valid SysRoleIdParam sysRoleIdParam) {
        return CommonResult.data(sysRoleService.ownUser(sysRoleIdParam));
    }

    /**
     * 给角色授权用户
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 13)
    @ApiOperation("给角色授权用户")
    @CommonLog("给角色授权用户")
    @PostMapping("/sys/role/grantUser")
    public CommonResult<String> grantUser(@RequestBody @Valid SysRoleGrantUserParam sysRoleGrantUserParam) {
        sysRoleService.grantUser(sysRoleGrantUserParam);
        return CommonResult.ok();
    }

    /* ====角色部分所需要用到的选择器==== */

    /**
     * 获取组织树选择器
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 14)
    @ApiOperation("获取组织树选择器")
    @GetMapping("/sys/role/orgTreeSelector")
    public CommonResult<List<Tree<String>>> orgTreeSelector() {
        return CommonResult.data(sysRoleService.orgTreeSelector());
    }

    /**
     * 获取资源授权树
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 15)
    @ApiOperation("获取单页授权树")
    @GetMapping("/sys/role/spaTreeSelector")
    public CommonResult<List<SysRoleGrantSpaResult>> spaTreeSelector() {
        return CommonResult.data(sysRoleService.spaTreeSelector());
    }

    /**
     * 获取登录终端授权树
     *
     * @author wanglin
     * @date 2023/11/07 20:00
     */
    @ApiOperationSupport(order = 15)
    @ApiOperation("获取登录终端授权树")
    @GetMapping("/sys/role/terminalSelector")
    public CommonResult<List<SysRoleGrantTerminalResult>> terminalSelector() {
        return CommonResult.data(sysRoleService.terminalSelector());
    }

    /**
     * 获取资源授权树
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 15)
    @ApiOperation("获取资源授权树")
    @GetMapping("/sys/role/resourceTreeSelector")
    public CommonResult<List<SysRoleGrantResourceTreeResult>> resourceTreeSelector() {
        return CommonResult.data(sysRoleService.resourceTreeSelector());
    }

    /**
     * 获取移动端菜单授权树
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 16)
    @ApiOperation("获取移动端菜单授权树")
    @GetMapping("/sys/role/mobileMenuTreeSelector")
    public CommonResult<List<SysRoleGrantMobileMenuTreeResult>> mobileMenuTreeSelector() {
        return CommonResult.data(sysRoleService.mobileMenuTreeSelector());
    }

    /**
     * 获取移动端全局权限授权数
     *
     * @author wanglin
     * @date 2023/10/26 20:00
     */
    @ApiOperationSupport(order = 17)
    @ApiOperation("获取移动端全局权限授权树")
    @GetMapping("/sys/role/mobileGlobalResourceSelector")
    public CommonResult<List<SysRoleGrantMobileGlobalResourceTreeResult>> mobileGlobalResourceSelector() {
        return CommonResult.data(sysRoleService.mobileGlobalResourceSelector());
    }

    /**
     * 获取权限授权树
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 17)
    @ApiOperation("获取权限授权树")
    @GetMapping("/sys/role/permissionTreeSelector")
    public CommonResult<List<String>> permissionTreeSelector() {
        return CommonResult.data(sysRoleService.permissionTreeSelector());
    }

    /**
     * 获取角色选择器
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 18)
    @ApiOperation("获取角色选择器")
    @GetMapping("/sys/role/roleSelector")
    public CommonResult<List<SysRole>> roleSelector(SysRoleSelectorRoleParam sysRoleSelectorRoleParam) {
        return CommonResult.data(sysRoleService.roleSelector(sysRoleSelectorRoleParam));
    }

    /**
     * 获取用户选择器
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 19)
    @ApiOperation("获取用户选择器")
    @GetMapping("/sys/role/userSelector")
    public CommonResult<List<SysUser>> userSelector(SysRoleSelectorUserParam sysRoleSelectorUserParam) {
        return CommonResult.data(sysRoleService.userSelector(sysRoleSelectorUserParam));
    }
}
