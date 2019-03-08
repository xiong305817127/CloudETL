package com.ys.idatrix.metacube.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "om")
public class UniomPlatformConfig {

    private String ambariClusterUrl;

    private String url;

    private String username;

    private String password;
}