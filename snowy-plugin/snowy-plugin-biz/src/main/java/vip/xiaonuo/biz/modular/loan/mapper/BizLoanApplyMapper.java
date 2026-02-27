package vip.xiaonuo.biz.modular.loan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import vip.xiaonuo.biz.modular.loan.entity.BizLoanApply;
import vip.xiaonuo.biz.modular.loan.param.BizLoanApplyPageParam;
import vip.xiaonuo.biz.modular.loan.result.BizLoanApplyPageResult;

import java.util.List;

/**
 * 贷款申请Mapper
 */
public interface BizLoanApplyMapper extends BaseMapper<BizLoanApply> {

    IPage<BizLoanApplyPageResult> page(Page<BizLoanApplyPageResult> page,
                                       @Param("param") BizLoanApplyPageParam param,
                                       @Param("allFarm") boolean allFarm,
                                       @Param("farmIds") List<String> farmIds);
}
