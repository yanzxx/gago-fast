package vip.xiaonuo.biz.modular.insproduct.service.impl;

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
import vip.xiaonuo.biz.modular.insproduct.entity.BizInsProduct;
import vip.xiaonuo.biz.modular.insproduct.mapper.BizInsProductMapper;
import vip.xiaonuo.biz.modular.insproduct.param.BizInsProductAddParam;
import vip.xiaonuo.biz.modular.insproduct.param.BizInsProductEditParam;
import vip.xiaonuo.biz.modular.insproduct.param.BizInsProductIdParam;
import vip.xiaonuo.biz.modular.insproduct.param.BizInsProductPageParam;
import vip.xiaonuo.biz.modular.insproduct.result.BizInsProductPageResult;
import vip.xiaonuo.biz.modular.insproduct.service.BizInsProductService;
import vip.xiaonuo.common.exception.CommonException;
import vip.xiaonuo.common.page.CommonPageRequest;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 保险产品服务实现
 */
@Service
public class BizInsProductServiceImpl implements BizInsProductService {

    private static final String STATUS_DRAFT = "DRAFT";
    private static final String STATUS_ON_SHELF = "ON_SHELF";
    private static final String STATUS_OFF_SHELF = "OFF_SHELF";
    private static final String DELETE_FLAG_DELETE = "DELETE";

    @Resource
    private BizInsProductMapper bizInsProductMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(BizInsProductAddParam addParam) {
        boolean allFarm = isAllFarmVisible();
        List<String> visibleFarmIds = getVisibleFarmIds(allFarm);
        String farmId = resolveFarmId(addParam.getFarmId(), allFarm, visibleFarmIds);

        if (bizInsProductMapper.selectCount(new LambdaQueryWrapper<BizInsProduct>()
                .eq(BizInsProduct::getProductCode, addParam.getProductCode())) > 0) {
            throw new CommonException("存在重复的产品编码：{}", addParam.getProductCode());
        }

        SaBaseLoginUser loginUser = StpLoginUserUtil.getLoginUser();
        BizInsProduct entity = BeanUtil.toBean(addParam, BizInsProduct.class);
        entity.setId(IdUtil.fastSimpleUUID());
        entity.setTenantId(ObjectUtil.isNotEmpty(loginUser) ? loginUser.getTenantId() : null);
        entity.setFarmId(farmId);
        entity.setStatus(STATUS_DRAFT);
        bizInsProductMapper.insert(entity);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(BizInsProductEditParam editParam) {
        boolean allFarm = isAllFarmVisible();
        List<String> visibleFarmIds = getVisibleFarmIds(allFarm);
        BizInsProduct entity = queryEntityByIdOrCode(editParam.getId(), null, allFarm, visibleFarmIds);
        String farmId = resolveFarmId(editParam.getFarmId(), allFarm, visibleFarmIds);

        if (STATUS_ON_SHELF.equals(entity.getStatus())) {
            throw new CommonException("已上架产品不允许编辑，请先下架");
        }

        entity.setFarmId(farmId);
        entity.setProductName(editParam.getProductName());
        entity.setInsuranceType(editParam.getInsuranceType());
        entity.setCoverageScope(editParam.getCoverageScope());
        entity.setPremiumRate(editParam.getPremiumRate());
        entity.setMaxCompensation(editParam.getMaxCompensation());
        entity.setRemark(editParam.getRemark());
        bizInsProductMapper.updateById(entity);
    }

    @Override
    public BizInsProduct detail(BizInsProductIdParam idParam) {
        boolean allFarm = isAllFarmVisible();
        List<String> visibleFarmIds = getVisibleFarmIds(allFarm);
        return queryEntityByIdOrCode(idParam.getId(), idParam.getProductCode(), allFarm, visibleFarmIds);
    }

    @Override
    public Page<BizInsProductPageResult> page(BizInsProductPageParam pageParam) {
        boolean allFarm = isAllFarmVisible();
        List<String> visibleFarmIds = getVisibleFarmIds(allFarm);
        if (!allFarm && StrUtil.isNotBlank(pageParam.getFarmId()) && !visibleFarmIds.contains(pageParam.getFarmId())) {
            return new Page<>();
        }
        return (Page<BizInsProductPageResult>) bizInsProductMapper.page(CommonPageRequest.defaultPage(), pageParam, allFarm, visibleFarmIds);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void onShelf(BizInsProductIdParam idParam) {
        boolean allFarm = isAllFarmVisible();
        List<String> visibleFarmIds = getVisibleFarmIds(allFarm);
        BizInsProduct entity = queryEntityByIdOrCode(idParam.getId(), idParam.getProductCode(), allFarm, visibleFarmIds);
        entity.setStatus(STATUS_ON_SHELF);
        bizInsProductMapper.updateById(entity);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void offShelf(BizInsProductIdParam idParam) {
        boolean allFarm = isAllFarmVisible();
        List<String> visibleFarmIds = getVisibleFarmIds(allFarm);
        BizInsProduct entity = queryEntityByIdOrCode(idParam.getId(), idParam.getProductCode(), allFarm, visibleFarmIds);
        entity.setStatus(STATUS_OFF_SHELF);
        bizInsProductMapper.updateById(entity);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(BizInsProductIdParam idParam) {
        boolean allFarm = isAllFarmVisible();
        List<String> visibleFarmIds = getVisibleFarmIds(allFarm);
        BizInsProduct entity = queryEntityByIdOrCode(idParam.getId(), idParam.getProductCode(), allFarm, visibleFarmIds);
        if (STATUS_ON_SHELF.equals(entity.getStatus())) {
            throw new CommonException("已上架产品不允许删除，请先下架");
        }
        entity.setDeleteFlag(DELETE_FLAG_DELETE);
        bizInsProductMapper.updateById(entity);
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

    private BizInsProduct queryEntityByIdOrCode(String id, String productCode, boolean allFarm, List<String> visibleFarmIds) {
        BizInsProduct entity = null;
        if (StrUtil.isNotBlank(id)) {
            entity = bizInsProductMapper.selectById(id);
        } else if (StrUtil.isNotBlank(productCode)) {
            entity = bizInsProductMapper.selectOne(new LambdaQueryWrapper<BizInsProduct>()
                    .eq(BizInsProduct::getProductCode, productCode)
                    .last("limit 1"));
        }
        if (ObjectUtil.isEmpty(entity)) {
            throw new CommonException("保险产品不存在，id：{}，产品编码：{}", id, productCode);
        }
        if (!allFarm && !visibleFarmIds.contains(entity.getFarmId())) {
            throw new CommonException("您没有权限查看该产品，养殖场id：{}", entity.getFarmId());
        }
        return entity;
    }
}
