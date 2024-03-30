package com.amogus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.file.Files.createDirectories;
import static java.nio.file.Files.createFile;
import static org.junit.jupiter.api.Assertions.assertTrue;

class YandexBackupTest {
    private static final String TEST_FOLDER_NAME = "testFolder";
    private static final String TEST_FILE_NAME = "testFile.txt";
    private static final String TEST_ARCHIVE_NAME = "test.zip";

    private YandexBackup underTest;

    @BeforeEach
    void setUp() {
        underTest = new YandexBackup();
    }

    @Test
    void shouldFindZipFile(@TempDir Path tempDir) throws Exception {
        Path sourceFolderPath = tempDir.resolve(TEST_FOLDER_NAME);
        createDirectories(sourceFolderPath);
        createFile(sourceFolderPath.resolve(TEST_FILE_NAME));
        var zipPath = tempDir.resolve(TEST_ARCHIVE_NAME);

        underTest.zipFolder(sourceFolderPath, zipPath);

        assertTrue(Files.exists(zipPath), "Zip-файл не был создан.");
    }
}