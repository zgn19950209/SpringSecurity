package io.spring.springauthorizationserver.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: 用户信息端点
 * @Created 2022/10/15 22:13
 **/
@RestController
@RequestMapping("/oauth2")
public class UserController {


    @GetMapping("/user")
    public ResponseEntity<Authentication> oauth2UserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new RuntimeException("无有效认证用户！");
        }
        return ResponseEntity.ok(authentication);
    }
}
