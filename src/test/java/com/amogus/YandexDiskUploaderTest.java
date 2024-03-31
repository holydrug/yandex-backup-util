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
import static java.lang.String.format;
import static java.nio.file.Files.createDirectories;
import static java.nio.file.Files.createFile;
import static org.junit.jupiter.api.Assertions.*;

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
        createTestZip(tempDir);

        underTest.uploadBackupToYandexDisk(tempDir.toAbsolutePath().toString(), "/help", testToken);
    }

    private void createTestZip(Path tempDir) throws IOException {
        String archiveName = format(ARCHIVE_NAME, LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE));
        Path sourceFolderPath = tempDir.resolve(archiveName);
        createDirectories(sourceFolderPath);
        createFile(sourceFolderPath.resolve(TEST_FILE_NAME));
        var zipPath = tempDir.resolve(archiveName);
        zipper.zipFolder(sourceFolderPath, zipPath);

        assertTrue(Files.exists(zipPath), "Zip-файл не был создан.");
    }
}