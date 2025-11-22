package com.tiamo.campusmarket.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Component
public class FileUploadUtil {

    @Value("${app.upload.path}")
    private String uploadPath;

    public String upload(MultipartFile file) throws IOException {
        String original = file.getOriginalFilename();
        String ext = original.substring(original.lastIndexOf("."));
        String newName = UUID.randomUUID() + ext;

        File dir = new File(uploadPath);
        if (!dir.exists()) dir.mkdirs();

        file.transferTo(new File(uploadPath + newName));
        return "/images/" + newName;   // 我们后面用 nginx 静态映射
    }
}