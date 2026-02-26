
package vip.xiaonuo;

import cn.hutool.core.util.NumberUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.RoundingMode;

/**
 * SpringBoot方式启动类
 *
 * @author xuyuxiang
 * @date 2021/12/18 16:57
 */
@Slf4j
@RestController
@SpringBootApplication(scanBasePackages = {"com.gago.bmp", "vip.xiaonuo"})
public class Application {

    /* 解决druid 日志报错：discard long time none received connection:xxx */
    static {
        System.setProperty("druid.mysql.usePingMethod","false");
    }

    /**
     * 主启动函数
     *
     * @author xuyuxiang
     * @date 2022/7/30 21:42
     */
    public static void main(String[] args) {
        long timeMillis = System.currentTimeMillis();
        SpringApplication springApplication = new SpringApplication(Application.class);
        springApplication.setBannerMode(Banner.Mode.OFF);
        springApplication.run(args);
        double seconds = NumberUtil.div(System.currentTimeMillis() - timeMillis, 1000, 2, RoundingMode.HALF_UP);
        log.info(">>> {} STARTING SUCCESS  耗时：{} 秒", Application.class.getSimpleName().toUpperCase(), seconds);
    }

    /**
     * 首页
     *
     * @author xuyuxiang
     * @date 2022/7/8 14:22
     **/
    @GetMapping("/")
    public String index() {
        return "WELCOME";
    }
}
