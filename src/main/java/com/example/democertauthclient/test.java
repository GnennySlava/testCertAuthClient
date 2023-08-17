package com.example.democertauthclient;

import io.netty.handler.ssl.SslContextBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ResourceUtils;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.KeyStore;
import java.security.SecureRandom;

@Slf4j
public class test {

    private static String KEY_STORE_PATH = "src/main/resources/certs/client-keystore.jks";
    private static String TRUST_STORE_PATH = "src/main/resources/certs/client-truststore.jks";
    private static String KEY_STORE_PWD = "password";
    private static String TRuST_STORE_PWD = "password";


    public static void main(String[] args) throws Exception {

        String urlString = "https://localhost:8082/server";
        URL url = new URL(urlString);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

        KeyStore keyStore = KeyStore.getInstance("jks");
        KeyStore trustStore = KeyStore.getInstance("jks");
        try (FileInputStream keyStoreFileInputStream = new FileInputStream(ResourceUtils.getFile(KEY_STORE_PATH));
             FileInputStream trustStoreFileInputStream = new FileInputStream(ResourceUtils.getFile(TRUST_STORE_PATH))
        ) {

            keyStore.load(keyStoreFileInputStream, KEY_STORE_PWD.toCharArray());
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
            keyManagerFactory.init(keyStore, KEY_STORE_PWD.toCharArray());

            trustStore.load(trustStoreFileInputStream, TRuST_STORE_PWD.toCharArray());
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
            trustManagerFactory.init(trustStore);

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), new SecureRandom());


            connection.setSSLSocketFactory(sslContext.getSocketFactory());
            connection.setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    if (hostname.equals("localhost")) {
                        return true;
                    }
                    return false;
                }
            });

            connection.setRequestMethod("GET");
            connection.setDoInput(true);

            InputStream inputStream = connection.getInputStream();

            try(BufferedReader br = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                log.info(response.toString());
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
