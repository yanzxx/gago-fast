
package vip.xiaonuo.sys.modular.position.provider;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.stereotype.Service;
import vip.xiaonuo.sys.api.SysPositionApi;
import vip.xiaonuo.sys.modular.position.param.SysPositionSelectorPositionParam;
import vip.xiaonuo.sys.modular.position.service.SysPositionService;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 职位API接口提供者
 *
 * @author xuyuxiang
 * @date 2022/7/22 14:56
 **/
@Service
public class SysPositionApiProvider implements SysPositionApi {

    @Resource
    private SysPositionService sysPositionService;

    @Override
    public String getNameById(String positionId) {
        return sysPositionService.queryEntity(positionId).getName();
    }

    @Override
    public List<JSONObject> positionSelector(String orgId, String searchKey) {
        SysPositionSelectorPositionParam sysPositionSelectorPositionParam = new SysPositionSelectorPositionParam();
        sysPositionSelectorPositionParam.setOrgId(orgId);
        sysPositionSelectorPositionParam.setSearchKey(searchKey);
        return sysPositionService.positionSelector(sysPositionSelectorPositionParam).stream().map(JSONUtil::parseObj).collect(Collectors.toList());
    }
}
