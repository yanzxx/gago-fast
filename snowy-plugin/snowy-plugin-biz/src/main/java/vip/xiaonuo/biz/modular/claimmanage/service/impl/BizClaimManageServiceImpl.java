package vip.xiaonuo.biz.modular.claimmanage.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vip.xiaonuo.auth.core.pojo.SaBaseLoginUser;
import vip.xiaonuo.auth.core.util.StpLoginUserUtil;
import vip.xiaonuo.biz.modular.claimmanage.entity.BizClaimManage;
import vip.xiaonuo.biz.modular.claimmanage.mapper.BizClaimManageMapper;
import vip.xiaonuo.biz.modular.claimmanage.param.BizClaimManageAddParam;
import vip.xiaonuo.biz.modular.claimmanage.param.BizClaimManageHandleParam;
import vip.xiaonuo.biz.modular.claimmanage.param.BizClaimManageIdParam;
import vip.xiaonuo.biz.modular.claimmanage.param.BizClaimManagePageParam;
import vip.xiaonuo.biz.modular.claimmanage.param.BizClaimManageSupplementParam;
import vip.xiaonuo.biz.modular.claimmanage.result.BizClaimManagePageResult;
import vip.xiaonuo.biz.modular.claimmanage.service.BizClaimManageService;
import vip.xiaonuo.common.exception.CommonException;
import vip.xiaonuo.common.page.CommonPageRequest;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 理赔管理Service实现
 */
@Service
public class BizClaimManageServiceImpl implements BizClaimManageService {

    private static final String STATUS_PENDING = "PENDING";
    private static final String STATUS_PROCESSING = "PROCESSING";
    private static final String STATUS_CLOSED = "CLOSED";
    private static final String STATUS_REJECTED = "REJECTED";
    private static final String DELETE_FLAG_DELETE = "DELETE";

    @Resource
    private BizClaimManageMapper bizClaimManageMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(BizClaimManageAddParam addParam) {
        boolean allFarm = isAllFarmVisible();
        List<String> visibleFarmIds = getVisibleFarmIds(allFarm);
        String farmId = resolveFarmId(addParam.getFarmId(), allFarm, visibleFarmIds);

        if (bizClaimManageMapper.selectCount(new LambdaQueryWrapper<BizClaimManage>()
                .eq(BizClaimManage::getClaimNo, addParam.getClaimNo())) > 0) {
            throw new CommonException("存在重复的理赔单号：{}", addParam.getClaimNo());
        }

        SaBaseLoginUser loginUser = StpLoginUserUtil.getLoginUser();
        BizClaimManage entity = BeanUtil.toBean(addParam, BizClaimManage.class);
        entity.setId(IdUtil.fastSimpleUUID());
        entity.setTenantId(ObjectUtil.isNotEmpty(loginUser) ? loginUser.getTenantId() : null);
        entity.setFarmId(farmId);
        entity.setStatus(STATUS_PENDING);
        bizClaimManageMapper.insert(entity);
    }

    @Override
    public BizClaimManage detail(BizClaimManageIdParam idParam) {
        boolean allFarm = isAllFarmVisible();
        List<String> visibleFarmIds = getVisibleFarmIds(allFarm);
        return queryEntityByIdOrNo(idParam.getId(), idParam.getClaimNo(), allFarm, visibleFarmIds);
    }

