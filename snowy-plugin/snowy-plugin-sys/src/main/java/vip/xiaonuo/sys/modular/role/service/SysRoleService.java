
package vip.xiaonuo.sys.modular.role.service;

import cn.hutool.core.lang.tree.Tree;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import vip.xiaonuo.sys.entity.SysRole;
import vip.xiaonuo.sys.modular.role.param.*;
import vip.xiaonuo.sys.modular.role.result.*;
import vip.xiaonuo.sys.entity.SysUser;

import java.util.List;

/**
 * 角色Service接口
 *
 * @author xuyuxiang
 * @date 2022/4/21 18:35
 **/
public interface SysRoleService extends IService<SysRole> {

    /**
     * 获取角色分页
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:08
     */
    Page<SysRole> page(SysRolePageParam sysRolePageParam);

    /**
     * 添加角色
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:48
     */
    void add(SysRoleAddParam sysRoleAddParam);

    /**
     * 编辑角色
     *
     * @author xuyuxiang
     * @date 2022/4/24 21:13
     */
    void edit(SysRoleEditParam sysRoleEditParam);

    /**
     * 删除角色
     *
     * @author xuyuxiang
     * @date 2022/4/24 21:18
     */
    void delete(List<SysRoleIdParam> sysRoleIdParamList);

    /**
     * 获取角色详情
     *
     * @author xuyuxiang
     * @date 2022/4/24 21:18
     */
    SysRole detail(SysRoleIdParam sysRoleIdParam);

    /**
     * 获取角色详情
     *
     * @author xuyuxiang
     * @date 2022/7/25 19:42
     **/
    SysRole queryEntity(String id);

    /**
     * 获取角色拥有资源
     *
     * @author xuyuxiang
     * @date 2022/5/13 20:51
     */
    SysRoleOwnResourceResult ownResource(SysRoleIdParam sysRoleIdParam);

    /**
     * 给角色授权单页
     *
     * @author wanglin
     * @date 2023/8/3 20:51
     */
    void grantSpa(SysRoleGrantSpaParam sysRoleGrantSpaParam);


    /**
     * 给角色授权登录终端
     *
     * @author xuyuxiang
     * @date 2022/11/07 20:00
     */
    void grantTerminal(SysRoleGrantTerminalParam sysRoleGrantTerminalParam);


    /**
     * 给角色授权资源
     *
     * @author xuyuxiang
     * @date 2022/4/29 10:12
     **/
    void grantResource(SysRoleGrantResourceParam sysRoleGrantResourceParam);

    /**
     * 获取角色拥有移动端菜单
     *
     * @author xuyuxiang
     * @date 2022/5/13 20:51
     */
    SysRoleOwnMobileMenuResult ownMobileMenu(SysRoleIdParam sysRoleIdParam);

    /**
     * 获取角色拥有的移动端全局权限
     *
     * @author wanglin
     * @date 2023/10/26 20:00
     */
    SysRoleOwnMobileGlobalResourceTreeResult ownMobileGlobalResource(SysRoleIdParam sysRoleIdParam);

    /**
     * 获取角色拥有的终端登录权限
     *
     * @author wanglin
     * @date 2023/11/07 20:00
     */
    SysRoleOwnTerminalResult ownTerminal(SysRoleIdParam sysRoleIdParam);


    /**
     * 给角色授权移动端菜单
     *
     * @author xuyuxiang
     * @date 2022/4/29 10:12
     **/
    void grantMobileMenu(SysRoleGrantMobileMenuParam sysRoleGrantMobileMenuParam);

    /**
     * 获取角色拥有权限
     *
     * @author xuyuxiang
     * @date 2022/5/13 20:51
     */
    SysRoleOwnPermissionResult ownPermission(SysRoleIdParam SysRoleIdParam);

    /**
     * 给角色授权权限
     *
     * @author xuyuxiang
     * @date 2022/4/29 10:12
     **/
    void grantPermission(SysRoleGrantPermissionParam sysRoleGrantPermissionParam);

    /**
     * 获取角色下的用户
     *
     * @author xuyuxiang
     * @date 2022/8/22 13:56
     **/
    List<String> ownUser(SysRoleIdParam sysRoleIdParam);

    /**
     * 给角色授权用户
     *
     * @author xuyuxiang
     * @date 2022/4/29 10:12
     **/
    void grantUser(SysRoleGrantUserParam sysRoleGrantUserParam);

    /* ====角色部分所需要用到的选择器==== */

    /**
     * 获取组织树选择器
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:08
     */
    List<Tree<String>> orgTreeSelector();

    /**
     * 获取资源授权树
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:08
     */
    List<SysRoleGrantResourceTreeResult> resourceTreeSelector();

    /**
     * 获取单页授权树
     * @return
     */
    List<SysRoleGrantSpaResult> spaTreeSelector();

    /**
     * 获取登录终端授权树
     *
     * @author wanglin
     * @date 2023/11/07 20:00
     */
    List<SysRoleGrantTerminalResult> terminalSelector();

    /**
     * 获取移动端菜单授权树
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:08
     */
    List<SysRoleGrantMobileMenuTreeResult> mobileMenuTreeSelector();

    /**
     * 获取移动端全局权限授权数
     *
     * @author xuyuxiang
     * @date 2023/10/26 20:00
     */
    List<SysRoleGrantMobileGlobalResourceTreeResult> mobileGlobalResourceSelector();

    /**
     * 获取权限授权树
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:08
     */
    List<String> permissionTreeSelector();

    /**
     * 获取角色选择器
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:08
     */
    List<SysRole> roleSelector(SysRoleSelectorRoleParam sysRoleSelectorRoleParam);

    /**
     * 获取用户选择器
     *
     * @author xuyuxiang
     * @date 2022/8/22 13:39
     **/
    List<SysUser> userSelector(SysRoleSelectorUserParam sysRoleSelectorUserParam);

}
