
package vip.xiaonuo.auth.core.util;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import vip.xiaonuo.auth.api.SaBaseLoginUserApi;
import vip.xiaonuo.auth.core.pojo.SaBaseClientLoginUser;

import java.util.List;

/**
 * C端登录用户工具类
 *
 * @author xuyuxiang
 * @date 2022/7/8 10:40
 **/
public class StpClientLoginUserUtil {

    /**
     * 获取当前C端登录用户
     *
     * @author xuyuxiang
     * @date 2022/7/8 10:41
     **/
    public static SaBaseClientLoginUser getClientLoginUser() {
        SaBaseClientLoginUser loginUser = (SaBaseClientLoginUser) StpClientUtil.getTokenSession().get("loginUser");
        if (ObjectUtil.isNotEmpty(loginUser)) {
            return loginUser;
        }
        // 兜底：token session未命中时按loginId回源
        SaBaseLoginUserApi clientLoginUserApi = SpringUtil.getBean("clientLoginUserApi", SaBaseLoginUserApi.class);
        loginUser = clientLoginUserApi.getClientUserById(StpClientUtil.getLoginIdAsString());
        if (ObjectUtil.isNotEmpty(loginUser)) {
            try {
                StpClientUtil.getTokenSession().set("loginUser", loginUser);
            } catch (Exception ignored) {
                // 忽略缓存异常，至少保证当前请求可用
            }
        }
        return loginUser;
    }

    /**
     * 获取当前C端登录用户的当前请求接口的数据范围（暂无数据范围）
     *
     * @author xuyuxiang
     * @date 2022/7/8 10:41
     **/
    public static List<String> getLoginUserDataScope() {
        return CollectionUtil.newArrayList();
    }
}
