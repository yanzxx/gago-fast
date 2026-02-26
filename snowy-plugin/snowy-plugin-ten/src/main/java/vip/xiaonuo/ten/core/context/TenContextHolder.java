
package vip.xiaonuo.ten.core.context;

/**
 * 核心基于ThreadLocal的切换租户工具类
 *
 * @author xuyuxiang
 * @date 2022/3/11 10:55
 **/
public final class TenContextHolder {

    private static final ThreadLocal<String> TENANT_ID_HOLDER = new ThreadLocal<>();

    public static void put(String value) {
        TENANT_ID_HOLDER.set(value);
    }

    public static String get() {
        return TENANT_ID_HOLDER.get();
    }

    public static void remove() {
        TENANT_ID_HOLDER.remove();
    }
}
