package io.spring.springfirstresourceserver.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

/**
 * <p>认证失败处理器</p>
 *
 * @author 土味儿
 * Date 2022/5/11
 * @version 1.0
 */
public class SimpleAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @SneakyThrows
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        HashMap<String, String> map = new HashMap<>(2);
        if (authException instanceof InvalidBearerTokenException) {
            // 令牌失效
            System.out.println("token失效");
            //todo token处理逻辑
        }
        map.put("uri", request.getRequestURI());
        map.put("msg", "认证失败");
        if (response.isCommitted()) {
            return;
        }
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setStatus(HttpServletResponse.SC_ACCEPTED);
        response.setCharacterEncoding("utf-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ObjectMapper objectMapper = new ObjectMapper();
        String resBody = objectMapper.writeValueAsString(map);
        PrintWriter printWriter = response.getWriter();
        printWriter.print(resBody);
        printWriter.flush();
        printWriter.close();
    }
}
