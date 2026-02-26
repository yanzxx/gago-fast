
package vip.xiaonuo.sys.modular.org.provider;

import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.stereotype.Service;
import vip.xiaonuo.sys.api.SysOrgApi;
import vip.xiaonuo.sys.entity.SysOrg;
import vip.xiaonuo.sys.modular.org.param.SysOrgSelectorOrgListParam;
import vip.xiaonuo.sys.modular.org.service.SysOrgService;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 组织API接口提供者
 *
 * @author xuyuxiang
 * @date 2022/7/22 14:56
 **/
@Service
public class SysOrgApiProvider implements SysOrgApi {

    @Resource
    private SysOrgService sysOrgService;

    @Override
    public String getNameById(String orgId) {
        return sysOrgService.queryEntity(orgId).getName();
    }

    @Override
    public String getSupervisorIdByOrgId(String orgId) {
        SysOrg sysOrg = sysOrgService.getById(orgId);
        if(ObjectUtil.isNotEmpty(sysOrg)) {
            return sysOrg.getDirectorId();
        }
        return null;
    }

    @Override
    public List<Tree<String>> orgTreeSelector() {
        return sysOrgService.orgTreeSelector();
    }

    @Override
    public List<JSONObject> orgListSelector(String parentId) {
        SysOrgSelectorOrgListParam sysOrgSelectorOrgListParam = new SysOrgSelectorOrgListParam();
        sysOrgSelectorOrgListParam.setParentId(parentId);
        return sysOrgService.orgListSelector(sysOrgSelectorOrgListParam).stream().map(JSONUtil::parseObj).collect(Collectors.toList());
    }
}
