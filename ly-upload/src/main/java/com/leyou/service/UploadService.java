package com.leyou.service;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.leyou.common.enums.ExceptionEnums;
import com.leyou.common.exception.LyException;
import com.leyou.config.UploadProperties;
import com.leyou.controller.UploadController;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.List;

@Service
//至少要@Component加入组件
@Slf4j
@EnableConfigurationProperties(UploadProperties.class)
public class UploadService {
    @Autowired
    private UploadProperties prop;

    //private static final Logger logger = LoggerFactory.getLogger(UploadController.class);//用@Slf4j代替

    @Autowired
    private FastFileStorageClient fileStorageClient;
    // 支持的文件类型
    //private static final List<String> suffixes = Arrays.asList("image/png", "image/jpeg");

    public String upload(MultipartFile file) {
        try {
            // 1、图片信息校验
            // 1)校验文件类型
            String type = file.getContentType();
            if (!prop.getSuffixes().contains(type)) {
                log.info("上传失败，文件类型不匹配：{}", type);
                throw new LyException(ExceptionEnums.FILE_UPLOAD_ERROR);
            }
            // 2)校验图片内容
            BufferedImage image = ImageIO.read(file.getInputStream());
            if (image == null) {
                log.info("上传失败，文件内容不符合要求");
                throw new LyException(ExceptionEnums.FILE_UPLOAD_ERROR);
            }
            // 2、保存图片
//            String extension=file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")+1);
            String extension= StringUtils.substringAfterLast(file.getOriginalFilename(),".");
            StorePath storePath = fileStorageClient.uploadFile(file.getInputStream(), file.getSize(), extension, null);
            String fullPath = storePath.getFullPath();
            return prop.getBase_url()+fullPath;
        } catch (Exception e) {
            return null;
        }
    }
}
