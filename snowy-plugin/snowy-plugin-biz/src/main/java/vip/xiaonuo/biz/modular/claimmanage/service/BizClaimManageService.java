package vip.xiaonuo.biz.modular.claimmanage.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import vip.xiaonuo.biz.modular.claimmanage.entity.BizClaimManage;
import vip.xiaonuo.biz.modular.claimmanage.param.BizClaimManageAddParam;
import vip.xiaonuo.biz.modular.claimmanage.param.BizClaimManageHandleParam;
import vip.xiaonuo.biz.modular.claimmanage.param.BizClaimManageIdParam;
import vip.xiaonuo.biz.modular.claimmanage.param.BizClaimManagePageParam;
import vip.xiaonuo.biz.modular.claimmanage.param.BizClaimManageSupplementParam;
import vip.xiaonuo.biz.modular.claimmanage.result.BizClaimManagePageResult;

/**
 * 理赔管理Service
 */
public interface BizClaimManageService {

    void add(BizClaimManageAddParam addParam);

    BizClaimManage detail(BizClaimManageIdParam idParam);

    Page<BizClaimManagePageResult> page(BizClaimManagePageParam pageParam);

    void supplement(BizClaimManageSupplementParam supplementParam);

    void handle(BizClaimManageHandleParam handleParam);

    String exportReport(BizClaimManageIdParam idParam);
}
