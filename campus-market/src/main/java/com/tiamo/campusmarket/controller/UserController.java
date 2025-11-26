package com.tiamo.campusmarket.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tiamo.campusmarket.dto.RegisterDto;
import com.tiamo.campusmarket.entity.User;
import com.tiamo.campusmarket.mapper.UserMapper;
import com.tiamo.campusmarket.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired private UserMapper userMapper;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    // 注册 - 强制返回纯文本，最稳版本！
    @PostMapping(value = "/register", produces = MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8")
    public String register(@RequestBody RegisterDto dto) {
        LambdaQueryWrapper<User> w = new LambdaQueryWrapper<>();
        w.eq(User::getUsername, dto.getUsername());
        if (userMapper.selectCount(w) > 0) {
            return "用户名已存在";
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(encoder.encode(dto.getPassword()));
        user.setNickname(dto.getNickname());
        user.setPhone(dto.getPhone());
        userMapper.insert(user);
        return "注册成功！用户id：" + user.getId();
    }

    // 登录 - 强制返回纯文本，最稳版本！
    @PostMapping(value = "/login", produces = MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8")
    public String login(@RequestParam String username, @RequestParam String password) {
        LambdaQueryWrapper<User> w = new LambdaQueryWrapper<>();
        w.eq(User::getUsername, username);
        User user = userMapper.selectOne(w);

        if (user == null || !encoder.matches(password, user.getPassword())) {
            return "用户名或密码错误";
        }

        String token = JwtUtil.generateToken(user.getId());
        return "登录成功！token: " + token;
    }
}