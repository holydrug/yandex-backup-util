package com.amogus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.amogus.YandexBackupZipper.ARCHIVE_NAME;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class YandexDiskUploaderTest {
    private static final String TEST_FILE_NAME = "testFile.txt";
    private YandexBackupZipper zipper = new YandexBackupZipper();;
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

        underTest.uploadBackupToYandexDisk(testZip.toString(), "/help", testToken);
    }

    private Path createTestZip(Path tempDir) throws IOException {
        String archiveName = String.format(ARCHIVE_NAME, LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE));
        Path sourceFolderPath = tempDir.resolve("sourceFolder"); // Создайте исходную папку
        Files.createDirectories(sourceFolderPath);
        Files.createFile(sourceFolderPath.resolve(TEST_FILE_NAME));
        Path zipFilePath = tempDir.resolve(archiveName); // Путь к файлу архива, уже с расширением

        Path zip = zipper.zipFolder(sourceFolderPath, zipFilePath);

        assertTrue(Files.exists(zipFilePath), "Zip-файл не был создан.");
        return zip;
    }
}