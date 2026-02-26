
package vip.xiaonuo.gen.prop;

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
@ConfigurationProperties(prefix = "gago.fast.gen")
public class GenProperties {

    /** 定义前端生成的目录 */
    private String frontBaseDir = "snowy-admin-web";

    /** 定义后端生成的目录 */
    private String backendBaseDir = "snowy-plugin";

}
