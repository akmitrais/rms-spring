package com.mitrais.khotim.rmsspring.client.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ClientConfig {
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}
