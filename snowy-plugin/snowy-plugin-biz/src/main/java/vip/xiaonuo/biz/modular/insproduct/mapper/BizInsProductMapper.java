package vip.xiaonuo.biz.modular.insproduct.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import vip.xiaonuo.biz.modular.insproduct.entity.BizInsProduct;
import vip.xiaonuo.biz.modular.insproduct.param.BizInsProductPageParam;
import vip.xiaonuo.biz.modular.insproduct.result.BizInsProductPageResult;

import java.util.List;

/**
 * 保险产品Mapper
 */
public interface BizInsProductMapper extends BaseMapper<BizInsProduct> {

    IPage<BizInsProductPageResult> page(Page<BizInsProductPageResult> page,
                                        @Param("param") BizInsProductPageParam param,
                                        @Param("allFarm") boolean allFarm,
                                        @Param("farmIds") List<String> farmIds);
}
