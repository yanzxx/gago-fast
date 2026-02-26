package vip.xiaonuo.sys.modular.resource.service.impl;

import org.springframework.stereotype.Service;
import vip.xiaonuo.auth.core.enums.AuthTerminalTypeEnum;
import vip.xiaonuo.sys.entity.SysRelation;
import vip.xiaonuo.sys.modular.relation.enums.SysRelationCategoryEnum;
import vip.xiaonuo.sys.modular.relation.service.SysRelationService;
import vip.xiaonuo.sys.modular.resource.service.SysTerminalService;
import vip.xiaonuo.sys.entity.SysRole;
import vip.xiaonuo.sys.modular.role.service.SysRoleService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SysTerminalServiceImpl implements SysTerminalService {

    @Resource
    private SysRoleService sysRoleService;
    @Resource
    private SysRelationService sysRelationService;

    @Override
    public void initTerminal() {

        // 获取所有角色
        List<SysRole> sysRoles = sysRoleService.list();
        List<String> roleIds = sysRoles.stream().map(SysRole::getId).collect(Collectors.toList());
        // 过滤出没有分配终端的角色
        List<SysRelation> sysRelations = sysRelationService.getRelationListByObjectIdListAndCategory(roleIds, SysRelationCategoryEnum.SYS_ROLE_HAS_TERMINAL.getValue());
        Set<String> set = sysRelations.stream().map(SysRelation::getObjectId).collect(Collectors.toSet());
        List<String> notTerminalRoleIds = roleIds.stream().filter(roleId -> !set.contains(roleId)).collect(Collectors.toList());

        // 添加默认允许登录的终端  PC终端
        List<SysRelation> sysRelationList = new ArrayList<>();
        for (String notTerminalRoleId : notTerminalRoleIds) {
            SysRelation sysRelation = new SysRelation();
            sysRelation.setObjectId(notTerminalRoleId);
            sysRelation.setTargetId(AuthTerminalTypeEnum.PC.getValue());
            sysRelation.setCategory(SysRelationCategoryEnum.SYS_ROLE_HAS_TERMINAL.getValue());
            sysRelationList.add(sysRelation);
        }

        sysRelationService.saveBatch(sysRelationList);
    }
}
