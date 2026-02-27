package vip.xiaonuo.biz.modular.livestock.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vip.xiaonuo.auth.core.pojo.SaBaseLoginUser;
import vip.xiaonuo.auth.core.util.StpLoginUserUtil;
import vip.xiaonuo.biz.modular.livestock.entity.BizLivestock;
import vip.xiaonuo.biz.modular.livestock.mapper.BizLivestockMapper;
import vip.xiaonuo.biz.modular.livestock.param.BizLivestockAddParam;
import vip.xiaonuo.biz.modular.livestock.param.BizLivestockEditParam;
import vip.xiaonuo.biz.modular.livestock.param.BizLivestockIdParam;
import vip.xiaonuo.biz.modular.livestock.param.BizLivestockPageParam;
import vip.xiaonuo.biz.modular.livestock.param.BizLivestockSpeciesOptionParam;
import vip.xiaonuo.biz.modular.livestock.result.BizLivestockPageResult;
import vip.xiaonuo.biz.modular.livestock.result.BizLivestockSpeciesOptionResult;
import vip.xiaonuo.biz.modular.livestock.service.BizLivestockService;
import vip.xiaonuo.common.exception.CommonException;
import vip.xiaonuo.common.page.CommonPageRequest;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 牲畜登记服务实现
 */
@Service
public class BizLivestockServiceImpl implements BizLivestockService {

    @Resource
    private BizLivestockMapper bizLivestockMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(BizLivestockAddParam addParam) {
        boolean allFarm = isAllFarmVisible();
        List<String> visibleFarmIds = getVisibleFarmIds(allFarm);
        String farmId = resolveFarmId(addParam.getFarmId(), allFarm, visibleFarmIds);

        if (bizLivestockMapper.selectCount(new LambdaQueryWrapper<BizLivestock>()
                .eq(BizLivestock::getCollarNo, addParam.getCollarNo())) > 0) {
            throw new CommonException("存在重复的项圈编号：{}", addParam.getCollarNo());
        }

        SaBaseLoginUser loginUser = StpLoginUserUtil.getLoginUser();
        BizLivestock entity = BeanUtil.toBean(addParam, BizLivestock.class);
        entity.setId(IdUtil.fastSimpleUUID());
        entity.setTenantId(ObjectUtil.isNotEmpty(loginUser) ? loginUser.getTenantId() : null);
        entity.setFarmId(farmId);
        entity.setLivestockNo("LS-" + IdUtil.getSnowflakeNextIdStr());
        entity.setStatus(StrUtil.isNotBlank(addParam.getStatus()) ? addParam.getStatus() : "IN_STOCK");
        entity.setRegisterDate(DateUtil.today());
        bizLivestockMapper.insert(entity);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(BizLivestockEditParam editParam) {
        boolean allFarm = isAllFarmVisible();
        List<String> visibleFarmIds = getVisibleFarmIds(allFarm);
        BizLivestock entity = queryEntityByIdOrNo(editParam.getId(), editParam.getLivestockNo(), allFarm, visibleFarmIds);
        String farmId = resolveFarmId(editParam.getFarmId(), allFarm, visibleFarmIds);

        if (bizLivestockMapper.selectCount(new LambdaQueryWrapper<BizLivestock>()
                .eq(BizLivestock::getCollarNo, editParam.getCollarNo())
                .ne(BizLivestock::getId, editParam.getId())) > 0) {
            throw new CommonException("存在重复的项圈编号：{}", editParam.getCollarNo());
        }

        entity.setFarmId(farmId);
        entity.setSpeciesName(editParam.getSpeciesName());
        entity.setBirthDate(editParam.getBirthDate());
        entity.setStatus(editParam.getStatus());
        entity.setImmunityStatus(editParam.getImmunityStatus());
        entity.setLastImmunityDate(editParam.getLastImmunityDate());
        entity.setCollarNo(editParam.getCollarNo());
        entity.setGender(editParam.getGender());
        entity.setPenNo(editParam.getPenNo());
        entity.setRemark(editParam.getRemark());
        entity.setImageUrls(editParam.getImageUrls());
        bizLivestockMapper.updateById(entity);
    }

    @Override
    public BizLivestock detail(BizLivestockIdParam idParam) {
        boolean allFarm = isAllFarmVisible();
        List<String> visibleFarmIds = getVisibleFarmIds(allFarm);
        return queryEntityByIdOrNo(idParam.getId(), idParam.getLivestockNo(), allFarm, visibleFarmIds);
    }

    @Override
    public Page<BizLivestockPageResult> page(BizLivestockPageParam pageParam) {
        boolean allFarm = isAllFarmVisible();
        List<String> visibleFarmIds = getVisibleFarmIds(allFarm);
        if (!allFarm && StrUtil.isNotBlank(pageParam.getFarmId()) && !visibleFarmIds.contains(pageParam.getFarmId())) {
            return new Page<>();
        }
        return (Page<BizLivestockPageResult>) bizLivestockMapper.page(CommonPageRequest.defaultPage(), pageParam, allFarm, visibleFarmIds);
    }

    @Override
    public List<BizLivestockSpeciesOptionResult> speciesOptions(BizLivestockSpeciesOptionParam optionParam) {
        boolean allFarm = isAllFarmVisible();
        List<String> visibleFarmIds = getVisibleFarmIds(allFarm);
        if (!allFarm && StrUtil.isNotBlank(optionParam.getFarmId()) && !visibleFarmIds.contains(optionParam.getFarmId())) {
            return Collections.emptyList();
        }
        return bizLivestockMapper.speciesOptions(optionParam.getFarmId(), allFarm, visibleFarmIds);
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
            throw new CommonException("新增失败，请先选择养殖场");
        }
        if (CollUtil.isEmpty(visibleFarmIds)) {
            throw new CommonException("您没有可用的养殖场数据权限");
        }
        if (StrUtil.isBlank(inputFarmId)) {
            return visibleFarmIds.get(0);
        }
        if (!visibleFarmIds.contains(inputFarmId)) {
            throw new CommonException("您没有权限在该养殖场下新增登记，养殖场id：{}", inputFarmId);
        }
        return inputFarmId;
    }

    private BizLivestock queryEntity(String id, boolean allFarm, List<String> visibleFarmIds) {
        BizLivestock entity = bizLivestockMapper.selectById(id);
        if (ObjectUtil.isEmpty(entity)) {
            throw new CommonException("牲畜登记不存在，id：{}", id);
        }
        if (!allFarm && !visibleFarmIds.contains(entity.getFarmId())) {
            throw new CommonException("您没有权限查看该登记，养殖场id：{}", entity.getFarmId());
        }
        return entity;
    }

    private BizLivestock queryEntityByIdOrNo(String id, String livestockNo, boolean allFarm, List<String> visibleFarmIds) {
        BizLivestock entity = null;
        if (StrUtil.isNotBlank(id)) {
            entity = bizLivestockMapper.selectById(id);
        } else if (StrUtil.isNotBlank(livestockNo)) {
            entity = bizLivestockMapper.selectOne(new LambdaQueryWrapper<BizLivestock>()
                    .eq(BizLivestock::getLivestockNo, livestockNo)
                    .last("limit 1"));
        }
        if (ObjectUtil.isEmpty(entity)) {
            throw new CommonException("牲畜登记不存在，id：{}，牲畜编号：{}", id, livestockNo);
        }
        if (!allFarm && !visibleFarmIds.contains(entity.getFarmId())) {
            throw new CommonException("您没有权限查看该登记，养殖场id：{}", entity.getFarmId());
        }
        return entity;
    }
}
