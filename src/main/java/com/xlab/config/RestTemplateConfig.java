package com.xlab.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * Created by Administrator on 2017/11/30.
 */

@Configuration
public class RestTemplateConfig {

    @Bean
    public SimpleClientHttpRequestFactory httpClientFactory() {
        SimpleClientHttpRequestFactory httpRequestFactory = new SimpleClientHttpRequestFactory();
        httpRequestFactory.setReadTimeout(50000);
        httpRequestFactory.setConnectTimeout(50000);
        return httpRequestFactory;
    }
    @Bean
    public RestTemplate restTemplate(SimpleClientHttpRequestFactory httpClientFactory) {
        RestTemplate restTemplate = new RestTemplate(httpClientFactory);
        return restTemplate;
    }
}
