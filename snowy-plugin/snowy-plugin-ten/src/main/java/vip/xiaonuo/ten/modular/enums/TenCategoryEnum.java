
package vip.xiaonuo.ten.modular.enums;

import lombok.Getter;
import vip.xiaonuo.common.exception.CommonException;

/**
 * 租户隔离分类枚举
 *
 * @author xuyuxiang
 * @date 2021/10/11 14:02
 **/
@Getter
public enum TenCategoryEnum {

    /**
     * 库隔离
     */
    DB("DB"),

    /**
     * 租户ID隔离
     */
    ID("ID");

    private final String value;

    TenCategoryEnum(String value) {
        this.value = value;
    }

    public static void validate(String value) {
        boolean flag = DB.getValue().equals(value) || ID.getValue().equals(value);
        if(!flag) {
            throw new CommonException("不支持的租户隔离分类：{}", value);
        }
    }
}
