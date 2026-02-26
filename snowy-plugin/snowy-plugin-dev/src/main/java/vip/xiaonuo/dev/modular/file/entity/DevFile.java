
package vip.xiaonuo.dev.modular.file.entity;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import vip.xiaonuo.common.pojo.CommonEntity;
import vip.xiaonuo.common.prop.CommonProperties;
import vip.xiaonuo.dev.modular.file.enums.DevFileEngineTypeEnum;

/**
 * 文件实体
 *
 * @author xuyuxiang
 * @date 2022/2/23 18:27
 **/
@Getter
@Setter
@TableName("DEV_FILE")
public class DevFile extends CommonEntity {

    /** id */
    @ApiModelProperty(value = "主键", position = 1)
    private String id;

    /** 租户id */
    @ApiModelProperty(value = "租户id", position = 2)
    private String tenantId;

    /** 存储引擎 */
    @ApiModelProperty(value = "存储引擎", position = 3)
    private String engine;

    /** 存储桶 */
    @ApiModelProperty(value = "存储桶", position = 4)
    private String bucket;

    /** 文件名称 */
    @ApiModelProperty(value = "文件名称", position = 5)
    private String name;

    /** 文件后缀 */
    @ApiModelProperty(value = "文件后缀", position = 6)
    private String suffix;

    /** 文件大小kb */
    @ApiModelProperty(value = "文件大小kb", position = 7)
    private String sizeKb;

    /** 文件大小（格式化后） */
    @ApiModelProperty(value = "文件大小（格式化后）", position = 8)
    private String sizeInfo;

    /** 文件的对象名（唯一名称） */
    @ApiModelProperty(value = "文件的对象名（唯一名称）", position = 9)
    private String objName;

    /** 文件存储路径 */
    @ApiModelProperty(value = "文件存储路径", position = 10)
    private String storagePath;

    /** 文件下载路径 */
    @ApiModelProperty(value = "文件下载路径", position = 11)
    private String downloadPath;

    /** 文件下载路径 */
    @ApiModelProperty(value = "文件下载路径", position = 11)
    @TableField(exist = false)
    private String downloadUrl;

    /** 图片缩略图 */
    @ApiModelProperty(value = "图片缩略图", position = 12)
    private String thumbnail;

    /** 扩展信息 */
    @ApiModelProperty(value = "扩展信息", position = 13)
    private String extJson;

    public void setDownloadPath(String downloadPath) {
        this.downloadPath = downloadPath;
        if (this.isLocalEngine()){
            CommonProperties commonProperties = SpringUtil.getBean(CommonProperties.class);
            this.downloadUrl = StrUtil.removeSuffix(commonProperties.getDownloadUrlPrefix(), "/") + downloadPath;
        }

    }

    @JsonIgnore
    public boolean isLocalEngine(){
        return this.engine.equals(DevFileEngineTypeEnum.LOCAL.getValue());
    }
}
