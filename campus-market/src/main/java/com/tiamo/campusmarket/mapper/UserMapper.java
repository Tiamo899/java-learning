package com.tiamo.campusmarket.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tiamo.campusmarket.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}