package com.ye.goods.utils.Properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "com.ye")
@Data
public class Properties {
    private FTPProperties ftp;

    private AlipayProperties alipay;
}
