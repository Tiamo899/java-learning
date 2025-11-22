package com.tiamo.campusmarket.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;
import java.util.List;

@Data
public class GoodsPublishDto {
    private String title;
    private BigDecimal price;
    private String description;
    private Long categoryId;
    private List<MultipartFile> images;   // 多图上传
}