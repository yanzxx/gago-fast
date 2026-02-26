
package vip.xiaonuo.dev.modular.message.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import vip.xiaonuo.common.pojo.CommonEntity;

/**
 * 站内信实体
 *
 * @author xuyuxiang
 * @date 2022/2/23 18:27
 **/
@Getter
@Setter
@TableName("DEV_MESSAGE")
public class DevMessage extends CommonEntity {

    /** id */
    @ApiModelProperty(value = "主键", position = 1)
    private String id;

    /** 租户id */
    @ApiModelProperty(value = "租户id", position = 2)
    private String tenantId;

    /** 分类 */
    @ApiModelProperty(value = "分类", position = 3)
    private String category;

    /** 主题 */
    @ApiModelProperty(value = "主题", position = 4)
    private String subject;

    /** 正文 */
    @ApiModelProperty(value = "正文", position = 5)
    private String content;

    /** 扩展信息 */
    @ApiModelProperty(value = "扩展信息", position = 6)
    private String extJson;
}