    @Override
    public Page<BizClaimManagePageResult> page(BizClaimManagePageParam pageParam) {
        boolean allFarm = isAllFarmVisible();
        List<String> visibleFarmIds = getVisibleFarmIds(allFarm);
        if (!allFarm && StrUtil.isNotBlank(pageParam.getFarmId()) && !visibleFarmIds.contains(pageParam.getFarmId())) {
            return new Page<>();
        }
        return (Page<BizClaimManagePageResult>) bizClaimManageMapper.page(CommonPageRequest.defaultPage(), pageParam, allFarm, visibleFarmIds);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void supplement(BizClaimManageSupplementParam supplementParam) {
        boolean allFarm = isAllFarmVisible();
        List<String> visibleFarmIds = getVisibleFarmIds(allFarm);
        BizClaimManage entity = queryEntityByIdOrNo(supplementParam.getId(), supplementParam.getClaimNo(), allFarm, visibleFarmIds);
        if (isClosed(entity.getStatus())) {
            throw new CommonException("已结案或已拒赔记录不允许补充材料");
        }
        entity.setEvidenceFiles(mergeFiles(entity.getEvidenceFiles(), supplementParam.getFiles()));
        entity.setRemark(mergeRemark(entity.getRemark(), supplementParam.getRemark()));
        if (STATUS_PENDING.equals(entity.getStatus())) {
            entity.setStatus(STATUS_PROCESSING);
        }
        bizClaimManageMapper.updateById(entity);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void handle(BizClaimManageHandleParam handleParam) {
        if (!CollUtil.newArrayList(STATUS_PROCESSING, STATUS_CLOSED, STATUS_REJECTED).contains(handleParam.getStatus())) {
            throw new CommonException("非法的处理状态：{}", handleParam.getStatus());
        }
        boolean allFarm = isAllFarmVisible();
        List<String> visibleFarmIds = getVisibleFarmIds(allFarm);
        BizClaimManage entity = queryEntityByIdOrNo(handleParam.getId(), handleParam.getClaimNo(), allFarm, visibleFarmIds);
        if (isClosed(entity.getStatus())) {
            throw new CommonException("已结案或已拒赔记录不允许再次处理");
        }
        entity.setStatus(handleParam.getStatus());
        entity.setResultRemark(handleParam.getResultRemark());
        bizClaimManageMapper.updateById(entity);
    }

    @Override
    public String exportReport(BizClaimManageIdParam idParam) {
        boolean allFarm = isAllFarmVisible();
        List<String> visibleFarmIds = getVisibleFarmIds(allFarm);
        BizClaimManage entity = queryEntityByIdOrNo(idParam.getId(), idParam.getClaimNo(), allFarm, visibleFarmIds);
        return String.format("理赔报告-%s.docx", entity.getClaimNo());
    }

    private boolean isAllFarmVisible() {
        SaBaseLoginUser loginUser = StpLoginUserUtil.getLoginUser();
        return ObjectUtil.isNotEmpty(loginUser) && CollUtil.contains(loginUser.getRoleCodeList(), "superAdmin");
    }

    private List<String> getVisibleFarmIds(boolean allFarm) {
        if (allFarm) {
            return Collections.emptyList();
        }
        List<String> dataScopeFarmIds = StpLoginUserUtil.getLoginUserDataScope();
        if (CollUtil.isNotEmpty(dataScopeFarmIds)) {
            return dataScopeFarmIds.stream().distinct().collect(Collectors.toList());
        }
        String farmId = StpLoginUserUtil.getLoginUser().getOrgId();
        return StrUtil.isNotBlank(farmId) ? Collections.singletonList(farmId) : Collections.emptyList();
    }

    private String resolveFarmId(String inputFarmId, boolean allFarm, List<String> visibleFarmIds) {
        if (allFarm) {
            if (StrUtil.isNotBlank(inputFarmId)) {
                return inputFarmId;
            }
            String loginOrgId = StpLoginUserUtil.getLoginUser().getOrgId();
            if (StrUtil.isNotBlank(loginOrgId)) {
                return loginOrgId;
            }
            throw new CommonException("请先选择养殖场");
        }
        if (CollUtil.isEmpty(visibleFarmIds)) {
            throw new CommonException("您没有可用的养殖场数据权限");
        }
        if (StrUtil.isBlank(inputFarmId)) {
            return visibleFarmIds.get(0);
        }
        if (!visibleFarmIds.contains(inputFarmId)) {
            throw new CommonException("您没有权限在该养殖场下操作，养殖场id：{}", inputFarmId);
        }
        return inputFarmId;
    }

    private BizClaimManage queryEntityByIdOrNo(String id, String claimNo, boolean allFarm, List<String> visibleFarmIds) {
        BizClaimManage entity = null;
        if (StrUtil.isNotBlank(id)) {
            entity = bizClaimManageMapper.selectById(id);
        } else if (StrUtil.isNotBlank(claimNo)) {
            entity = bizClaimManageMapper.selectOne(new LambdaQueryWrapper<BizClaimManage>()
                    .eq(BizClaimManage::getClaimNo, claimNo)
                    .last("limit 1"));
        }
        if (ObjectUtil.isEmpty(entity) || DELETE_FLAG_DELETE.equals(entity.getDeleteFlag())) {
            throw new CommonException("理赔记录不存在，id：{}，理赔单号：{}", id, claimNo);
        }
        if (!allFarm && !visibleFarmIds.contains(entity.getFarmId())) {
            throw new CommonException("您没有权限查看该理赔记录，养殖场id：{}", entity.getFarmId());
        }
        return entity;
    }

    private boolean isClosed(String status) {
        return STATUS_CLOSED.equals(status) || STATUS_REJECTED.equals(status);
    }

    private String mergeFiles(String oldFiles, String newFiles) {
        if (StrUtil.isBlank(oldFiles)) {
            return newFiles;
        }
        if (StrUtil.isBlank(newFiles)) {
            return oldFiles;
        }
        return oldFiles + "," + newFiles;
    }

    private String mergeRemark(String oldRemark, String newRemark) {
        if (StrUtil.isBlank(newRemark)) {
            return oldRemark;
        }
        if (StrUtil.isBlank(oldRemark)) {
            return newRemark;
        }
        return oldRemark + "\n" + newRemark;
    }
}
