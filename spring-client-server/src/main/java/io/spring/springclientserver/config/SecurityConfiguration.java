package io.spring.springclientserver.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.client.RestTemplate;

/**
 * @author 土味儿
 * Date 2022/5/13
 * @version 1.0
 */
@Configuration(proxyBeanMethods = false)
public class SecurityConfiguration {
    /***
     * 安全配置
     * @param http http
     * @return SecurityFilterChain
     * @throws Exception exception
     */
    @Bean
    SecurityFilterChain oauth2SecurityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeRequests(requests ->
                // 任何请求都需要认证
                requests.anyRequest().authenticated()
            )
                // oauth2三方登录
                .oauth2Login(Customizer.withDefaults())
                .oauth2Client()
            .and()
                .logout();
        return http.build();
    }

    @Bean
    public RestTemplate oauth2ClientRestTemplate(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder.build();
    }
}
