package com.lezo.idober.config;

import lombok.Getter;
import lombok.Setter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class AppConfig {
    @Value("${idober_env_name}")
    private String envName;
    @Value("${app.solr.server_url}")
    private String sorlServerUrl;
    @Value("${idober_static_host}")
    private String staticHost;
}
