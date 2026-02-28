package vip.xiaonuo.biz.modular.supervision.service;

import vip.xiaonuo.biz.modular.supervision.param.BizSupervisionHomeParam;
import vip.xiaonuo.biz.modular.supervision.result.BizSupervisionHomeResult;

/**
 * 综合监管服务
 */
public interface BizSupervisionService {

    /**
     * 首页大屏数据
     */
    BizSupervisionHomeResult home(BizSupervisionHomeParam homeParam);
}
