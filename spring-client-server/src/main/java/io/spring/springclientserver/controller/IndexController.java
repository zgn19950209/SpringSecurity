package io.spring.springclientserver.controller;

import com.nimbusds.jose.util.JSONObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * @author 土味儿
 * Date 2022/5/16
 * @version 1.0
 */
@Controller
public class IndexController {
    @Autowired
    RestTemplate restTemplate;

    @GetMapping("/")
    public String index(Model model) {
        // 从安全上下文中获取登录信息，返回给model
        Map<String, Object> map = new HashMap<>(2);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        map.put("name", auth.getName());
        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.stream().iterator();
        ArrayList<Object> authList = new ArrayList<>();
        while (iterator.hasNext()) {
            authList.add(iterator.next().getAuthority());
        }

        map.put("authorities", authList);
        model.addAttribute("user", JSONObjectUtils.toJSONString(map));
        return "index";
    }

    @GetMapping("/out")
    public void logout(HttpServletRequest request,
                       HttpServletResponse response) {

        // ========== 清理客户端 ===========
        // 清理客户端session
        request.getSession().invalidate();
        // 清理客户端安全上下文
        SecurityContextHolder.clearContext();

        // ========== 清理认证中心 ===========
        // 跳转至认证中心退出页面
        try {
            response.sendRedirect("http://os.com:9000/logout");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
