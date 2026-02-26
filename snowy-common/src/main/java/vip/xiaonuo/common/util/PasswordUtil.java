package vip.xiaonuo.common.util;

import vip.xiaonuo.common.exception.CommonException;

/**
 * 密码工具类
 *
 * @author xuyuxiang
 * @date 2024/1/8 17:00
 */
public class PasswordUtil {

    /**
     * 校验密码复杂度
     * 要求:密码必须包含数字、大小写字母、特殊字符，且长度至少8位
     *
     * @param password 密码
     */
    public static void validatePassword(String password) {
        if(!password.matches("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[~!@#$%^&*()_+|<>,.?/:;'\\[\\]{}\"]).{8,}$")) {
            throw new CommonException("密码复杂度不符合要求");
        }
    }
}
