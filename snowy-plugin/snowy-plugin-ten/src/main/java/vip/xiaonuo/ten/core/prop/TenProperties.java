
package vip.xiaonuo.ten.core.prop;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 多租户相关配置
 *
 * @author xuyuxiang
 * @date 2022/1/2 17:03
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "gago.fast.ten")
public class TenProperties {

    /** 是否开启多租户 */
    private Boolean enabled;

    /** 租户id隔离的租户模式下，需要忽略拼接租户id字段的表名称，逗号分割 */
    private String ignoreTableNames;

    /** 租户id隔离的租户模式下，租户字段名称（数据表需具备对应字段名称） */
    private String tenIdColumnName;

    /** 租户id隔离的租户模式下，默认租户ID */
    private String defaultTenId;

}
