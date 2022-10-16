package io.spring.springfirstresourceserver.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

/**
 * <p>资源服务器配置</p>
 * 当解码器JwtDecoder存在时生效
 * proxyBeanMethods = false 每次调用都创建新的对象
 *
 * @author 土味儿
 * Date 2022/5/11
 * @version 1.0
 */
@ConditionalOnBean(JwtDecoder.class)
@Configuration(proxyBeanMethods = false)
public class OAuth2ResourceServerConfiguration {
    /**
     * 资源管理器配置
     *
     * @param http the http
     * @return the security filter chain
     * @throws Exception the exception
     */
    @Bean
    SecurityFilterChain jwtSecurityFilterChain(HttpSecurity http) throws Exception {
        // 拒绝访问处理器 401
        SimpleAccessDeniedHandler accessDeniedHandler = new SimpleAccessDeniedHandler();
        // 认证失败处理器 403
        SimpleAuthenticationEntryPoint authenticationEntryPoint = new SimpleAuthenticationEntryPoint();

        return http
                // security的session生成策略改为security不主动创建session即STALELESS
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
                // 对 /res1 的请求，需要 SCOPE_read 权限
                .authorizeRequests()
                .antMatchers("/res1").hasAnyAuthority("SCOPE_read","SCOPE_all")
                .antMatchers("/res2").hasAnyAuthority("SCOPE_write1","SCOPE_all")
                // 其余请求都需要认证
                .anyRequest().authenticated()
             .and()
                // 异常处理
                .exceptionHandling(exceptionConfigurer -> exceptionConfigurer
                        // 拒绝访问
                        .accessDeniedHandler(accessDeniedHandler)
                        // 认证失败
                        .authenticationEntryPoint(authenticationEntryPoint)
                )
                // 资源服务
                .oauth2ResourceServer(resourceServer -> resourceServer
                        .accessDeniedHandler(accessDeniedHandler)
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .jwt()
                )
                .build();
    }


    /**
     * JWT个性化解析
     *
     * @return
     */
    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
//        如果不按照规范  解析权限集合Authorities 就需要自定义key
//        jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName("scopes");
//        OAuth2 默认前缀是 SCOPE_     Spring Security 是 ROLE_
//        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        // 用户名 可以放sub
        jwtAuthenticationConverter.setPrincipalClaimName(JwtClaimNames.SUB);
        return jwtAuthenticationConverter;
    }
}
