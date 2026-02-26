
package vip.xiaonuo.dev.modular.email.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import vip.xiaonuo.common.pojo.CommonEntity;

/**
 * 邮件实体
 *
 * @author xuyuxiang
 * @date 2022/2/23 18:27
 **/
@Getter
@Setter
@TableName("DEV_EMAIL")
public class DevEmail extends CommonEntity {

    /** id */
    @ApiModelProperty(value = "主键", position = 1)
    private String id;

    /** 租户id */
    @ApiModelProperty(value = "租户id", position = 2)
    private String tenantId;

    /** 邮件引擎 */
    @ApiModelProperty(value = "邮件引擎", position = 3)
    private String engine;

    /** 发件人邮箱 */
    @ApiModelProperty(value = "发件人邮箱", position = 4)
    private String sendAccount;

    /** 发件人昵称 */
    @ApiModelProperty(value = "发件人昵称", position = 5)
    private String sendUser;

    /** 接收人 */
    @ApiModelProperty(value = "接收人", position = 6)
    private String receiveAccounts;

    /** 邮件主题 */
    @ApiModelProperty(value = "邮件主题", position = 7)
    private String subject;

    /** 邮件正文 */
    @ApiModelProperty(value = "邮件正文", position = 8)
    private String content;

    /** 标签名 */
    @ApiModelProperty(value = "标签名", position = 9)
    private String tagName;

    /** 模板名 */
    @ApiModelProperty(value = "模板名", position = 10)
    private String templateName;

    /** 发送参数 */
    @ApiModelProperty(value = "发送参数", position = 11)
    private String templateParam;

    /** 回执信息 */
    @ApiModelProperty(value = "回执信息", position = 12)
    private String receiptInfo;

    /** 扩展信息 */
    @ApiModelProperty(value = "扩展信息", position = 13)
    private String extJson;
}
