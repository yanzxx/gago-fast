package vip.xiaonuo.biz.modular.insproduct.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import vip.xiaonuo.biz.modular.insproduct.entity.BizInsProduct;
import vip.xiaonuo.biz.modular.insproduct.param.BizInsProductAddParam;
import vip.xiaonuo.biz.modular.insproduct.param.BizInsProductEditParam;
import vip.xiaonuo.biz.modular.insproduct.param.BizInsProductIdParam;
import vip.xiaonuo.biz.modular.insproduct.param.BizInsProductPageParam;
import vip.xiaonuo.biz.modular.insproduct.result.BizInsProductPageResult;

/**
 * 保险产品服务
 */
public interface BizInsProductService {

    void add(BizInsProductAddParam addParam);

    void edit(BizInsProductEditParam editParam);

    BizInsProduct detail(BizInsProductIdParam idParam);

    Page<BizInsProductPageResult> page(BizInsProductPageParam pageParam);

    void onShelf(BizInsProductIdParam idParam);

    void offShelf(BizInsProductIdParam idParam);

    void delete(BizInsProductIdParam idParam);
}
