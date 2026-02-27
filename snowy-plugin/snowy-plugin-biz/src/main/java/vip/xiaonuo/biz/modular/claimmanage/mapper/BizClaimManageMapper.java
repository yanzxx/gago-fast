package vip.xiaonuo.biz.modular.claimmanage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import vip.xiaonuo.biz.modular.claimmanage.entity.BizClaimManage;
import vip.xiaonuo.biz.modular.claimmanage.param.BizClaimManagePageParam;
import vip.xiaonuo.biz.modular.claimmanage.result.BizClaimManagePageResult;

import java.util.List;

/**
 * 理赔管理Mapper
 */
public interface BizClaimManageMapper extends BaseMapper<BizClaimManage> {

    IPage<BizClaimManagePageResult> page(Page<BizClaimManagePageResult> page,
                                         @Param("param") BizClaimManagePageParam param,
                                         @Param("allFarm") boolean allFarm,
                                         @Param("farmIds") List<String> farmIds);
}
