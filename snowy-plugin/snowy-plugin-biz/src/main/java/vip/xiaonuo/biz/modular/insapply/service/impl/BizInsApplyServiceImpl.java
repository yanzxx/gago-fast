package vip.xiaonuo.biz.modular.insapply.service.impl;

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
import vip.xiaonuo.biz.modular.insapply.entity.BizInsApply;
import vip.xiaonuo.biz.modular.insapply.mapper.BizInsApplyMapper;
import vip.xiaonuo.biz.modular.insapply.param.BizInsApplyAddParam;
import vip.xiaonuo.biz.modular.insapply.param.BizInsApplyEditParam;
import vip.xiaonuo.biz.modular.insapply.param.BizInsApplyIdParam;
import vip.xiaonuo.biz.modular.insapply.param.BizInsApplyPageParam;
import vip.xiaonuo.biz.modular.insapply.result.BizInsApplyPageResult;
import vip.xiaonuo.biz.modular.insapply.service.BizInsApplyService;
import vip.xiaonuo.common.exception.CommonException;
import vip.xiaonuo.common.page.CommonPageRequest;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 投保记录服务实现
 */
@Service
public class BizInsApplyServiceImpl implements BizInsApplyService {

    private static final String STATUS_PENDING_SUBMIT = "PENDING_SUBMIT";
    private static final String STATUS_EFFECTIVE = "EFFECTIVE";
    private static final String DELETE_FLAG_DELETE = "DELETE";

    @Resource
    private BizInsApplyMapper bizInsApplyMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(BizInsApplyAddParam addParam) {
        boolean allFarm = isAllFarmVisible();
        List<String> visibleFarmIds = getVisibleFarmIds(allFarm);
        String farmId = resolveFarmId(addParam.getFarmId(), allFarm, visibleFarmIds);

        if (bizInsApplyMapper.selectCount(new LambdaQueryWrapper<BizInsApply>()
                .eq(BizInsApply::getApplyNo, addParam.getApplyNo())) > 0) {
            throw new CommonException("存在重复的投保单号：{}", addParam.getApplyNo());
        }

        SaBaseLoginUser loginUser = StpLoginUserUtil.getLoginUser();
        BizInsApply entity = BeanUtil.toBean(addParam, BizInsApply.class);
        entity.setId(IdUtil.fastSimpleUUID());
        entity.setTenantId(ObjectUtil.isNotEmpty(loginUser) ? loginUser.getTenantId() : null);
        entity.setFarmId(farmId);
        entity.setStatus(StrUtil.isNotBlank(addParam.getStatus()) ? addParam.getStatus() : STATUS_PENDING_SUBMIT);
        bizInsApplyMapper.insert(entity);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(BizInsApplyEditParam editParam) {
        boolean allFarm = isAllFarmVisible();
        List<String> visibleFarmIds = getVisibleFarmIds(allFarm);
        BizInsApply entity = queryEntityByIdOrNo(editParam.getId(), null, allFarm, visibleFarmIds);
        String farmId = resolveFarmId(editParam.getFarmId(), allFarm, visibleFarmIds);

        if (STATUS_EFFECTIVE.equals(entity.getStatus())) {
            throw new CommonException("已生效保单不允许编辑");
        }

        entity.setFarmId(farmId);
        entity.setPolicyNo(editParam.getPolicyNo());
        entity.setProductCode(editParam.getProductCode());
        entity.setInsuredName(editParam.getInsuredName());
        entity.setSpeciesType(editParam.getSpeciesType());
        entity.setInsuredCount(editParam.getInsuredCount());
        entity.setInsuredAmount(editParam.getInsuredAmount());
        entity.setPremiumAmount(editParam.getPremiumAmount());
        entity.setStatus(StrUtil.isNotBlank(editParam.getStatus()) ? editParam.getStatus() : entity.getStatus());
        entity.setRemark(editParam.getRemark());
        bizInsApplyMapper.updateById(entity);
    }

    @Override
    public BizInsApply detail(BizInsApplyIdParam idParam) {
        boolean allFarm = isAllFarmVisible();
        List<String> visibleFarmIds = getVisibleFarmIds(allFarm);
        return queryEntityByIdOrNo(idParam.getId(), idParam.getApplyNo(), allFarm, visibleFarmIds);
    }

    @Override
    public Page<BizInsApplyPageResult> page(BizInsApplyPageParam pageParam) {
        boolean allFarm = isAllFarmVisible();
        List<String> visibleFarmIds = getVisibleFarmIds(allFarm);
        if (!allFarm && StrUtil.isNotBlank(pageParam.getFarmId()) && !visibleFarmIds.contains(pageParam.getFarmId())) {
            return new Page<>();
        }
        return (Page<BizInsApplyPageResult>) bizInsApplyMapper.page(CommonPageRequest.defaultPage(), pageParam, allFarm, visibleFarmIds);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(BizInsApplyIdParam idParam) {
        boolean allFarm = isAllFarmVisible();
        List<String> visibleFarmIds = getVisibleFarmIds(allFarm);
        BizInsApply entity = queryEntityByIdOrNo(idParam.getId(), idParam.getApplyNo(), allFarm, visibleFarmIds);
        if (STATUS_EFFECTIVE.equals(entity.getStatus())) {
            throw new CommonException("已生效保单不允许删除");
        }
        entity.setDeleteFlag(DELETE_FLAG_DELETE);
        bizInsApplyMapper.updateById(entity);
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

    private BizInsApply queryEntityByIdOrNo(String id, String applyNo, boolean allFarm, List<String> visibleFarmIds) {
        BizInsApply entity = null;
        if (StrUtil.isNotBlank(id)) {
            entity = bizInsApplyMapper.selectById(id);
        } else if (StrUtil.isNotBlank(applyNo)) {
            entity = bizInsApplyMapper.selectOne(new LambdaQueryWrapper<BizInsApply>()
                    .eq(BizInsApply::getApplyNo, applyNo)
                    .last("limit 1"));
        }
        if (ObjectUtil.isEmpty(entity)) {
            throw new CommonException("投保记录不存在，id：{}，投保单号：{}", id, applyNo);
        }
        if (!allFarm && !visibleFarmIds.contains(entity.getFarmId())) {
            throw new CommonException("您没有权限查看该投保记录，养殖场id：{}", entity.getFarmId());
        }
        return entity;
    }
}
