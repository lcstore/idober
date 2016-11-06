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
	@Value("${qq_connect_appid}")
	private String qqConnectAppid;
	@Value("${qq_connect_appkey}")
	private String qqConnectAppkey;
	@Value("${qq_redirect_url}")
	private String qqRedirectUrl;

	@Value("${wb_app_key}")
	private String wbAppKey;
	@Value("${wb_app_secret}")
	private String wbAppSecret;
	@Value("${wb_redirect_url}")
	private String wbRedirectUrl;
	@Value("${idober_torrent_dir}")
	private String torrentDir;
}
