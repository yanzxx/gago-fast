package vip.xiaonuo.biz.modular.finproduct.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import vip.xiaonuo.biz.modular.finproduct.entity.BizFinProduct;
import vip.xiaonuo.biz.modular.finproduct.param.BizFinProductPageParam;
import vip.xiaonuo.biz.modular.finproduct.result.BizFinProductPageResult;

import java.util.List;

/**
 * 金融产品Mapper
 */
public interface BizFinProductMapper extends BaseMapper<BizFinProduct> {

    IPage<BizFinProductPageResult> page(Page<BizFinProductPageResult> page,
                                        @Param("param") BizFinProductPageParam param,
                                        @Param("allFarm") boolean allFarm,
                                        @Param("farmIds") List<String> farmIds);
}
