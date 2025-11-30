package com.tiamo.campusmarket.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tiamo.campusmarket.common.Result;
import com.tiamo.campusmarket.dto.GoodsPublishDto;
import com.tiamo.campusmarket.entity.Goods;
import com.tiamo.campusmarket.entity.GoodsImage;
import com.tiamo.campusmarket.mapper.GoodsImageMapper;
import com.tiamo.campusmarket.mapper.GoodsMapper;
import com.tiamo.campusmarket.util.FileUploadUtil;
import com.tiamo.campusmarket.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Autowired private GoodsMapper goodsMapper;
    @Autowired private GoodsImageMapper imageMapper;
    @Autowired private FileUploadUtil fileUploadUtil;
    @Autowired private RedisTemplate<String, Object> redisTemplate;

    // ✅ 修复后的发布商品接口
    @PostMapping("/publish")
    public Result<String> publish(
            @RequestPart("dto") GoodsPublishDto dto,
            @RequestPart(value = "images", required = false) List<MultipartFile> images
    ) {
        System.out.println("=== 发布商品接口被调用 ===");
        System.out.println("DTO: " + dto);
        System.out.println("图片数量: " + (images == null ? 0 : images.size()));

        Long userId = JwtUtil.getCurrentUser();
        if (userId == null) return Result.error("请先登录");

        // 插入商品基本信息
        Goods goods = new Goods();
        goods.setUserId(userId);
        goods.setTitle(dto.getTitle());
        goods.setPrice(dto.getPrice());
        goods.setDescription(dto.getDescription());
        goods.setCategoryId(dto.getCategoryId());
        goodsMapper.insert(goods);

        System.out.println("商品插入成功，ID: " + goods.getId());

        // 处理图片上传
        if (images != null && !images.isEmpty()) {
            for (int i = 0; i < images.size(); i++) {
                MultipartFile file = images.get(i);
                if (file.isEmpty()) {
                    System.out.println("第 " + (i + 1) + " 张图片为空，跳过");
                    continue;
                }

                try {
                    String url = fileUploadUtil.upload(file);
                    System.out.println("第 " + (i + 1) + " 张图片上传成功: " + url);

                    GoodsImage img = new GoodsImage();
                    img.setGoodsId(goods.getId());
                    img.setUrl(url);
                    img.setIsCover(i == 0 ? 1 : 0);
                    img.setSort(i);
                    imageMapper.insert(img);
                } catch (IOException e) {
                    e.printStackTrace();
                    return Result.error("第 " + (i + 1) + " 张图片上传失败: " + e.getMessage());
                }
            }
        }

        return Result.success("发布成功！商品ID: " + goods.getId());
    }

    // 其他方法保持不变...
    @GetMapping("/list")
    public Result<Page<Goods>> list(@RequestParam(defaultValue = "1") int page,
                                    @RequestParam(defaultValue = "10") int size,
                                    String keyword, Long categoryId) {
        Page<Goods> p = new Page<>(page, size);
        LambdaQueryWrapper<Goods> w = new LambdaQueryWrapper<>();
        w.eq(Goods::getStatus, 1);
        if (keyword != null && !keyword.isEmpty()) w.like(Goods::getTitle, keyword);
        if (categoryId != null && categoryId != 0) w.eq(Goods::getCategoryId, categoryId);
        w.orderByDesc(Goods::getCreateTime);
        return Result.success(goodsMapper.selectPage(p, w));
    }

    @GetMapping("/detail/{id}")
    public Result<Goods> detail(@PathVariable Long id) {
        Goods goods = goodsMapper.selectById(id);
        if (goods == null) return Result.error("商品不存在");

        redisTemplate.opsForValue().increment("goods:views:" + id);
        Object viewsObj = redisTemplate.opsForValue().get("goods:views:" + id);
        int views = viewsObj == null ? 0 : Integer.parseInt(viewsObj.toString());
        goods.setViews(views);

        return Result.success(goods);
    }

    @GetMapping("/my/publish")
    public Result<List<Goods>> myPublish() {
        Long userId = JwtUtil.getCurrentUser();
        if (userId == null) return Result.error("请先登录");

        LambdaQueryWrapper<Goods> w = new LambdaQueryWrapper<>();
        w.eq(Goods::getUserId, userId);
        w.orderByDesc(Goods::getCreateTime);

        return Result.success(goodsMapper.selectList(w));
    }
}