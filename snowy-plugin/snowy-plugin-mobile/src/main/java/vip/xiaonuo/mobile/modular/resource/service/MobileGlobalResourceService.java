package vip.xiaonuo.mobile.modular.resource.service;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import vip.xiaonuo.mobile.modular.resource.entity.MobileGlobalResource;
import vip.xiaonuo.mobile.modular.resource.param.resource.MobileGlobalResourceAddParam;
import vip.xiaonuo.mobile.modular.resource.param.resource.MobileGlobalResourceEditParam;
import vip.xiaonuo.mobile.modular.resource.param.resource.MobileGlobalResourceIdParam;
import vip.xiaonuo.mobile.modular.resource.param.resource.MobileGlobalResourcePageParam;

import java.util.List;

public interface MobileGlobalResourceService  extends IService<MobileGlobalResource> {

    /**
     * 获取移动端按钮分页
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:08
     */
    Page<MobileGlobalResource> page(MobileGlobalResourcePageParam mobileGlobalResourcePageParam);

    void add(MobileGlobalResourceAddParam mobileGlobalResourceAddParam);

    void edit(MobileGlobalResourceEditParam mobileGlobalResourceEditParam);

    MobileGlobalResource queryEntity(String id);

    void delete(List<MobileGlobalResourceIdParam> mobileGlobalResourceIdParamList);


    /**
     * 获取移动端全局权限授权树
     *
     * @return
     * @author wanglin
     * @date 2023/10/26 20:00
     */
    List<JSONObject> mobileGlobalResourceSelector();
}
