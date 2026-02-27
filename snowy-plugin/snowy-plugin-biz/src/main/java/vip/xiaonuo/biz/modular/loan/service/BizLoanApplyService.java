package vip.xiaonuo.biz.modular.loan.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import vip.xiaonuo.biz.modular.loan.entity.BizLoanApply;
import vip.xiaonuo.biz.modular.loan.param.BizLoanApplyAddParam;
import vip.xiaonuo.biz.modular.loan.param.BizLoanApplyEditParam;
import vip.xiaonuo.biz.modular.loan.param.BizLoanApplyIdParam;
import vip.xiaonuo.biz.modular.loan.param.BizLoanApplyPageParam;
import vip.xiaonuo.biz.modular.loan.result.BizLoanApplyPageResult;

/**
 * 贷款申请服务
 */
public interface BizLoanApplyService {

    void add(BizLoanApplyAddParam addParam);

    void edit(BizLoanApplyEditParam editParam);

    BizLoanApply detail(BizLoanApplyIdParam idParam);

    Page<BizLoanApplyPageResult> page(BizLoanApplyPageParam pageParam);

    void delete(BizLoanApplyIdParam idParam);
}
