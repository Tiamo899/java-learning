package com.tiamo.campusmarket.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data
@TableName("goods_image")
public class GoodsImage {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long goodsId;
    private String url;
    private Integer isCover = 0;
    private Integer sort = 0;
}