package com.tiamo.campusmarket.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tiamo.campusmarket.dto.RegisterDto;
import com.tiamo.campusmarket.entity.User;
import com.tiamo.campusmarket.mapper.UserMapper;
import com.tiamo.campusmarket.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserMapper userMapper;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    // 注册
    @PostMapping("/register")
    public String register(@RequestBody RegisterDto dto) {
        // 检查用户名是否重复
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, dto.getUsername());
        if (userMapper.selectCount(wrapper) > 0) {
            return "用户名已存在";
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(encoder.encode(dto.getPassword())); // 加密密码
        user.setNickname(dto.getNickname());
        user.setPhone(dto.getPhone());

        userMapper.insert(user);
        return "注册成功！用户id：" + user.getId();
    }

    // 登录
    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        User user = userMapper.selectOne(wrapper);

        if (user == null || !encoder.matches(password, user.getPassword())) {
            return "用户名或密码错误";
        }

        String token = JwtUtil.generateToken(user.getId());
        return "登录成功！token: " + token;
    }
}