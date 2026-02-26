
package vip.xiaonuo.ten.core.filter;

import cn.hutool.extra.spring.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import vip.xiaonuo.common.exception.CommonException;
import vip.xiaonuo.common.util.CommonFilterExceptionUtil;
import vip.xiaonuo.common.util.CommonServletUtil;
import vip.xiaonuo.dbs.api.DbsApi;
import vip.xiaonuo.ten.core.context.TenContextHolder;
import vip.xiaonuo.ten.core.prop.TenProperties;
import vip.xiaonuo.ten.modular.entity.TenStorage;
import vip.xiaonuo.ten.modular.enums.TenCategoryEnum;
import vip.xiaonuo.ten.modular.service.TenService;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * 动态租户解析过滤器
 *
 * @author xuyuxiang
 * @date 2022/3/11 16:22
 **/
@Slf4j
public class TenResolveFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
        try {
            TenProperties tenProperties = SpringUtil.getBean(TenProperties.class);
            DbsApi dbsApi = SpringUtil.getBean(DbsApi.class);
            // 如果开启了多租户
            if(tenProperties.getEnabled()) {
                // 获取到解析的租户
                TenStorage tenStorage = SpringUtil.getBean(TenService.class).getCurrentTen();
                // 获取租户id
                String tenantId = tenStorage.getId();
                if(tenantId.equals(tenProperties.getDefaultTenId())) {
                    // 执行切换租户的数据源为master
                    dbsApi.changeDataSource(dbsApi.getDefaultDataSourceName());
                    // 切换租户id为默认id
                    TenContextHolder.put(tenProperties.getDefaultTenId());
                } else {
                    if(TenCategoryEnum.DB.getValue().equals(tenStorage.getCategory())) {
                        // 执行切换租户的数据源
                        dbsApi.changeDataSource(tenStorage.getDbsName());
                        // 切换租户id为默认id
                        TenContextHolder.put(tenProperties.getDefaultTenId());
                    } else {
                        // 执行切换租户的数据源为master
                        dbsApi.changeDataSource(dbsApi.getDefaultDataSourceName());
                        // 切换租户id为其id
                        TenContextHolder.put(tenantId);
                    }
                }
            } else {
                // 执行切换租户的数据源为master
                dbsApi.changeDataSource(dbsApi.getDefaultDataSourceName());
                // 切换租户id为默认id
                TenContextHolder.put(tenProperties.getDefaultTenId());
            }
            // 继续执行
            chain.doFilter(request,response);
        } catch (Exception e) {
            // 处理filter中的异常
            if(e instanceof CommonException) {
                CommonException commonException = ((CommonException) e);
                commonException.setMsg(commonException.getMsg() + "，请求地址：" + CommonServletUtil.getRequest().getRequestURL());
            }
            CommonFilterExceptionUtil.handleFilterException(request, response, e);
        } finally {
            TenContextHolder.remove();
        }
    }
}
