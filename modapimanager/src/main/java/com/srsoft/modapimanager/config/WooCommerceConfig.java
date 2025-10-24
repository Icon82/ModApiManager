package com.srsoft.modapimanager.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;


import java.time.Duration;

@Configuration
public class WooCommerceConfig {

    @Value("${woocommerce.api.url}")
    private String apiUrl;

    @Value("${woocommerce.api.consumer-key}")
    private String consumerKey;

    @Value("${woocommerce.api.consumer-secret}")
    private String consumerSecret;

    @Bean
    public RestTemplate wooCommerceRestTemplate(RestTemplateBuilder builder) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(10));
        factory.setReadTimeout(Duration.ofSeconds(30));

        return builder
                .rootUri(apiUrl)
                .requestFactory(() -> factory)
                .additionalInterceptors(new BasicAuthenticationInterceptor(consumerKey, consumerSecret))
                .build();
    }


    public String getApiUrl() {
        return apiUrl;
    }

    public String getConsumerKey() {
        return consumerKey;
    }

    public String getConsumerSecret() {
        return consumerSecret;
    }
}