
package vip.xiaonuo.auth.core.enums;

import lombok.Getter;
import vip.xiaonuo.common.exception.CommonException;

/**
 * 登录设备类型枚举
 *
 * @author xuyuxiang
 * @date 2021/10/11 14:02
 **/
@Getter
public enum AuthTerminalTypeEnum {

    /**
     * PC端
     */
    PC("PC", "PC端"),

    /**
     * 移动端
     */
    APP("APP", "移动端"),

    /**
     * 小程序端
     */
    MINI("MINI", "小程序端");

    private final String value;

    private final String desc;

    AuthTerminalTypeEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static void validate(String value) {
        boolean flag = false;
        for (AuthTerminalTypeEnum authTerminalTypeEnum : AuthTerminalTypeEnum.values()) {
            if (authTerminalTypeEnum.getValue().equals(value)) {
                flag = true;
                break;
            }
        }

        if(!flag) {
            throw new CommonException("不支持的登录设备类型：{}", value);
        }
    }
}
