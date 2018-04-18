package com.ye.goods.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface IFileService {
    List<String> upload(MultipartFile[] multipartFile, String path) throws IOException;
}
