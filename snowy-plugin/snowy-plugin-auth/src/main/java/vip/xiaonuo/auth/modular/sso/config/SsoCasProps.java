package vip.xiaonuo.auth.modular.sso.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "gago.sso.cas")
public class SsoCasProps {

    private String baseUrl;

    private String service;
}
