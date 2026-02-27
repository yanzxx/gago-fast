package vip.xiaonuo.biz.modular.insapply.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import vip.xiaonuo.biz.modular.insapply.entity.BizInsApply;
import vip.xiaonuo.biz.modular.insapply.param.BizInsApplyPageParam;
import vip.xiaonuo.biz.modular.insapply.result.BizInsApplyPageResult;

import java.util.List;

/**
 * 投保记录Mapper
 */
public interface BizInsApplyMapper extends BaseMapper<BizInsApply> {

    IPage<BizInsApplyPageResult> page(Page<BizInsApplyPageResult> page,
                                      @Param("param") BizInsApplyPageParam param,
                                      @Param("allFarm") boolean allFarm,
                                      @Param("farmIds") List<String> farmIds);
}
