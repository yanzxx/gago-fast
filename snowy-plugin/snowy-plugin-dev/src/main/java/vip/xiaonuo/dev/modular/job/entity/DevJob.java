
package vip.xiaonuo.dev.modular.job.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import vip.xiaonuo.common.pojo.CommonEntity;
import vip.xiaonuo.dev.modular.job.enums.DevJobStatusEnum;

/**
 * 定时任务实体类
 *
 * @author xuyuxiang
 * @date 2022/8/5 10:38
 **/
@Getter
@Setter
@TableName("DEV_JOB")
public class DevJob extends CommonEntity {

    /** id */
    @ApiModelProperty(value = "id", position = 1)
    private String id;

    /** 租户id */
    @ApiModelProperty(value = "租户id", position = 2)
    private String tenantId;

    /** 名称 */
    @ApiModelProperty(value = "名称", position = 3)
    private String name;

    /** 编码 */
    @ApiModelProperty(value = "编码", position = 4)
    private String code;

    /** 分类 */
    @ApiModelProperty(value = "分类", position = 5)
    private String category;

    /** 任务类名 */
    @ApiModelProperty(value = "任务类名", position = 6)
    private String actionClass;

    /** cron表达式 */
    @ApiModelProperty(value = "cron表达式", position = 7)
    private String cronExpression;

    /** 任务状态 */
    @ApiModelProperty(value = "任务状态", position = 8)
    private String jobStatus;

    /** 排序码 */
    @ApiModelProperty(value = "排序码", position = 9)
    private Integer sortCode;

    /** 扩展信息 */
    @ApiModelProperty(value = "扩展信息", position = 10)
    @TableField(insertStrategy = FieldStrategy.IGNORED, updateStrategy = FieldStrategy.IGNORED)
    private String extJson;

    @ApiModelProperty(value = "一当前为开始时间，下一次匹配到的日期", position = 10)
    @TableField(exist = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String nextTime;

    @JsonIgnore
    public boolean ifRunning() {
        return jobStatus.equals(DevJobStatusEnum.RUNNING.getValue());
    }
}
