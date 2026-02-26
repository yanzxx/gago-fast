package vip.xiaonuo.mobile.modular.resource.provider;

import cn.hutool.json.JSONObject;
import org.springframework.stereotype.Service;
import vip.xiaonuo.mobile.api.MobileGlobalResourceApi;
import vip.xiaonuo.mobile.modular.resource.entity.MobileGlobalResource;
import vip.xiaonuo.mobile.modular.resource.service.MobileGlobalResourceService;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class MobileGlobalResourceApiProvider implements MobileGlobalResourceApi {

    @Resource
    private MobileGlobalResourceService mobileGlobalResourceService;


    @Override
    public List<JSONObject> mobileGlobalResourceSelector() {
        return mobileGlobalResourceService.mobileGlobalResourceSelector();
    }

    @Override
    public List<String> getCodeListByIds(List<String> mobileGlobalResourceIds) {
        return mobileGlobalResourceService.listByIds(mobileGlobalResourceIds).stream().map(MobileGlobalResource::getCode).collect(Collectors.toList());
    }
}
