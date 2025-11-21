package com.tiamo.campusmarket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())                    // 关闭csrf（前后端分离必须）
                .cors(cors -> cors.disable())                    // 先关cors，后面再配
                .authorizeHttpRequests(auth -> auth
                        // 放行所有我们写的接口（包括 OPTIONS 预检请求）
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/user/**", "/test/**", "/goods/**").permitAll()
                        .anyRequest().permitAll()                    // 暂时全部放行（开发阶段最方便）
                )
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable());

        return http.build();
    }
}