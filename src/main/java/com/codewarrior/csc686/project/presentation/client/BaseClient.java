package com.codewarrior.csc686.project.presentation.client;

import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;

public class BaseClient {

    public static final String CRYTOGRAPHY_STANDARD_PROTOCOL = "JKS";
    public static final String SCHEME = "https";


    @Value("classpath:${client.keystore}")
    private Resource clientKeyStore;

    @Value("${middle.tier.host}")
    protected String encryptedMiddleTierHost;

    protected String middleTierHost;

    @Value("${keystore.password}")
    private String encryptedPassword;

    @Autowired
   	private StandardPBEStringEncryptor standardPBEStringEncryptor;

    private SSLContextBuilder builder;

    @PostConstruct
    public void initBuilder() throws KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException, IOException, CertificateException {

        middleTierHost = standardPBEStringEncryptor.decrypt(encryptedMiddleTierHost);

        if (builder == null) {
            builder = new SSLContextBuilder();

            String password = standardPBEStringEncryptor.decrypt(encryptedPassword);

            final KeyStore keyStore = KeyStore.getInstance(CRYTOGRAPHY_STANDARD_PROTOCOL);
            keyStore.load(clientKeyStore.getInputStream(), password.toCharArray());




            builder.loadKeyMaterial(keyStore, password.toCharArray());
            builder.loadTrustMaterial(keyStore);


        }
    }

    protected RestTemplate createRestTemplate() {

        try {

            HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory = buildClientHttpRequestFactory();

            return new RestTemplate(httpComponentsClientHttpRequestFactory);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        return null;

    }

    private HttpComponentsClientHttpRequestFactory buildClientHttpRequestFactory() throws NoSuchAlgorithmException, KeyManagementException {

        SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(builder.build());


        final Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create().register(SCHEME, sslConnectionSocketFactory).register("HTTP", new PlainConnectionSocketFactory()).build();


        PoolingHttpClientConnectionManager poolingHttpClientConnectionManager = new PoolingHttpClientConnectionManager(registry);

        poolingHttpClientConnectionManager.setDefaultMaxPerRoute(10);
        poolingHttpClientConnectionManager.setMaxTotal(10);


        return new HttpComponentsClientHttpRequestFactory(HttpClientBuilder.create().setConnectionManager(poolingHttpClientConnectionManager).build());


    }
}
