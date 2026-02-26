
package vip.xiaonuo.dbs.modular.enums;

import lombok.Getter;
import vip.xiaonuo.common.exception.CommonException;

/**
 * 数据源分类枚举
 *
 * @author xuyuxiang
 * @date 2022/7/6 22:21
 */
@Getter
public enum DbsCategoryEnum {

    /**
     * 主租户的数据源
     */
    MASTER("MASTER"),

    /**
     * 子租户的数据源
     */
    SLAVE("SLAVE");

    private final String value;

    DbsCategoryEnum(String value) {
        this.value = value;
    }

    public static void validate(String value) {
        boolean flag = MASTER.getValue().equals(value) || SLAVE.getValue().equals(value);
        if(!flag) {
            throw new CommonException("不支持的数据源分类：{}", value);
        }
    }
}
