package io.spring.springauthorizationserver.config;

import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.interfaces.RSAPublicKey;

/**
 * <p>授权服务器安全策略</p>
 *
 * @author 土味儿
 * Date 2022/5/10
 * @version 1.0
 */
@EnableWebSecurity(debug = true)
public class SpringSecurityConfig {
    /**
     * 配置 请求授权
     *
     * @param http
     * @return
     * @throws Exception
     */
    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        // 配置 请求授权
        http.
                authorizeRequests(authorizeRequests -> authorizeRequests.anyRequest().authenticated())
                .formLogin()
                .and()

                .logout()
                .logoutSuccessUrl("http://127.0.0.1:8000")
                .and()

                .oauth2ResourceServer()
                .jwt();
        return http.build();
    }

    /**
     * 模拟用户
     *
     * @return
     */
    @Bean
    UserDetailsService users() {
        UserDetails user = User.builder()
                .username("admin")
                .password("123456")
                .passwordEncoder(PasswordEncoderFactories.createDelegatingPasswordEncoder()::encode)
                .roles("USER").build();
        return new InMemoryUserDetailsManager(user);
    }

    /**
     * jwt解码器
     * 客户端认证授权后，需要访问user信息，解码器可以从令牌中解析出user信息
     *
     * @return
     */
    @SneakyThrows
    @Bean
    JwtDecoder jwtDecoder() {
        CertificateFactory certificateFactory = CertificateFactory.getInstance("x.509");
        // 读取cer公钥证书来配置解码器
        ClassPathResource resource = new ClassPathResource("myjks.cer");
        Certificate certificate = certificateFactory.generateCertificate(resource.getInputStream());
        RSAPublicKey publicKey = (RSAPublicKey) certificate.getPublicKey();
        return NimbusJwtDecoder.withPublicKey(publicKey).build();
    }
}
