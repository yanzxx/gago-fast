
package vip.xiaonuo.sys.modular.region.enums;

import lombok.Getter;

/**
 * 行政区划枚举
 *
 * @author gago
 * @date  2025/08/26 15:08
 **/
@Getter
public enum RegionEnum {

    /** 测试 */
    TEST("TEST");

    private final String value;

    RegionEnum(String value) {
        this.value = value;
    }
}
