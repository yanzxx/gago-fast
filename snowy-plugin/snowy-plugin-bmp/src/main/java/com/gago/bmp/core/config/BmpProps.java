package com.gago.bmp.core.config;

import cn.hutool.core.util.CharsetUtil;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "gago.bmp")
@Getter
@Setter
public class BmpProps {

    private String baseUrl;

    private String accessKeyId;

    private String accessKeySecret;

    /**
     * 中台项目编号
     */
    private String projectCode;

    private String regionName;

    public void setRegionName(String regionName) {
        this.regionName = CharsetUtil.convert(regionName, CharsetUtil.CHARSET_ISO_8859_1, CharsetUtil.CHARSET_UTF_8);
    }
}
