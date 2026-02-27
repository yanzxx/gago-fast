package vip.xiaonuo.biz.modular.afterloan.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import vip.xiaonuo.biz.modular.afterloan.param.BizAfterLoanAnomalyPageParam;
import vip.xiaonuo.biz.modular.afterloan.param.BizAfterLoanSendNoticeParam;
import vip.xiaonuo.biz.modular.afterloan.param.BizAfterLoanStatsParam;
import vip.xiaonuo.biz.modular.afterloan.result.BizAfterLoanAnomalyPageResult;
import vip.xiaonuo.biz.modular.afterloan.result.BizAfterLoanStatsResult;

/**
 * 贷后管理服务
 */
public interface BizAfterLoanAdministrationService {

    BizAfterLoanStatsResult stats(BizAfterLoanStatsParam statsParam);

    Page<BizAfterLoanAnomalyPageResult> anomalyPage(BizAfterLoanAnomalyPageParam pageParam);

    void sendRectifyNotice(BizAfterLoanSendNoticeParam sendNoticeParam);
}
