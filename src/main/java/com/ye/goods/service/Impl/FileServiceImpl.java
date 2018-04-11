package com.ye.goods.service.Impl;

import com.ye.goods.service.IFileService;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class FileServiceImpl implements IFileService {

    private Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    @Override
    public String upload(MultipartFile multipartFile, String path) throws IOException {
        String fileName = multipartFile.getOriginalFilename();
        String fileExtensionName = fileName.substring(fileName.lastIndexOf(".") + 1);

        String finalFileName = UUID.randomUUID().toString() + "." + fileExtensionName;

        logger.info("Start uploading files, upload file name: {}, upload path: {}, new file name: {}"
                        , fileName, path, finalFileName);

        File fileDir = new File(path);
        if (!fileDir.exists()) {
             fileDir.setWritable(true);
            if (fileDir.mkdirs()) {
                logger.info("Create folder successfully");
            } else {
                logger.info("Failed to create folder");
            }
        }

        File targetFile = new File(path, finalFileName);
        multipartFile.transferTo(targetFile);
        targetFile.delete();

        return targetFile.getName();
    }
}
