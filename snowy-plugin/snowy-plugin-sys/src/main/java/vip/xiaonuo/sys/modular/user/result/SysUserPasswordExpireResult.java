package vip.xiaonuo.sys.modular.user.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;


/**
 * 用户密码过期结果类
 *
 * @author xuyuxiang
 * @date 2025/5/16 17:20
 **/
@Getter
@Setter
public class SysUserPasswordExpireResult {

    /** 密码是否过期 */
    @ApiModelProperty(value = "密码是否过期", position = 1)
    private Boolean expired;

    /** 提示消息 */
    @ApiModelProperty(value = "提示消息", position = 2)
    private String message;
} 