package com.jungwuk.klasuploader;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.*;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.*;
import java.util.*;
import javax.crypto.*;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;

public class ApiManager {
    @Getter
    private static final ApiManager instance = new ApiManager();

    public static final String klasHome = "https://klas.kw.ac.kr/";
    private static final String loginSecurity = klasHome + "usr/cmn/login/LoginSecurity.do";
    private static final String loginConfirm = klasHome + "usr/cmn/login/LoginConfirm.do";

    private Map<String, String> cookies;

    private ApiManager () {
        this.cookies = new HashMap<>();
    }

    private String postRequest(String apiUrl, String data, boolean saveCookies) throws IOException {
        return postRequest(apiUrl, data, saveCookies, "application/json");
    }

    private String postRequest(String apiUrl, String data, boolean saveCookies, String contentType) throws IOException {
        URL url = new URL(apiUrl);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", contentType + "; charset=UTF-8");
        connection.setRequestProperty("Accept", "*/*");
        connection.setRequestProperty("Cookie", getCookies());
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = data.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            if (saveCookies) {
                String setCookie = connection.getHeaderField("Set-Cookie");
                if (setCookie != null) {
                    saveCookies(setCookie);
                }
            }
            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                return response.toString();
            }
        } else {
            throw new IOException("Post 요청 실패: " + responseCode);
        }
    }

    private String getRequest(String apiUrl, boolean saveCookies) throws IOException {
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "*/*");
        connection.setRequestProperty("Cookie", getCookies());

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            if (saveCookies) {
                String setCookie = connection.getHeaderField("Set-Cookie");
                if (setCookie != null) {
                    saveCookies(setCookie);
                }
            }
            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                return response.toString();
            }
        } else {
            throw new IOException("Get 요청 실패: " + responseCode);
        }
    }

    public ResponseResult loginToKlas(String id, String pw) throws Exception {
        getRequest(klasHome, true);
        String securityResponse = postRequest(loginSecurity, "{}", true);
        JsonObject securityJson = JsonParser.parseString(securityResponse).getAsJsonObject();

        String publicKeyStr = securityJson.get("publicKey").getAsString();
        RSAPublicKey publicKey = Utils.parsePublicKey(publicKeyStr);

        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        String loginData = String.format("{\"loginId\":\"%s\",\"loginPwd\":\"%s\",\"storeIdYn\":true}", id, pw);
        byte[] encryptedLoginData = cipher.doFinal(loginData.getBytes());
        String loginToken = Base64.getEncoder().encodeToString(encryptedLoginData);

        String loginRequestData = String.format("{\"loginToken\":\"%s\",\"redirectUrl\":\"\",\"redirectTabUrl\":\"\"}", loginToken);
        String loginResponse = postRequest(loginConfirm, loginRequestData, true);

        JsonObject loginJson = JsonParser.parseString(loginResponse).getAsJsonObject();

        boolean success = loginJson.get("redirect").getAsBoolean();
        return new ResponseResult(success, loginJson.toString());
    }


    private void saveCookies(String setCookie) {
        String[] cookieArray = setCookie.split(";");
        for (String cookie : cookieArray) {
            String[] cookiePair = cookie.split("=");
            if (cookiePair.length == 2) {
                cookies.put(cookiePair[0].trim(), cookiePair[1].trim());
            }
        }
    }

    public String getCookies() {
        StringBuilder cookieBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : cookies.entrySet()) {
            cookieBuilder.append(entry.getKey()).append("=").append(entry.getValue()).append("; ");
        }
        return cookieBuilder.toString();
    }

    @Getter
    public class ResponseResult {
        private final boolean success;
        private final String data;

        public ResponseResult(boolean success, String data) {
            this.success = success;
            this.data = data;
        }
    }
}