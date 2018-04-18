package com.ye.goods.utils;

import com.ye.goods.utils.Properties.Properties;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

@Data
@Slf4j
public class FTPUtil {
    private Properties properties;
    private FTPClient ftpClient;
    private String ip;
    private String username;
    private String password;
    private String imgDirectory;

    public FTPUtil(Properties properties) {
        this.ip = properties.getFtp().getImageHost();
        this.username = properties.getFtp().getUsername();
        this.password = properties.getFtp().getPassword();
        this.imgDirectory = properties.getFtp().getImgDirectory();
    }

    public static boolean uploadFile(List<File> fileList, Properties properties) {
        FTPUtil ftpUtil = new FTPUtil(properties);
        return ftpUtil.uploadFile(fileList);
    }

    private boolean uploadFile(List<File> fileList) {
        String remotePath = this.imgDirectory;
        boolean uploaded = true;
        FileInputStream fileInputStream = null;

        if (connect(this.ip, this.username, this.password)) {
            try {
                ftpClient.changeWorkingDirectory(remotePath);
                ftpClient.setBufferSize(1024);
                ftpClient.setControlEncoding("UTF-8");
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                ftpClient.enterLocalPassiveMode();

                for (File file : fileList) {
                    fileInputStream = new FileInputStream(file);
                    ftpClient.storeFile(file.getName(), fileInputStream);
                }
            } catch (IOException e) {
                e.printStackTrace();
                uploaded = false;
                log.error("文件传输异常");
            } finally {
                try {
                    fileInputStream.close();
                    ftpClient.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else
            uploaded = false;
        return uploaded;
    }

    public boolean connect(String ip, String username, String password) {
        ftpClient = new FTPClient();
        boolean cResult = false;
        try {
            ftpClient.connect(ip);
            cResult = ftpClient.login(username, password);
        } catch (IOException e) {
            log.error("连接FTP服务器异常");
        }

        return cResult;
    }
}
