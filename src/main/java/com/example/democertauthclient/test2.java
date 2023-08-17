package com.example.democertauthclient;

import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyStore;
import java.security.SecureRandom;

@Slf4j
public class test2 {

    private static String KEY_STORE_PATH = "/tmp/client-keystore.jks";
    private static String TRUST_STORE_PATH = "/tmp/client-truststore.jks";
    private static String KEY_STORE_PWD = "password";
    private static String TRuST_STORE_PWD = "password";


    public static void main(String[] args) throws Exception {

        URL url = new URL("https://genny.openiam.com:8082/server/search/12345");
//        URL url = new URL("https://87.117.25.141:8082/server/search/12345");
//

        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        try (FileInputStream keyStoreFileInputStream = new FileInputStream(KEY_STORE_PATH)) {
            keyStore.load(keyStoreFileInputStream, KEY_STORE_PWD.toCharArray());
        }
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
        keyManagerFactory.init(keyStore, KEY_STORE_PWD.toCharArray());


        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        FileInputStream trustStoreFileInputStream = new FileInputStream(TRUST_STORE_PATH);
        trustStore.load(trustStoreFileInputStream, TRuST_STORE_PWD.toCharArray());
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
        trustManagerFactory.init(trustStore);


        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), new SecureRandom());

        HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setSSLSocketFactory(sslContext.getSocketFactory());
        con.setHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });

        int responseCode = con.getResponseCode();
        InputStream inputStream;
        if (responseCode == HttpURLConnection.HTTP_OK) {
            inputStream = con.getInputStream();
        } else {
            inputStream = con.getErrorStream();
        }

        // Process the response
        BufferedReader reader;
        String line = null;
        reader = new BufferedReader(new InputStreamReader(inputStream));
        while ((line = reader.readLine()) != null) {
            log.info(line);
        }

        inputStream.close();
    }
}
