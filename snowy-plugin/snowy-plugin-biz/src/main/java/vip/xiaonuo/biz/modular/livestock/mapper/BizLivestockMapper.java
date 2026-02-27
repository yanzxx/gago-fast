package vip.xiaonuo.biz.modular.livestock.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import vip.xiaonuo.biz.modular.livestock.entity.BizLivestock;
import vip.xiaonuo.biz.modular.livestock.param.BizLivestockPageParam;
import vip.xiaonuo.biz.modular.livestock.result.BizLivestockPageResult;
import vip.xiaonuo.biz.modular.livestock.result.BizLivestockSpeciesOptionResult;

import java.util.List;

/**
 * 牲畜登记Mapper
 */
public interface BizLivestockMapper extends BaseMapper<BizLivestock> {

    /**
     * 分页
     */
    IPage<BizLivestockPageResult> page(Page<BizLivestockPageResult> page,
                                       @Param("param") BizLivestockPageParam param,
                                       @Param("allFarm") boolean allFarm,
                                       @Param("farmIds") List<String> farmIds);

    /**
     * 畜种选项
     */
    List<BizLivestockSpeciesOptionResult> speciesOptions(@Param("farmId") String farmId,
                                                         @Param("allFarm") boolean allFarm,
                                                         @Param("farmIds") List<String> farmIds);
}
