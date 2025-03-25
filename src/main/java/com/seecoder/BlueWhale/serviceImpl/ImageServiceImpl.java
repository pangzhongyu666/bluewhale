package com.seecoder.BlueWhale.serviceImpl;

import com.seecoder.BlueWhale.exception.BlueWhaleException;
import com.seecoder.BlueWhale.service.ImageService;
import com.seecoder.BlueWhale.util.OssUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author: DingXiaoyu
 * @Date: 12:02 2023/12/13
 * 实现了上传文件的功能。
*/
@Service
public class ImageServiceImpl implements ImageService {

    @Autowired
    OssUtil ossUtil;
    private static final Logger logger = LoggerFactory.getLogger(ImageServiceImpl.class);

    @Override
    public String upload(MultipartFile file) {
        try {
            logger.info("上传图片");
            return ossUtil.upload(file.getOriginalFilename(),file.getInputStream());
        }catch (Exception e){
            e.printStackTrace();
            throw BlueWhaleException.fileUploadFail();
        }
    }
}
