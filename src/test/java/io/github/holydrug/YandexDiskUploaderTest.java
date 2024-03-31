package io.github.holydrug;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class YandexDiskUploaderTest {
    private static final String TEST_FILE_NAME = "testFile.txt";
    private YandexBackupZipper zipper = new YandexBackupZipper();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    private YandexDiskUploader underTest;
    private String testToken;

    @BeforeEach
    void setUp() {
        testToken = System.getenv("YANDEX_DISK_TOKEN");
        assertNotNull(testToken, "Token не должен быть null");

        underTest = new YandexDiskUploader();

    }

    @Test
    void uploadBackupToYandexDisk(@TempDir Path tempDir) throws Exception {
        Path testZip = createTestZip(tempDir);

        underTest.uploadBackupToYandexDisk(testZip.toString(), "/test", testToken);

        assertTrue(checkFileExistsOnYandexDisk("/test", testToken));

    }

    private Path createTestZip(Path tempDir) throws IOException {
        String archiveName = String.format(YandexBackupZipper.ARCHIVE_NAME, LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE));
        Path sourceFolderPath = tempDir.resolve("sourceFolder");
        Files.createDirectories(sourceFolderPath);
        Files.createFile(sourceFolderPath.resolve(TEST_FILE_NAME));
        Path zipFilePath = tempDir.resolve(archiveName);

        Path zip = zipper.zipFolder(sourceFolderPath, zipFilePath);

        assertTrue(Files.exists(zipFilePath), "Zip-файл не был создан.");
        return zip;
    }

    private boolean checkFileExistsOnYandexDisk(String filePath, String token) {
        String uri = "https://cloud-api.yandex.net/v1/disk/resources/?path=" + URLEncoder.encode(filePath, StandardCharsets.UTF_8);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .header("Authorization", "OAuth " + token)
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200;
        } catch (IOException | InterruptedException e) {
            System.err.println("Error checking file on Yandex Disk: " + e.getMessage());
            return false;
        }
    }
}