package vip.xiaonuo.sys.modular.region.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNode;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.springframework.util.CollectionUtils;

import lombok.SneakyThrows;
import javax.servlet.http.HttpServletResponse;
import vip.xiaonuo.common.annotation.ExcelConfig;
import vip.xiaonuo.common.excel.CommonExcelExportServer;
import vip.xiaonuo.common.excel.ExcelExporter;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vip.xiaonuo.common.enums.CommonSortOrderEnum;
import vip.xiaonuo.common.exception.CommonException;
import vip.xiaonuo.common.page.CommonPageEntity;
import vip.xiaonuo.sys.entity.SysOrg;
import vip.xiaonuo.sys.entity.SysUser;
import vip.xiaonuo.sys.modular.org.param.SysOrgSelectorUserParam;
import vip.xiaonuo.sys.modular.region.entity.Region;
import vip.xiaonuo.sys.modular.region.entity.VO.RegionExportVO;
import vip.xiaonuo.sys.modular.region.entity.BO.RegionIntoBO;
import vip.xiaonuo.sys.modular.region.mapper.RegionMapper;
import vip.xiaonuo.sys.modular.region.param.RegionAddParam;
import vip.xiaonuo.sys.modular.region.param.RegionEditParam;
import vip.xiaonuo.sys.modular.region.param.RegionIdParam;
import vip.xiaonuo.sys.modular.region.param.RegionPageParam;
import vip.xiaonuo.sys.modular.region.service.RegionService;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 行政区划Service接口实现类
 *
 * @author gago
 * @date  2025/08/26 15:08
 **/
