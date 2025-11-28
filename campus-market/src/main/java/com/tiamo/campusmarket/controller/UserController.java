package com.tiamo.campusmarket.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tiamo.campusmarket.dto.LoginDto;
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

    // Spring Security 推荐的加密方式（只实例化一次）
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    /** 注册 - JSON 方式 */
    @PostMapping("/register")
    public String register(@RequestBody RegisterDto dto) {
        // 检查用户名是否已存在
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, dto.getUsername());
        if (userMapper.selectCount(wrapper) > 0) {
            return "用户名已存在";
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(encoder.encode(dto.getPassword())); // 正确加密
        user.setNickname(dto.getNickname());
        user.setPhone(dto.getPhone());
        userMapper.insert(user);

        return "注册成功！用户ID：" + user.getId();
    }

    /** 登录 - JSON 方式（和注册保持一致，再也不用纠结 form 还是 json） */
    @PostMapping("/login")
    public String login(@RequestBody LoginDto dto) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, dto.getUsername());
        User user = userMapper.selectOne(wrapper);

        // 用户不存在或密码不匹配
        if (user == null || !encoder.matches(dto.getPassword(), user.getPassword())) {
            return "用户名或密码错误";
        }

        // 生成 JWT
        String token = JwtUtil.generateToken(user.getId());
        return "登录成功！token: " + token;
    }
}