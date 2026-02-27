package vip.xiaonuo.biz.modular.finproduct.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import vip.xiaonuo.biz.modular.finproduct.entity.BizFinProduct;
import vip.xiaonuo.biz.modular.finproduct.param.BizFinProductAddParam;
import vip.xiaonuo.biz.modular.finproduct.param.BizFinProductEditParam;
import vip.xiaonuo.biz.modular.finproduct.param.BizFinProductIdParam;
import vip.xiaonuo.biz.modular.finproduct.param.BizFinProductPageParam;
import vip.xiaonuo.biz.modular.finproduct.result.BizFinProductPageResult;

import java.util.List;

/**
 * 金融产品服务
 */
public interface BizFinProductService {

    void add(BizFinProductAddParam addParam);

    void edit(BizFinProductEditParam editParam);

    BizFinProduct detail(BizFinProductIdParam idParam);

    Page<BizFinProductPageResult> page(BizFinProductPageParam pageParam);

    void onShelf(BizFinProductIdParam idParam);

    void offShelf(BizFinProductIdParam idParam);

    void delete(BizFinProductIdParam idParam);

    void batchDelete(List<BizFinProductIdParam> idParamList);
}
