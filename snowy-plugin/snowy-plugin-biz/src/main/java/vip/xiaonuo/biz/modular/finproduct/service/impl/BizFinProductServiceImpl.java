package vip.xiaonuo.biz.modular.finproduct.service.impl;

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
import vip.xiaonuo.biz.modular.finproduct.entity.BizFinProduct;
import vip.xiaonuo.biz.modular.finproduct.mapper.BizFinProductMapper;
import vip.xiaonuo.biz.modular.finproduct.param.BizFinProductAddParam;
import vip.xiaonuo.biz.modular.finproduct.param.BizFinProductEditParam;
import vip.xiaonuo.biz.modular.finproduct.param.BizFinProductIdParam;
import vip.xiaonuo.biz.modular.finproduct.param.BizFinProductPageParam;
import vip.xiaonuo.biz.modular.finproduct.result.BizFinProductPageResult;
import vip.xiaonuo.biz.modular.finproduct.service.BizFinProductService;
import vip.xiaonuo.common.exception.CommonException;
import vip.xiaonuo.common.page.CommonPageRequest;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 金融产品服务实现
 */
@Service
public class BizFinProductServiceImpl implements BizFinProductService {

    private static final String STATUS_DRAFT = "DRAFT";
    private static final String STATUS_ON_SHELF = "ON_SHELF";
    private static final String STATUS_OFF_SHELF = "OFF_SHELF";
    private static final String DELETE_FLAG_DELETE = "DELETE";

    @Resource
    private BizFinProductMapper bizFinProductMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(BizFinProductAddParam addParam) {
        boolean allFarm = isAllFarmVisible();
        List<String> visibleFarmIds = getVisibleFarmIds(allFarm);
        String farmId = resolveFarmId(addParam.getFarmId(), allFarm, visibleFarmIds);

        checkAmountRange(addParam.getAmountMin(), addParam.getAmountMax());

        if (bizFinProductMapper.selectCount(new LambdaQueryWrapper<BizFinProduct>()
                .eq(BizFinProduct::getProductCode, addParam.getProductCode())) > 0) {
            throw new CommonException("存在重复的产品编码：{}", addParam.getProductCode());
        }

        SaBaseLoginUser loginUser = StpLoginUserUtil.getLoginUser();
        BizFinProduct entity = BeanUtil.toBean(addParam, BizFinProduct.class);
        entity.setId(IdUtil.fastSimpleUUID());
        entity.setTenantId(ObjectUtil.isNotEmpty(loginUser) ? loginUser.getTenantId() : null);
        entity.setFarmId(farmId);
        entity.setStatus(STATUS_DRAFT);
        bizFinProductMapper.insert(entity);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(BizFinProductEditParam editParam) {
        boolean allFarm = isAllFarmVisible();
        List<String> visibleFarmIds = getVisibleFarmIds(allFarm);
        BizFinProduct entity = queryEntityByIdOrCode(editParam.getId(), editParam.getProductCode(), allFarm, visibleFarmIds);
        String farmId = resolveFarmId(editParam.getFarmId(), allFarm, visibleFarmIds);

        if (STATUS_ON_SHELF.equals(entity.getStatus())) {
            throw new CommonException("已上架产品不允许编辑，请先下架");
        }

        checkAmountRange(editParam.getAmountMin(), editParam.getAmountMax());

        entity.setFarmId(farmId);
        entity.setProductName(editParam.getProductName());
        entity.setSpeciesCodes(editParam.getSpeciesCodes());
        entity.setAmountMin(editParam.getAmountMin());
        entity.setAmountMax(editParam.getAmountMax());
        entity.setAnnualRate(editParam.getAnnualRate());
        entity.setTermMonths(editParam.getTermMonths());
        entity.setRepayType(editParam.getRepayType());
        entity.setRemark(editParam.getRemark());
        bizFinProductMapper.updateById(entity);
    }

    @Override
    public BizFinProduct detail(BizFinProductIdParam idParam) {
        boolean allFarm = isAllFarmVisible();
        List<String> visibleFarmIds = getVisibleFarmIds(allFarm);
        return queryEntityByIdOrCode(idParam.getId(), idParam.getProductCode(), allFarm, visibleFarmIds);
    }

    @Override
    public Page<BizFinProductPageResult> page(BizFinProductPageParam pageParam) {
        boolean allFarm = isAllFarmVisible();
        List<String> visibleFarmIds = getVisibleFarmIds(allFarm);
        if (!allFarm && StrUtil.isNotBlank(pageParam.getFarmId()) && !visibleFarmIds.contains(pageParam.getFarmId())) {
            return new Page<>();
        }
        return (Page<BizFinProductPageResult>) bizFinProductMapper.page(CommonPageRequest.defaultPage(), pageParam, allFarm, visibleFarmIds);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void onShelf(BizFinProductIdParam idParam) {
        boolean allFarm = isAllFarmVisible();
        List<String> visibleFarmIds = getVisibleFarmIds(allFarm);
        BizFinProduct entity = queryEntityByIdOrCode(idParam.getId(), idParam.getProductCode(), allFarm, visibleFarmIds);
        entity.setStatus(STATUS_ON_SHELF);
        bizFinProductMapper.updateById(entity);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void offShelf(BizFinProductIdParam idParam) {
        boolean allFarm = isAllFarmVisible();
        List<String> visibleFarmIds = getVisibleFarmIds(allFarm);
        BizFinProduct entity = queryEntityByIdOrCode(idParam.getId(), idParam.getProductCode(), allFarm, visibleFarmIds);
        entity.setStatus(STATUS_OFF_SHELF);
        bizFinProductMapper.updateById(entity);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(BizFinProductIdParam idParam) {
        boolean allFarm = isAllFarmVisible();
        List<String> visibleFarmIds = getVisibleFarmIds(allFarm);
        BizFinProduct entity = queryEntityByIdOrCode(idParam.getId(), idParam.getProductCode(), allFarm, visibleFarmIds);
        if (STATUS_ON_SHELF.equals(entity.getStatus())) {
            throw new CommonException("已上架产品不允许删除，请先下架");
        }
        entity.setDeleteFlag(DELETE_FLAG_DELETE);
        bizFinProductMapper.updateById(entity);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void batchDelete(List<BizFinProductIdParam> idParamList) {
        idParamList.forEach(this::delete);
    }

    private void checkAmountRange(java.math.BigDecimal amountMin, java.math.BigDecimal amountMax) {
        if (ObjectUtil.isEmpty(amountMin) || ObjectUtil.isEmpty(amountMax) || amountMin.compareTo(amountMax) > 0) {
            throw new CommonException("额度范围不正确，最低额度不能大于最高额度");
        }
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

    private BizFinProduct queryEntityByIdOrCode(String id, String productCode, boolean allFarm, List<String> visibleFarmIds) {
        BizFinProduct entity = null;
        if (StrUtil.isNotBlank(id)) {
            entity = bizFinProductMapper.selectById(id);
        } else if (StrUtil.isNotBlank(productCode)) {
            entity = bizFinProductMapper.selectOne(new LambdaQueryWrapper<BizFinProduct>()
                    .eq(BizFinProduct::getProductCode, productCode)
                    .last("limit 1"));
        }
        if (ObjectUtil.isEmpty(entity)) {
            throw new CommonException("金融产品不存在，id：{}，产品编码：{}", id, productCode);
        }
        if (!allFarm && !visibleFarmIds.contains(entity.getFarmId())) {
            throw new CommonException("您没有权限查看该产品，养殖场id：{}", entity.getFarmId());
        }
        return entity;
    }
}
