package com.tiamo.campusmarket.interceptor;

import com.tiamo.campusmarket.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.PrintWriter;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        System.out.println("=== JwtInterceptor 被触发 ===");
        System.out.println("请求路径: " + uri);

        // 公开接口直接放行
        if (uri.contains("/user/login") || uri.contains("/user/register") ||
                uri.contains("/goods/list") || uri.contains("/goods/detail") ||
                uri.contains("/category/list") || uri.contains("/error") || uri.startsWith("/test")) {
            System.out.println("公开接口，直接放行");
            return true;
        }

        String auth = request.getHeader("Authorization");
        System.out.println("前端传的 Authorization 头是: " + auth);   // ← 这一行决定生死！

        if (auth == null || !auth.startsWith("Bearer ")) {
            System.out.println("没有携带有效 token，被拦截");
            writeError(response, "请先登录");
            return false;
        }

        String token = auth.substring(7).trim();
        System.out.println("提取到的 token: " + token);

        try {
            Long userId = JwtUtil.getUserId(token);
            System.out.println("Token 验证通过！当前用户ID: " + userId);
            JwtUtil.setCurrentUser(userId);
            return true;
        } catch (ExpiredJwtException e) {
            System.out.println("Token 已过期");
            writeError(response, "登录已过期，请重新登录");
        } catch (SignatureException e) {
            System.out.println("Token 签名错误");
            writeError(response, "登录状态无效，请重新登录");
        } catch (Exception e) {
            e.printStackTrace();
            writeError(response, "登录状态异常");
        }
        return false;
    }

    private void writeError(HttpServletResponse resp, String msg) throws Exception {
        resp.setStatus(200);
        resp.setContentType(MediaType.APPLICATION_JSON_VALUE);
        resp.setCharacterEncoding("UTF-8");
        PrintWriter writer = resp.getWriter();
        writer.write("{\"code\":500,\"msg\":\"" + msg + "\",\"data\":null}");
        writer.flush();
    }
}