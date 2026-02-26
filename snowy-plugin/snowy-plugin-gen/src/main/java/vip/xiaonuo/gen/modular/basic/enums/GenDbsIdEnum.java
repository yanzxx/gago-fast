
package vip.xiaonuo.gen.modular.basic.enums;

import lombok.Getter;

/**
 * 数据源ID枚举
 *
 * @author yubaoshan
 * @date 2022/10/28 9:57
 **/
@Getter
public enum GenDbsIdEnum {

    /** master主数据源 */
    MASTER("master");

    private final String value;

    GenDbsIdEnum(String value) {
        this.value = value;
    }
}
