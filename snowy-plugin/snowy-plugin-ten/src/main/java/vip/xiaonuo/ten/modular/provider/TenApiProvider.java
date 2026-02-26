
package vip.xiaonuo.ten.modular.provider;

import org.springframework.stereotype.Service;
import vip.xiaonuo.ten.api.TenApi;
import vip.xiaonuo.ten.core.context.TenContextHolder;
import vip.xiaonuo.ten.core.prop.TenProperties;
import vip.xiaonuo.ten.modular.service.TenService;

import javax.annotation.Resource;

/**
 * 多租户API接口提供者
 *
 * @author xuyuxiang
 * @date 2022/3/10 18:57
 **/
@Service
public class TenApiProvider implements TenApi {

    @Resource
    private TenProperties tenProperties;

    @Resource
    private TenService tenService;

    @Override
    public boolean getTenEnabled() {
        return tenProperties.getEnabled();
    }

    @Override
    public String getDefaultTenColumnName() {
        return tenProperties.getTenIdColumnName();
    }

    @Override
    public String getDefaultTenId() {
        return tenProperties.getDefaultTenId();
    }

    @Override
    public String getCurrentTenId() {
        return TenContextHolder.get();
    }

    @Override
    public String getCurrentTenDomain() {
        return tenService.getCurrentTenDomain();
    }
}
