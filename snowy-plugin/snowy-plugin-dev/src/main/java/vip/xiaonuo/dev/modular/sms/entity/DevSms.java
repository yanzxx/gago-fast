
package vip.xiaonuo.dev.modular.sms.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import vip.xiaonuo.common.pojo.CommonEntity;

/**
 * 短信实体
 *
 * @author xuyuxiang
 * @date 2022/2/23 18:27
 **/
@Getter
@Setter
@TableName("DEV_SMS")
public class DevSms extends CommonEntity {

    /** id */
    @ApiModelProperty(value = "主键", position = 1)
    private String id;

    /** 租户id */
    @ApiModelProperty(value = "租户id", position = 2)
    private String tenantId;

    /** 短信引擎 */
    @ApiModelProperty(value = "短信引擎", position = 3)
    private String engine;

    /** 手机号 */
    @ApiModelProperty(value = "手机号", position = 4)
    private String phoneNumbers;

    /** 短信签名 */
    @ApiModelProperty(value = "短信签名", position = 5)
    private String signName;

    /** 模板编码 */
    @ApiModelProperty(value = "模板编码", position = 6)
    private String templateCode;

    /** 发送参数 */
    @ApiModelProperty(value = "发送参数", position = 7)
    private String templateParam;

    /** 回执信息 */
    @ApiModelProperty(value = "回执信息", position = 8)
    private String receiptInfo;

    /** 扩展信息 */
    @ApiModelProperty(value = "扩展信息", position = 9)
    private String extJson;
}
