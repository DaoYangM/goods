package com.ye.goods.service.Impl;

import com.ye.goods.service.IFileService;
import com.ye.goods.utils.FTPUtil;
import com.ye.goods.utils.Properties.Properties;
import lombok.extern.slf4j.Slf4j;

import org.assertj.core.util.Lists;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class FileServiceImpl implements IFileService {

    private Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    private Properties properties;

    @Autowired
    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    @Override
    public List<String> upload(MultipartFile[] multipartFileList, String path) throws IOException {

        List<String> fileNameList = new ArrayList<>();
        List<File> fileList = new ArrayList<>();

        for (MultipartFile multipartFile: multipartFileList) {
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
            fileList.add(targetFile);
            multipartFile.transferTo(targetFile);
            fileNameList.add("ftp://"+properties.getFtp().getImageHost()+ "/" +
                    properties.getFtp().getImgDirectory() + "/"+targetFile.getName());
        }

        if (FTPUtil.uploadFile(Lists.newArrayList(fileList), properties))
            log.info("文件上传至服务器成功");

        for(File file: fileList) {
            if (file.delete())
                log.info("本地文件" + file.getName() + "删除成功");
        }

        return fileNameList;
    }
}
