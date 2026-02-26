package vip.xiaonuo.mobile.modular.resource.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import vip.xiaonuo.common.enums.CommonSortOrderEnum;
import vip.xiaonuo.common.exception.CommonException;
import vip.xiaonuo.common.page.CommonPageRequest;
import vip.xiaonuo.mobile.modular.resource.entity.MobileGlobalResource;
import vip.xiaonuo.mobile.modular.resource.enums.MobileResourceCategoryEnum;
import vip.xiaonuo.mobile.modular.resource.mapper.MobileGlobalResourceMapper;
import vip.xiaonuo.mobile.modular.resource.param.resource.MobileGlobalResourceAddParam;
import vip.xiaonuo.mobile.modular.resource.param.resource.MobileGlobalResourceEditParam;
import vip.xiaonuo.mobile.modular.resource.param.resource.MobileGlobalResourceIdParam;
import vip.xiaonuo.mobile.modular.resource.param.resource.MobileGlobalResourcePageParam;
import vip.xiaonuo.mobile.modular.resource.service.MobileGlobalResourceService;
import vip.xiaonuo.sys.api.SysRelationApi;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MobileGlobalResourceServiceImpl extends ServiceImpl<MobileGlobalResourceMapper, MobileGlobalResource> implements MobileGlobalResourceService {

    @Resource
    private SysRelationApi sysRelationApi;


    @Override
    public Page<MobileGlobalResource> page(MobileGlobalResourcePageParam mobileGlobalResourcePageParam) {
        QueryWrapper<MobileGlobalResource> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(MobileGlobalResource::getCategory, MobileResourceCategoryEnum.BUTTON.getValue())
                .like(StringUtils.hasText(mobileGlobalResourcePageParam.getTitle()), MobileGlobalResource::getTitle, mobileGlobalResourcePageParam.getTitle());


        if(ObjectUtil.isAllNotEmpty(mobileGlobalResourcePageParam.getSortField(), mobileGlobalResourcePageParam.getSortOrder())) {
            CommonSortOrderEnum.validate(mobileGlobalResourcePageParam.getSortOrder());
            queryWrapper.orderBy(true, mobileGlobalResourcePageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(mobileGlobalResourcePageParam.getSortField()));
        } else {
            queryWrapper.lambda().orderByAsc(MobileGlobalResource::getSortCode);
        }
        return this.page(CommonPageRequest.defaultPage(), queryWrapper);
    }

    @Override
    public void add(MobileGlobalResourceAddParam mobileGlobalResourceAddParam) {
        MobileGlobalResource mobileGlobalResource = BeanUtil.toBean(mobileGlobalResourceAddParam, MobileGlobalResource.class);
        boolean repeatCode = this.count(new LambdaQueryWrapper<MobileGlobalResource>()
                .eq(MobileGlobalResource::getCategory, MobileResourceCategoryEnum.BUTTON.getValue())
                .eq(MobileGlobalResource::getCode, mobileGlobalResource.getCode())) > 0;
        if(repeatCode) {
            throw new CommonException("存在重复的移动端全局权限，编码为：{}", mobileGlobalResource.getCode());
        }


        this.save(mobileGlobalResource);
    }

    @Override
    public void edit(MobileGlobalResourceEditParam mobileGlobalResourceEditParam) {
        MobileGlobalResource mobileGlobalResource = this.queryEntity(mobileGlobalResourceEditParam.getId());
        BeanUtil.copyProperties(mobileGlobalResourceEditParam, mobileGlobalResource);

        boolean repeatCode = this.count(new LambdaQueryWrapper<MobileGlobalResource>()
                .eq(MobileGlobalResource::getCategory, MobileResourceCategoryEnum.BUTTON.getValue())
                .eq(MobileGlobalResource::getCode, mobileGlobalResource.getCode())
                .ne(MobileGlobalResource::getId, mobileGlobalResourceEditParam.getId())) > 0;
        if(repeatCode) {
            throw new CommonException("存在重复的移动端全局权限，编码为：{}", mobileGlobalResource.getCode());
        }
        this.updateById(mobileGlobalResource);
    }

    @Override
    public MobileGlobalResource queryEntity(String id) {
        MobileGlobalResource mobileGlobalResource = this.getById(id);
        if(ObjectUtil.isEmpty(mobileGlobalResource)) {
            throw new CommonException("移动端全局权限不存在，id值为：{}", id);
        }
        return mobileGlobalResource;
    }

    @Override
    public void delete(List<MobileGlobalResourceIdParam> mobileGlobalResourceIdParamList) {
        List<String> globalResourceIdList = CollStreamUtil.toList(mobileGlobalResourceIdParamList, MobileGlobalResourceIdParam::getId);
        if (CollectionUtil.isEmpty(globalResourceIdList)){
            return;
        }

        // 执行删除
        this.removeByIds(globalResourceIdList);
        // 删除这些按钮和角色的绑定关系
        sysRelationApi.removeRoleHasMobileGlobalRelation(globalResourceIdList);
    }

    @Override
    public List<JSONObject> mobileGlobalResourceSelector() {
        return this.lambdaQuery().eq(MobileGlobalResource::getCategory, MobileResourceCategoryEnum.BUTTON.getValue()).list().stream().map(JSONObject::new).collect(Collectors.toList());

    }
}
