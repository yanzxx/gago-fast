
package vip.xiaonuo.sys.modular.resource.provider;

import org.springframework.stereotype.Service;
import vip.xiaonuo.sys.api.SysButtonApi;
import vip.xiaonuo.sys.modular.resource.service.SysButtonService;

import javax.annotation.Resource;

/**
 * 按钮API接口实现类
 *
 * @author xuyuxiang
 * @date 2022/11/1 13:50
 **/
@Service
public class SysButtonApiProvider implements SysButtonApi {

    @Resource
    private SysButtonService sysButtonService;

    @Override
    public void addForGenButton(String menuId, String className, String functionName) {
        sysButtonService.addForGenButton(menuId, className, functionName);
    }
}