@Service
public class RegionServiceImpl extends ServiceImpl<RegionMapper, Region> implements RegionService ,CommonExcelExportServer {
    List<Integer> lengthRules= ListUtil.toList(2, 4, 6, 9, 12, 15, 18, 21, 24, 27, 30);
    @Override
    public Page<Region> page(RegionPageParam regionPageParam) {
        QueryWrapper<Region> queryWrapper = new QueryWrapper<>();
        if(ObjectUtil.isNotEmpty(regionPageParam.getCode())) {
            queryWrapper.lambda().eq(Region::getCode, regionPageParam.getCode());
        }
        if(ObjectUtil.isNotEmpty(regionPageParam.getName())) {
            queryWrapper.lambda().like(Region::getName, regionPageParam.getName());
        }
        if(ObjectUtil.isNotEmpty(regionPageParam.getParentCode())) {
            queryWrapper.lambda().eq(Region::getParentCode, regionPageParam.getParentCode());
        }
        if(ObjectUtil.isNotEmpty(regionPageParam.getParentName())) {
            queryWrapper.lambda().like(Region::getParentName, regionPageParam.getParentName());
        }
        if(ObjectUtil.isNotEmpty(regionPageParam.getSearchKey())) {
            queryWrapper.lambda().and(wrapper -> wrapper.like(Region::getCode, regionPageParam.getSearchKey()).or()
                    .like(Region::getName, regionPageParam.getSearchKey()));
        }
        queryWrapper.lambda().orderByAsc(Region::getParentCode).orderByAsc(Region::getOrderNum);
        if(ObjectUtil.isAllNotEmpty(regionPageParam.getSortField(), regionPageParam.getSortOrder())) {
            CommonSortOrderEnum.validate(regionPageParam.getSortOrder());
            queryWrapper.orderBy(true, regionPageParam.getSortOrder().equals(CommonSortOrderEnum.DESC.getValue()),
                    StrUtil.toUnderlineCase(regionPageParam.getSortField()));
        }
        queryWrapper.lambda().orderByDesc(Region::getCreateTime);
        return this.page(regionPageParam.createPage(), queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(RegionAddParam regionAddParam) {
        Region region = BeanUtil.toBean(regionAddParam, Region.class);
        this.validateParam(region);
        this.save(region);
    }
    //数据校验
    private void validateParam(Region region) {
        //1、如果id为空，则判断为添加，需要校验code唯一性
        if(ObjectUtil.isEmpty(region.getId())) {
            if(this.count(new LambdaQueryWrapper<Region>().eq(Region::getCode, region.getCode())) > 0) {
                throw new CommonException("编码重复，请重新输入");
            }
        }
        //2、如果id不为空，则判断为编辑，需要校验code唯一性
        if (ObjectUtil.isNotEmpty(region.getId())){
            if(this.count(new LambdaQueryWrapper<Region>().eq(Region::getCode, region.getCode()).ne(Region::getId, region.getId())) > 0) {
                throw new CommonException("编码重复，请重新输入");
            }
        }
        //3、其他校验:当父级编码不为空时，判断父级编码是否存在
        if (ObjectUtil.isNotEmpty(region.getParentCode())){
            if(this.count(new LambdaQueryWrapper<Region>().eq(Region::getCode, region.getParentCode())) <= 0) {
                throw new CommonException("父级编码不存在，请重新输入");
            }
        }
        //4、其他校验:当父级编辑码不为空时
        if (ObjectUtil.isNotEmpty(region.getParentCode())){
            //4.1判断当前区划代码是否符合父级编码的格式
            if(!region.getCode().startsWith(region.getParentCode())) {
                throw new CommonException("地区划代码不符合父级编码的格式");
            }
            //4.2判断当前区划代码的长度是否符合父级编码的格式，一般都是按省2位、市4位、县6位、镇9位村12位这种层级校验
            if (!lengthRules.contains(region.getParentCode().length())){
                throw new CommonException("父级划代码长度错误");
            }else if(lengthRules.get(lengthRules.indexOf(region.getParentCode().length())+1)!=region.getCode().length()){
                throw new CommonException("区划代码长度错误");
            }

        }
        //5、其他校验:code需要是数字类型
        if (!region.getCode().matches("^[0-9]+$")){
            throw new CommonException("编码格式错误,需要是数字类型");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(RegionEditParam regionEditParam) {
        Region region = this.queryEntity(regionEditParam.getId());
        StringBuilder sql=new StringBuilder();
        String oldCode = region.getCode();
        //修改的区划代码和之前的区划代码位数需要一直，只允许统计修改
        if (region.getCode().length() != regionEditParam.getCode().length()){
            throw new CommonException("只允许修改到父级同级的区划下");
        }
        sql.append(" code =").append(" REPLACE(code, ")
                .append("'").append(oldCode).append("',").append("'").append(regionEditParam.getCode()).append( "'),")
                        .append("parent_code =").append("REPLACE(parent_code,")
                        .append("'").append(oldCode).append("','").append(regionEditParam.getCode()).append("')");
        BeanUtil.copyProperties(regionEditParam, region);
        this.validateParam(region);
        this.updateById(region);
        //当前区划修改后，需要将其相关的子集也修改
        this.update(new UpdateWrapper<Region>().lambda().setSql(sql.toString())
                .like(Region::getParentCode, oldCode));
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<RegionIdParam> regionIdParamList) {
        // 执行删除
        this.removeByIds(CollStreamUtil.toList(regionIdParamList, RegionIdParam::getId));
    }

    @Override
    public Region detail(RegionIdParam regionIdParam) {
        return this.queryEntity(regionIdParam.getId());
    }

    @Override
    public Region queryEntity(Long id) {
        Region region = this.getById(id);
        if(ObjectUtil.isEmpty(region)) {
            throw new CommonException("行政区划不存在，id值为：{}", id);
        }
        return region;
    }

    @Override
    @SneakyThrows
    public void excelOut (HttpServletResponse response,RegionPageParam regionPageParam) {
        // 对返回头的设置 要写在返回数据写入前，
        // 在本方中就是要放到excle流导出前
        response.setCharacterEncoding("UTF-8");
        //客户端不缓存
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
        response.setHeader("Content-Disposition","attachment;filename=" + URLUtil.encode("行政区划") + ".xlsx");

        ExcelExporter
                .pojoClass(RegionExportVO.class)
                .rowService(this)
                .listParam(regionPageParam)
                .outputStream(response.getOutputStream())
                .export();
    }

    @Override
    @SneakyThrows
    @Transactional(rollbackFor = Exception.class)
    public void excelIn (MultipartFile file) {
        Map<String, String> headerMap = new LinkedHashMap<>();
        Field[] fields = ReflectUtil.getFields(RegionIntoBO.class, field -> field.getAnnotation(ExcelConfig.class) != null);
        Arrays.stream(fields).forEach( field -> headerMap.put(field.getAnnotation(ExcelConfig.class).name(), field.getName()));

        ExcelReader reader = ExcelUtil.getReader(file.getInputStream());
        reader.setHeaderAlias(headerMap);
        List<RegionIntoBO> list = reader.read(0, 1, RegionIntoBO.class);
        if (!CollectionUtils.isEmpty(list)){
            this.saveOrUpdateBatch(BeanUtil.copyToList(list, Region.class));
        }

    }

    @Override
    public List<Tree<Long>> tree() {
        List<Region> regionList = this.list();
        //将code 作为id
        regionList.forEach(region -> region.setId(Long.valueOf(region.getCode())));
        List<TreeNode<Long>> treeNodeList = regionList.stream().map(region ->
                        new TreeNode<>(Long.valueOf(region.getCode()),ObjectUtil.isEmpty(region.getParentCode())?0L:Long.valueOf(region.getParentCode()) ,
                                region.getName(), region.getOrderNum()).setExtra(JSONUtil.parseObj(region)))
                .collect(Collectors.toList());
        return TreeUtil.build(treeNodeList, 0L);
    }

    @Override
    public List<Tree<String>> orgTreeSelector() {
        List<Region> regionList = this.list();
        regionList.forEach(region -> region.setId(Long.valueOf(region.getCode())));

        List<TreeNode<String>> treeNodeList = regionList.stream().map(region ->
                        new TreeNode<>(region.getCode(), region.getParentCode(),
                                region.getName(), region.getOrderNum()))
                .collect(Collectors.toList());
        return TreeUtil.build(treeNodeList, null);
    }

    @Override
    public List<Region> selectListForExcelExport(CommonPageEntity listParam) {
        return Optional.of(this.page((RegionPageParam) listParam)).map(Page::getRecords).orElseGet(ArrayList::new);
    }
}
