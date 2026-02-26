
package vip.xiaonuo.dev.modular.dev;

import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNode;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vip.xiaonuo.dev.api.DevApi;
import vip.xiaonuo.dev.modular.config.entity.DevConfig;
import vip.xiaonuo.dev.modular.config.enums.DevConfigCategoryEnum;
import vip.xiaonuo.dev.modular.config.service.DevConfigService;
import vip.xiaonuo.dev.modular.dict.entity.DevDict;
import vip.xiaonuo.dev.modular.dict.enums.DevDictCategoryEnum;
import vip.xiaonuo.dev.modular.dict.service.DevDictService;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 开发工具模块综合API接口实现类
 *
 * @author xuyuxiang
 * @date 2022/9/26 14:30
 **/
@Service
public class DevApiProvider implements DevApi {

    @Resource
    private DevDictService devDictService;

    @Resource
    private DevConfigService devConfigService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void initTenDataForCategoryId(String tenId, String tenName) {

        // ==============给租户初始化配置=================
        List<DevConfig> devConfigList = devConfigService.list(new LambdaQueryWrapper<DevConfig>().ne(DevConfig::getCategory,
                DevConfigCategoryEnum.BIZ_DEFINE.getValue())).stream().peek(devConfig -> {
            devConfig.setId(null);
            devConfig.setTenantId(tenId);
            // 非系统基础的设置为“未配置”
            if(!devConfig.getCategory().equals(DevConfigCategoryEnum.SYS_BASE.getValue())) {
                devConfig.setConfigValue("未配置");
            }
            // 将系统名称设置为租户名称
            if("SNOWY_SYS_NAME".equals(devConfig.getConfigKey())) {
                devConfig.setConfigValue(tenName);
            }
            // 将系统默认工作台数据设置为空
            if("SNOWY_SYS_DEFAULT_WORKBENCH_DATA".equals(devConfig.getConfigKey())) {
                devConfig.setConfigValue("{\"shortcut\":[]}");
            }
        }).collect(Collectors.toList());
        // 批量保存配置
        devConfigService.saveBatch(devConfigList);

        // ==============给租户初始化字典=================
        List<DevDict> devDictList = devDictService.list(new LambdaQueryWrapper<DevDict>().eq(DevDict::getCategory,
                DevDictCategoryEnum.FRM.getValue()));
        List<TreeNode<String>> treeNodeList = devDictList.stream().map(devDict ->
                new TreeNode<>(devDict.getId(), devDict.getParentId(),
                        devDict.getDictLabel(), devDict.getSortCode()).setExtra(JSONUtil.parseObj(devDict)))
                .collect(Collectors.toList());
        List<Tree<String>> treeList = TreeUtil.build(treeNodeList, "0");
        // 批量保存字典
        execRecursionInsertDict("0", treeList, tenId);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void removeTenDataForCategoryId(String tenId) {
        // ==============移除租户初始化的配置=================
        devConfigService.remove(new LambdaQueryWrapper<DevConfig>().eq(DevConfig::getTenantId, tenId));

        // ==============移除租户初始化的字典=================
        devDictService.remove(new LambdaQueryWrapper<DevDict>().eq(DevDict::getTenantId, tenId));
    }

    /**
     * 递归插入字典
     *
     * @author xuyuxiang
     * @date 2022/9/26 15:33
     **/
    private void execRecursionInsertDict(String parentId, List<Tree<String>> treeList, String tenId) {
        treeList.forEach(tree -> {
            DevDict devDict = JSONUtil.toBean(JSONUtil.parseObj(tree), DevDict.class);
            devDict.setId(null);
            devDict.setParentId(parentId);
            devDict.setTenantId(tenId);
            devDictService.save(devDict);
            String dictId = devDict.getId();
            List<Tree<String>> children = tree.getChildren();
            if(ObjectUtil.isNotEmpty(children)) {
                execRecursionInsertDict(dictId, children, tenId);
            }
        });
    }
}
