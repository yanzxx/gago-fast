package vip.xiaonuo.biz.modular.insapply.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import vip.xiaonuo.biz.modular.insapply.entity.BizInsApply;
import vip.xiaonuo.biz.modular.insapply.param.BizInsApplyAddParam;
import vip.xiaonuo.biz.modular.insapply.param.BizInsApplyEditParam;
import vip.xiaonuo.biz.modular.insapply.param.BizInsApplyIdParam;
import vip.xiaonuo.biz.modular.insapply.param.BizInsApplyPageParam;
import vip.xiaonuo.biz.modular.insapply.result.BizInsApplyPageResult;

/**
 * 投保记录服务
 */
public interface BizInsApplyService {

    void add(BizInsApplyAddParam addParam);

    void edit(BizInsApplyEditParam editParam);

    BizInsApply detail(BizInsApplyIdParam idParam);

    Page<BizInsApplyPageResult> page(BizInsApplyPageParam pageParam);

    void delete(BizInsApplyIdParam idParam);
}
