package com.srsoft.modapimanager.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * Configurazione per l'integrazione con ZonWizard API
 * 
 * Questa configurazione crea un RestTemplate dedicato per le chiamate API a ZonWizard
 * con autenticazione Bearer Token e timeout configurabili.
 */
@Configuration
public class ZonWizardConfig {

    @Value("${zonwizard.api.url}")
    private String zonwizardApiUrl;

    @Value("${zonwizard.api.token}")
    private String zonwizardApiToken;

    @Value("${zonwizard.api.timeout.connect:10}")
    private int connectTimeout;

    @Value("${zonwizard.api.timeout.read:30}")
    private int readTimeout;

    /**
     * RestTemplate configurato per ZonWizard API con:
     * - Bearer Token authentication
     * - Timeout personalizzabili
     * - Headers predefiniti (Content-Type, Accept)
     * 
     * @param builder RestTemplateBuilder fornito da Spring Boot
     * @return RestTemplate configurato per ZonWizard
     */
    @Bean(name = "zonwizardRestTemplate")
    public RestTemplate zonwizardRestTemplate(RestTemplateBuilder builder) {
        // Configurazione timeout (compatibile Spring Boot 3.4+)
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(connectTimeout));
        factory.setReadTimeout(Duration.ofSeconds(readTimeout));

        return builder
                .requestFactory(() -> factory)
                .rootUri(zonwizardApiUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + zonwizardApiToken)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .defaultHeader(HttpHeaders.ACCEPT, "application/json")
                .build();
    }

    // Getter per uso nei service
    public String getZonwizardApiUrl() {
        return zonwizardApiUrl;
    }

    public String getZonwizardApiToken() {
        return zonwizardApiToken;
    }
}