package com.scau.fileservice.serviceImpl;

import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.web.multipart.MultipartFile;
import service.PictureService;

/**
 * @program: pdd
 * @description: 图片管理服务
 * @create: 2020-12-06 00:40
 **/
@DubboService
public class PictureServiceImpl implements PictureService {
    @Override
    public String uploadPicture(MultipartFile file) {
        return null;
    }
}
