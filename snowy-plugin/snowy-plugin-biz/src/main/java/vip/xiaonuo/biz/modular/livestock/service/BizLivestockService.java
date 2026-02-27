package vip.xiaonuo.biz.modular.livestock.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import vip.xiaonuo.biz.modular.livestock.param.BizLivestockAddParam;
import vip.xiaonuo.biz.modular.livestock.param.BizLivestockEditParam;
import vip.xiaonuo.biz.modular.livestock.param.BizLivestockIdParam;
import vip.xiaonuo.biz.modular.livestock.param.BizLivestockPageParam;
import vip.xiaonuo.biz.modular.livestock.param.BizLivestockSpeciesOptionParam;
import vip.xiaonuo.biz.modular.livestock.entity.BizLivestock;
import vip.xiaonuo.biz.modular.livestock.result.BizLivestockPageResult;
import vip.xiaonuo.biz.modular.livestock.result.BizLivestockSpeciesOptionResult;

import java.util.List;

/**
 * 牲畜登记服务
 */
public interface BizLivestockService {

    /**
     * 新增登记
     */
    void add(BizLivestockAddParam addParam);

    /**
     * 编辑登记
     */
    void edit(BizLivestockEditParam editParam);

    /**
     * 详情
     */
    BizLivestock detail(BizLivestockIdParam idParam);

    /**
     * 分页
     */
    Page<BizLivestockPageResult> page(BizLivestockPageParam pageParam);

    /**
     * 畜种选项
     */
    List<BizLivestockSpeciesOptionResult> speciesOptions(BizLivestockSpeciesOptionParam optionParam);
}
