package com.jungwuk.klasuploader.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jungwuk.klasuploader.ApiManager;
import com.jungwuk.klasuploader.dialog.SimpleInformationDialog;

import java.awt.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KlasDownloadManager {
    private static final String userUploadFile = ApiManager.klasHome + "common/file/UserUploadFile.do";
    private static final int CHUNK_SIZE = 20 * 1024 * 1024; // 20MB
    public static final String DATA_FILE = "file_database.json";


    private String uploadChunk(byte[] chunk, String fileName, int partNumber) throws IOException {
        URL url = new URL(userUploadFile);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");

        // Setting headers
        connection.setRequestProperty("Accept", "application/json, text/plain, */*");
        connection.setRequestProperty("Connection", "keep-alive");
        connection.setRequestProperty("Cookie", ApiManager.getInstance().getCookies());
        connection.setRequestProperty("Origin", "https://klas.kw.ac.kr");
        connection.setRequestProperty("Referer", "https://klas.kw.ac.kr/std/lis/evltn/TaskUpdateStdPage.do");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36");
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=----WebKitFormBoundaryU5e9BAf1kYq4LtKK");

        String boundary = "----WebKitFormBoundaryU5e9BAf1kYq4LtKK";

        try (OutputStream outputStream = connection.getOutputStream();
             PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, "UTF-8"), true)) {

            writer.append("--").append(boundary).append("\r\n");
            writer.append("Content-Disposition: form-data; name=\"storageId\"\r\n\r\nCLS_STU_TASK\r\n");
            writer.append("--").append(boundary).append("\r\n");
            writer.append("Content-Disposition: form-data; name=\"attachId\"\r\n\r\n\r\n");
            writer.append("--").append(boundary).append("\r\n");
            writer.append("Content-Disposition: form-data; name=\"params\"\r\n\r\n{ 'ordseq' : '2' }\r\n");
            writer.append("--").append(boundary).append("\r\n");
            writer.append("Content-Disposition: form-data; name=\"files[0]\"; filename=\"").append(fileName).append("_part").append(String.valueOf(partNumber)).append("\"\r\n");
            writer.append("Content-Type: application/octet-stream\r\n\r\n");
            writer.flush();

            outputStream.write(chunk);
            outputStream.flush();

            writer.append("\r\n--").append(boundary).append("--\r\n");
            writer.flush();
        }

        int responseCode = connection.getResponseCode();
        System.out.println("Response Code: " + responseCode);

        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (InputStream responseStream = connection.getInputStream()) {
                return new String(responseStream.readAllBytes());
            }
        } else {
            try (InputStream errorStream = connection.getErrorStream()) {
                String errorResponse = new String(errorStream.readAllBytes());
                System.err.println("Error Response: " + errorResponse);
                throw new IOException("Server returned non-OK status: " + responseCode + " - " + errorResponse);
            }
        }

    }

    public List<String> uploadFileInChunks(File file, Window window) throws IOException {
        List<String> responses = new ArrayList<>();
        byte[] buffer = new byte[CHUNK_SIZE];
        var loadingDialog = new SimpleInformationDialog(window, "업로드 중", "로드 중");

        new Thread(() -> {
            try (InputStream inputStream = new FileInputStream(file)) {
                int bytesRead;
                int partNumber = 0;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    loadingDialog.setContent("업로드 중(" + partNumber + '/' + file.length() / CHUNK_SIZE + ")");

                    byte[] chunk = new byte[bytesRead];
                    System.arraycopy(buffer, 0, chunk, 0, bytesRead);
                    String response = uploadChunk(chunk, file.getName(), partNumber);
                    responses.add(response.replace("\"", ""));  // Remove quotes from response
                    partNumber++;
                }
                loadingDialog.dispose();
            } catch (IOException e) {
                e.printStackTrace(System.out);
                var failDialog = new SimpleInformationDialog(window, "업로드 실패", "오류가 발생했습니다.");
                failDialog.setVisible(true);
                loadingDialog.dispose();
            }
        }).start();

        loadingDialog.setVisible(true);
        return responses;
    }

    public void downloadFile(String response, OutputStream out) throws IOException {
        String fileURL = "https://klas.kw.ac.kr/common/file/DownloadFile/" + response + "/1";

        try (InputStream in = new URL(fileURL).openStream()) {
            byte[] buffer = new byte[CHUNK_SIZE];
            int bytesRead;

            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
    }

    public void saveFileDatabase(Map<String, List<String>> data) {
        try (Writer writer = new FileWriter(DATA_FILE)) {
            Gson gson = new Gson();
            gson.toJson(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<String, List<String>> getFileDatabase() {
        try (Reader reader = new FileReader(DATA_FILE)) {
            Gson gson = new Gson();
            java.lang.reflect.Type type = new TypeToken<Map<String, List<String>>>() {}.getType();

            Map<String, List<String>> fileChunks = gson.fromJson(reader, type);
            if (fileChunks != null) {
                return fileChunks;
            }

        } catch (IOException e) {
            e.printStackTrace(System.out);
        }

        return new HashMap<>();
    }
}
