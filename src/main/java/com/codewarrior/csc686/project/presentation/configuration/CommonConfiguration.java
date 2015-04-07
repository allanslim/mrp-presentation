package com.codewarrior.csc686.project.presentation.configuration;

import org.apache.commons.lang3.StringUtils;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class CommonConfiguration extends WebMvcConfigurerAdapter {

    public static final String ENCRYPTION_ALGORITHM = "PBEWithMD5AndTripleDES";

    @Bean
    public StandardPBEStringEncryptor standardPBEStringEncryptor() {

        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setAlgorithm(ENCRYPTION_ALGORITHM);

        String password = System.getProperty("SYSTEM_PASSWORD");
        if (StringUtils.isEmpty(password)) {
            password = System.getenv("SYSTEM_PASSWORD");
        }
        encryptor.setPassword(password);
        return encryptor;
    }
}
