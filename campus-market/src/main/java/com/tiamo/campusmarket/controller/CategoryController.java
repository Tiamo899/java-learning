package com.tiamo.campusmarket.controller;

import com.tiamo.campusmarket.common.Result;
import com.tiamo.campusmarket.entity.Category;
import com.tiamo.campusmarket.mapper.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryMapper categoryMapper;

    // 获取所有分类
    @GetMapping("/list")
    public Result<List<Category>> list() {
        return Result.success(categoryMapper.selectList(null));
    }
}