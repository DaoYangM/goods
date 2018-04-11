package com.ye.goods.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface IFileService {
    String upload(MultipartFile multipartFile, String path) throws IOException;
}
