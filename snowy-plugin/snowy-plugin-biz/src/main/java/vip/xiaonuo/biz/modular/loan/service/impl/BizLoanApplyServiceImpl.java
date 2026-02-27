package vip.xiaonuo.biz.modular.loan.service.impl;

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
import vip.xiaonuo.biz.modular.loan.entity.BizLoanApply;
import vip.xiaonuo.biz.modular.loan.mapper.BizLoanApplyMapper;
import vip.xiaonuo.biz.modular.loan.param.BizLoanApplyAddParam;
import vip.xiaonuo.biz.modular.loan.param.BizLoanApplyEditParam;
import vip.xiaonuo.biz.modular.loan.param.BizLoanApplyIdParam;
import vip.xiaonuo.biz.modular.loan.param.BizLoanApplyPageParam;
import vip.xiaonuo.biz.modular.loan.result.BizLoanApplyPageResult;
import vip.xiaonuo.biz.modular.loan.service.BizLoanApplyService;
import vip.xiaonuo.common.exception.CommonException;
import vip.xiaonuo.common.page.CommonPageRequest;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 贷款申请服务实现
 */
@Service
public class BizLoanApplyServiceImpl implements BizLoanApplyService {

    private static final String STATUS_PENDING_SUBMIT = "PENDING_SUBMIT";
    private static final String DELETE_FLAG_DELETE = "DELETE";

    @Resource
    private BizLoanApplyMapper bizLoanApplyMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(BizLoanApplyAddParam addParam) {
        boolean allFarm = isAllFarmVisible();
        List<String> visibleFarmIds = getVisibleFarmIds(allFarm);
        String farmId = resolveFarmId(addParam.getFarmId(), allFarm, visibleFarmIds);

        if (bizLoanApplyMapper.selectCount(new LambdaQueryWrapper<BizLoanApply>()
                .eq(BizLoanApply::getApplyNo, addParam.getApplyNo())) > 0) {
            throw new CommonException("存在重复的申请单号：{}", addParam.getApplyNo());
        }

        SaBaseLoginUser loginUser = StpLoginUserUtil.getLoginUser();
        BizLoanApply entity = BeanUtil.toBean(addParam, BizLoanApply.class);
        entity.setId(IdUtil.fastSimpleUUID());
        entity.setTenantId(ObjectUtil.isNotEmpty(loginUser) ? loginUser.getTenantId() : null);
        entity.setFarmId(farmId);
        entity.setLoanStatus(StrUtil.isNotBlank(addParam.getLoanStatus()) ? addParam.getLoanStatus() : STATUS_PENDING_SUBMIT);
        entity.setApplyTime(new java.util.Date());
        bizLoanApplyMapper.insert(entity);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(BizLoanApplyEditParam editParam) {
        boolean allFarm = isAllFarmVisible();
        List<String> visibleFarmIds = getVisibleFarmIds(allFarm);
        BizLoanApply entity = queryEntityByIdOrNo(editParam.getId(), null, allFarm, visibleFarmIds);
        String farmId = resolveFarmId(editParam.getFarmId(), allFarm, visibleFarmIds);

        entity.setFarmId(farmId);
        entity.setProductCode(editParam.getProductCode());
        entity.setApplyAmount(editParam.getApplyAmount());
        entity.setApplicantName(editParam.getApplicantName());
        entity.setLoanStatus(StrUtil.isNotBlank(editParam.getLoanStatus()) ? editParam.getLoanStatus() : entity.getLoanStatus());
        entity.setRemark(editParam.getRemark());
        bizLoanApplyMapper.updateById(entity);
    }

    @Override
    public BizLoanApply detail(BizLoanApplyIdParam idParam) {
        boolean allFarm = isAllFarmVisible();
        List<String> visibleFarmIds = getVisibleFarmIds(allFarm);
        return queryEntityByIdOrNo(idParam.getId(), idParam.getApplyNo(), allFarm, visibleFarmIds);
    }

    @Override
    public Page<BizLoanApplyPageResult> page(BizLoanApplyPageParam pageParam) {
        boolean allFarm = isAllFarmVisible();
        List<String> visibleFarmIds = getVisibleFarmIds(allFarm);
        if (!allFarm && StrUtil.isNotBlank(pageParam.getFarmId()) && !visibleFarmIds.contains(pageParam.getFarmId())) {
            return new Page<>();
        }
        return (Page<BizLoanApplyPageResult>) bizLoanApplyMapper.page(CommonPageRequest.defaultPage(), pageParam, allFarm, visibleFarmIds);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(BizLoanApplyIdParam idParam) {
        boolean allFarm = isAllFarmVisible();
        List<String> visibleFarmIds = getVisibleFarmIds(allFarm);
        BizLoanApply entity = queryEntityByIdOrNo(idParam.getId(), idParam.getApplyNo(), allFarm, visibleFarmIds);
        entity.setDeleteFlag(DELETE_FLAG_DELETE);
        bizLoanApplyMapper.updateById(entity);
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

    private BizLoanApply queryEntityByIdOrNo(String id, String applyNo, boolean allFarm, List<String> visibleFarmIds) {
        BizLoanApply entity = null;
        if (StrUtil.isNotBlank(id)) {
            entity = bizLoanApplyMapper.selectById(id);
        } else if (StrUtil.isNotBlank(applyNo)) {
            entity = bizLoanApplyMapper.selectOne(new LambdaQueryWrapper<BizLoanApply>()
                    .eq(BizLoanApply::getApplyNo, applyNo)
                    .last("limit 1"));
        }
        if (ObjectUtil.isEmpty(entity)) {
            throw new CommonException("贷款申请不存在，id：{}，申请单号：{}", id, applyNo);
        }
        if (!allFarm && !visibleFarmIds.contains(entity.getFarmId())) {
            throw new CommonException("您没有权限查看该申请，养殖场id：{}", entity.getFarmId());
        }
        return entity;
    }
}
