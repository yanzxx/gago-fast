package vip.xiaonuo.auth.modular.sso.body;

import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginTicket2_0Body {

    @NotBlank(message = "ticket不能为空")
    @ApiModelProperty(value = "票据", required = true, position = 1)
    private String ticket;
}
