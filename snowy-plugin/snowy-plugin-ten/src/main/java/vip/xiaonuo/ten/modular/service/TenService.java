
package vip.xiaonuo.ten.modular.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import vip.xiaonuo.ten.modular.entity.TenStorage;
import vip.xiaonuo.ten.modular.param.*;
import vip.xiaonuo.ten.modular.result.TenDbsSelectorResult;

import java.util.List;

/**
 * 多租户Service接口
 *
 * @author xuyuxiang
 * @date 2022/3/11 10:25
 **/
public interface TenService extends IService<TenStorage> {

    /**
     * 获取当前租户域名
     *
     * @author xuyuxiang
     * @date 2022/4/22 14:52
     **/
    String getCurrentTenDomain();

    /**
     * 获取当前租户
     *
     * @author xuyuxiang
     * @date 2022/7/18 18:31
     **/
    TenStorage getCurrentTen();

    /**
     * 获取租户分页
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:08
     */
    Page<TenStorage> page(TenStoragePageParam tenStoragePageParam);

    /**
     * 租户选择器
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:08
     */
    List<TenStorage> tenSelector(TenStorageSelectorParam tenStorageSelectorParam);

    /**
     * 添加租户
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:48
     */
    void add(TenStorageAddParam tenStorageAddParam);

    /**
     * 编辑租户
     *
     * @author xuyuxiang
     * @date 2022/4/24 21:13
     */
    void edit(TenStorageEditParam tenStorageEditParam);

    /**
     * 删除租户
     *
     * @author xuyuxiang
     * @date 2022/4/24 21:18
     */
    void delete(List<TenStorageIdParam> tenStorageIdParamList);

    /**
     * 获取租户详情
     *
     * @author xuyuxiang
     * @date 2022/4/24 21:18
     */
    TenStorage detail(TenStorageIdParam tenStorageIdParam);

    /**
     * 获取租户详情
     *
     * @author xuyuxiang
     * @date 2022/4/24 21:18
     */
    TenStorage queryEntity(String id);

    /* ====租户部分所需要用到的选择器==== */

    /**
     * 获取租户未被使用数据源列表
     *
     * @author yubaoshan
     * @date 2022/7/11 17:53
     */
    List<TenDbsSelectorResult> dbsList();
}
