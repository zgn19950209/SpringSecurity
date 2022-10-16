package io.spring.springsecondresourceserver.controller;

import com.nimbusds.jose.util.JSONObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 土味儿
 * Date 2022/5/18
 * @version 1.0
 */
@RestController
public class ResourceController {
    @Resource
    private RestTemplate restTemplate;

    @GetMapping("/res1")
    public ResponseEntity getRes1(HttpServletRequest request) throws ParseException {
        return ResponseEntity.ok(getServer("http://127.0.0.1:8001/res2", request));
    }

    @GetMapping("/res2")
    public ResponseEntity getRes2(HttpServletRequest request) throws ParseException {
        return ResponseEntity.ok(getServer("http://127.0.0.1:8001/res2", request));
    }

    /**
     * 请求资源
     *
     * @param url
     * @param request
     * @return
     */
    private String getServer(String url,
                             HttpServletRequest request) throws ParseException {
        // ======== 1、从session中取token ========
        HttpSession session = request.getSession();
        String token = (String) session.getAttribute("micro-token");

        // ======== 2、请求token ========
        // 先查session中是否有token；session中没有
        if (StringUtils.isEmpty(token)) {
            // ===== 去认证中心申请 =====
            // 对id及密钥加密
            byte[] userpass = Base64.encodeBase64(("micro_service:123456").getBytes());
            String str = "";
            try {
                str = new String(userpass, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }


            // 请求头
            HttpHeaders headers1 = new HttpHeaders();
            // 组装请求头
            headers1.add("Authorization", "Basic " + str);
            // 请求体
            HttpEntity<Object> httpEntity1 = new HttpEntity<>(headers1);
            // 响应体
            ResponseEntity<String> responseEntity1 = null;
            try {
                // 发起申请令牌请求
                responseEntity1 = restTemplate.exchange("http://os.com:9000/oauth2/token?grant_type=client_credentials", HttpMethod.POST, httpEntity1, String.class);
            } catch (RestClientException e) {
                //
                System.out.println("令牌申请失败");
            }

            // 令牌申请成功
            if (responseEntity1 != null) {
                // 解析令牌
                //String t = JSON.parseObject(responseEntity1.getBody(), MyAuth.class).getAccess_token();
                Map<String, Object> resMap = JSONObjectUtils.parse((responseEntity1.getBody()));
                String t = resMap.get("access_token").toString();
                // 存入session
                session.setAttribute("micro-token", t);
                // 赋于token变量
                token = t;
            }
        }

        // ======== 3、请求资源 ========
        // 请求头
        HttpHeaders headers2 = new HttpHeaders();
        // 组装请求头
        headers2.add("Authorization", "Bearer " + token);
        // 请求体
        HttpEntity<Object> httpEntity2 = new HttpEntity<>(headers2);
        // 响应体
        ResponseEntity<String> responseEntity2;
        try {
            // 发起访问资源请求
            responseEntity2 = restTemplate.exchange(url, HttpMethod.GET, httpEntity2, String.class);
        } catch (RestClientException e) {
            // 令牌失效(认证失效401) --> 清除session
            // e.getMessage() 信息格式：
            // 401 : "{"msg":"认证失败","uri":"/res2"}"
            String str = e.getMessage();
            // 判断是否含有 401
            if (StringUtils.contains(str, "401")) {
                // 如果有401，把session中 micro-token 的值设为空
                session.setAttribute("micro-token", "");
            }
            // 取两个括号中间的部分（包含两个括号）
            return str.substring(str.indexOf("{"), str.indexOf("}") + 1);
        }
        // 返回
        return responseEntity2.getBody();
    }
}

/*@Data
class MyAuth {
    private String access_token;
    private String scope;
    private String token_type;
    private long expires_in;
}*/
