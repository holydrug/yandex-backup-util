package com.amogus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.amogus.YandexBackupZipper.ARCHIVE_NAME;
import static java.lang.String.format;
import static java.nio.file.Files.createDirectories;
import static java.nio.file.Files.createFile;
import static org.junit.jupiter.api.Assertions.assertTrue;

class YandexBackupZipperTest {
    private static final String TEST_FILE_NAME = "testFile.txt";

    private YandexBackupZipper underTest;

    @BeforeEach
    void setUp() {
        underTest = new YandexBackupZipper();
    }

    @Test
    void shouldFindZipFile(@TempDir Path tempDir) throws Exception {
        String archiveName = format(ARCHIVE_NAME, LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE));
        Path sourceFolderPath = tempDir.resolve("folder");
        createDirectories(sourceFolderPath);
        createFile(sourceFolderPath.resolve(TEST_FILE_NAME));
        var zipPath = tempDir.resolve(archiveName);

        underTest.zipFolder(sourceFolderPath, zipPath);

        assertTrue(Files.exists(zipPath), "Zip-файл не был создан.");
    }
}