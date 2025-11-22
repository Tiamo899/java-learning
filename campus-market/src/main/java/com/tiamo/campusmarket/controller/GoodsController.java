package com.tiamo.campusmarket.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tiamo.campusmarket.dto.GoodsPublishDto;
import com.tiamo.campusmarket.entity.Goods;
import com.tiamo.campusmarket.entity.GoodsImage;
import com.tiamo.campusmarket.mapper.GoodsImageMapper;
import com.tiamo.campusmarket.mapper.GoodsMapper;
import com.tiamo.campusmarket.util.FileUploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Autowired private GoodsMapper goodsMapper;
    @Autowired private GoodsImageMapper imageMapper;
    @Autowired private FileUploadUtil fileUploadUtil;

    // 发布商品（支持多图）
    @PostMapping("/publish")
    public String publish(@RequestPart GoodsPublishDto dto) throws Exception {
        Long userId = 1L; // 先写死1，后面从token拿

        Goods goods = new Goods();
        goods.setUserId(userId);
        goods.setTitle(dto.getTitle());
        goods.setPrice(dto.getPrice());
        goods.setDescription(dto.getDescription());
        goods.setCategoryId(dto.getCategoryId());
        goodsMapper.insert(goods);

        // 上传图片
        if (dto.getImages() != null && !dto.getImages().isEmpty()) {
            for (int i = 0; i < dto.getImages().size(); i++) {
                MultipartFile file = dto.getImages().get(i);
                String url = fileUploadUtil.upload(file);

                GoodsImage img = new GoodsImage();
                img.setGoodsId(goods.getId());
                img.setUrl(url);
                img.setIsCover(i == 0 ? 1 : 0);  // 第一张是封面
                img.setSort(i);
                imageMapper.insert(img);
            }
        }
        return "发布成功！商品id：" + goods.getId();
    }

    // 分页条件查询
    @GetMapping("/list")
    public Page<Goods> list(@RequestParam(defaultValue = "1") int page,
                            @RequestParam(defaultValue = "10") int size,
                            String keyword) {
        Page<Goods> p = new Page<>(page, size);
        LambdaQueryWrapper<Goods> w = new LambdaQueryWrapper<>();
        w.eq(Goods::getStatus, 1); // 只查在售
        if (keyword != null && !keyword.isEmpty()) {
            w.like(Goods::getTitle, keyword);
        }
        w.orderByDesc(Goods::getCreateTime);
        return goodsMapper.selectPage(p, w);
    }
}