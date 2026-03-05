package vip.xiaonuo.biz.modular.supervision.service;

import vip.xiaonuo.biz.modular.supervision.param.BizMobileHomeParam;
import vip.xiaonuo.biz.modular.supervision.result.BizMobileHomeHeaderResult;
import vip.xiaonuo.biz.modular.supervision.result.BizMobileHomeMetricsResult;
import vip.xiaonuo.biz.modular.supervision.result.BizMobileHomeMetricDetailResult;
import vip.xiaonuo.biz.modular.supervision.result.BizMobileHomeTodoItemResult;
import vip.xiaonuo.biz.modular.supervision.result.BizMobileHomeTodoSummaryResult;
import vip.xiaonuo.biz.modular.supervision.result.BizMobileHomeWeeklyStatResult;

import java.util.List;

/**
 * 移动端首页服务
 */
public interface BizMobileHomeService {

    BizMobileHomeHeaderResult header(BizMobileHomeParam param);

    BizMobileHomeMetricsResult metrics(BizMobileHomeParam param);

    List<BizMobileHomeTodoSummaryResult> todos(BizMobileHomeParam param, Integer topN);

    List<BizMobileHomeTodoItemResult> todoDetails(BizMobileHomeParam param, String todoType);

    List<BizMobileHomeWeeklyStatResult> weeklyStats(BizMobileHomeParam param);

    List<BizMobileHomeMetricDetailResult> metricDetails(BizMobileHomeParam param);
}
