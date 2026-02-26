package vip.xiaonuo.sys.modular.region.service;

import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.lang.tree.Tree;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;
import vip.xiaonuo.sys.entity.SysUser;
import vip.xiaonuo.sys.modular.org.param.SysOrgSelectorUserParam;
import vip.xiaonuo.sys.modular.region.entity.Region;
import vip.xiaonuo.sys.modular.region.param.RegionAddParam;
import vip.xiaonuo.sys.modular.region.param.RegionEditParam;
import vip.xiaonuo.sys.modular.region.param.RegionIdParam;
import vip.xiaonuo.sys.modular.region.param.RegionPageParam;

import java.util.List;

/**
 * 行政区划Service接口
 *
 * @author gago
 * @date  2025/08/26 15:08
 **/
public interface RegionService extends IService<Region> {

    /**
     * 获取行政区划分页
     *
     * @author gago
     * @date  2025/08/26 15:08
     */
    Page<Region> page(RegionPageParam regionPageParam);

    /**
     * 添加行政区划
     *
     * @author gago
     * @date  2025/08/26 15:08
     */
    void add(RegionAddParam regionAddParam);

    /**
     * 编辑行政区划
     *
     * @author gago
     * @date  2025/08/26 15:08
     */
    void edit(RegionEditParam regionEditParam);

    /**
     * 删除行政区划
     *
     * @author gago
     * @date  2025/08/26 15:08
     */
    void delete(List<RegionIdParam> regionIdParamList);

    /**
     * 获取行政区划详情
     *
     * @author gago
     * @date  2025/08/26 15:08
     */
    Region detail(RegionIdParam regionIdParam);

    /**
     * 获取行政区划详情
     *
     * @author gago
     * @date  2025/08/26 15:08
     **/
    Region queryEntity(Long id);

    /**
     * 导出行政区划数据
     *
     * @author gago
     * @date  2025/08/26 15:08
     **/

    void excelOut(HttpServletResponse response,RegionPageParam regionPageParam);

    /**
     * 导出行政区划数据
     *
     * @author gago
     * @date  2025/08/26 15:08
     **/
    void excelIn(MultipartFile multipartFile);


    /**
     * 获取组织树
     */
    List<Tree<Long>> tree();

    /**
     * 获取组织树选择器
     */
    List<Tree<String>> orgTreeSelector();

}
