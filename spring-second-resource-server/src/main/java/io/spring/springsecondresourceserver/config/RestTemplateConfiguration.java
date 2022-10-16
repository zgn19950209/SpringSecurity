package io.spring.springsecondresourceserver.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @author 土味儿
 * Date 2022/5/20
 * @version 1.0
 */
@Configuration(proxyBeanMethods = false)
public class RestTemplateConfiguration {
    @Bean
    public RestTemplate oauth2ClientRestTemplate(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder.build();
    }
}
