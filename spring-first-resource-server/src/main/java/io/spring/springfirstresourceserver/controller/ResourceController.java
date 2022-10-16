package io.spring.springfirstresourceserver.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author 土味儿
 * Date 2022/5/18
 * @version 1.0
 */
@RestController
public class ResourceController {
    @GetMapping("/res1")
    public ResponseEntity getRes1(HttpServletRequest request) {
        return ResponseEntity.ok("服务A -> 资源1");
    }

    @GetMapping("/res2")
    public ResponseEntity getRes2(HttpServletRequest request) {
        return ResponseEntity.ok("服务A -> 资源2");
    }

}
