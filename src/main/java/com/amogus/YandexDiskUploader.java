package com.amogus;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.URI;
import java.net.http.*;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.LocalDateTime;

public class YandexDiskUploader {
    private static final String LOG_FILE = System.getProperty("java.io.tmpdir") + File.separator + "yandex_disk_uploader_log.txt";
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void uploadBackupToYandexDisk(String zipFilePath, String yandexFolderPath, String token) {
        File zipFile = new File(zipFilePath);
        if (!zipFile.exists() || !zipFile.isFile()) {
            System.err.println("File does not exist or is not a file: " + zipFilePath);
            return;
        }

        String folderPathOnYandex = createFolderOnYandexDisk(yandexFolderPath, token);
        if (folderPathOnYandex != null) {
            uploadFileToYandexDisk(zipFile, yandexFolderPath, token);
        } else {
            System.err.println("Failed to create folder on Yandex Disk");
        }
    }

    private String createFolderOnYandexDisk(String folderPath, String token) {
        String uri = "https://cloud-api.yandex.net/v1/disk/resources/?path=" + folderPath;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .header("Authorization", "OAuth " + token)
                .PUT(HttpRequest.BodyPublishers.noBody())
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
            if (response.statusCode() == 201 || response.statusCode() == 409) {
                System.out.println("Folder created or already exists: " + folderPath);
                return folderPath;
            } else {
                System.err.println("Failed to create folder, status code: " + response.statusCode());
                return null;
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Error creating folder on Yandex Disk: " + e.getMessage());
            return null;
        }
    }

    private void uploadFileToYandexDisk(File file, String folderPath, String token) {
        String filename = file.getName();
        String uri = "https://cloud-api.yandex.net:443/v1/disk/resources/upload/?path=" + folderPath + "/" + filename + "&overwrite=true";
        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .header("Authorization", "OAuth " + token)
                .GET()
                .build();

        try {
            HttpResponse<String> getResponse = httpClient.send(getRequest, BodyHandlers.ofString());
            if (getResponse.statusCode() == 200) {
                JsonNode jsonResponse = objectMapper.readTree(getResponse.body());
                String uploadUrl = jsonResponse.get("href").asText();

                HttpRequest postRequest = HttpRequest.newBuilder()
                        .uri(URI.create(uploadUrl))
                        .header("Authorization", "OAuth " + token)
                        .PUT(HttpRequest.BodyPublishers.ofFile(file.toPath()))
                        .build();

                HttpResponse<String> postResponse = httpClient.send(postRequest, BodyHandlers.ofString());
                if (postResponse.statusCode() == 201) {
                    System.out.println("File uploaded successfully: " + filename);
                    logUploadEvent(filename);
                } else {
                    System.err.println("Failed to upload file, status code: " + postResponse.statusCode());
                }
            } else {
                System.err.println("Failed to get upload link, status code: " + getResponse.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Error uploading file to Yandex Disk: " + e.getMessage());
        }
    }

    private void logUploadEvent(String filename) {
        try (FileWriter fw = new FileWriter(LOG_FILE, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.println(LocalDateTime.now() + ": " + filename);
        } catch (IOException e) {
            System.err.println("Error writing to log file: " + e.getMessage());
        }
    }
}
