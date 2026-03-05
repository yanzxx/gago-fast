package vip.xiaonuo.biz.modular.supervision.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 移动端首页头部信息
 */
@Getter
@Setter
public class BizMobileHomeHeaderResult {

    @ApiModelProperty("组织id")
    private String orgId;

    @ApiModelProperty("组织名称")
    private String orgName;

    @ApiModelProperty("刷新时间")
    private String refreshTime;
}
