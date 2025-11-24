package com.tiamo.campusmarket.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tiamo.campusmarket.dto.RegisterDto;
import com.tiamo.campusmarket.entity.User;
import com.tiamo.campusmarket.mapper.UserMapper;
import com.tiamo.campusmarket.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired private UserMapper userMapper;
    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    // 注册 - 直接写响应，彻底绕过所有包装
    @PostMapping("/register")
    public void register(@RequestBody RegisterDto dto, HttpServletResponse response) throws IOException {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, dto.getUsername());
        if (userMapper.selectCount(wrapper) > 0) {
            response.setContentType("text/plain;charset=UTF-8");
            response.getWriter().write("用户名已存在");
            return;
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(encoder.encode(dto.getPassword()));
        user.setNickname(dto.getNickname());
        user.setPhone(dto.getPhone());
        userMapper.insert(user);

        response.setContentType("text/plain;charset=UTF-8");
        response.getWriter().write("注册成功！用户id：" + user.getId());
    }

    // 登录 - 直接写响应，彻底绕过所有包装
    @PostMapping("/login")
    public void login(@RequestParam String username, @RequestParam String password, HttpServletResponse response) throws IOException {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        User user = userMapper.selectOne(wrapper);

        if (user == null || !encoder.matches(password, user.getPassword())) {
            response.setContentType("text/plain;charset=UTF-8");
            response.getWriter().write("用户名或密码错误");
            return;
        }

        String token = JwtUtil.generateToken(user.getId());
        response.setContentType("text/plain;charset=UTF-8");
        response.getWriter().write("登录成功！token: " + token);
    }
}