package com.amogus;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class YandexBackup {
    public void zipFolder(Path sourceFolderPath, Path zipPath) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipPath))) {
            try (Stream<Path> paths = Files.walk(sourceFolderPath)) {
                paths.filter(path -> !Files.isDirectory(path)).forEach(path -> {
                    ZipEntry zipEntry = new ZipEntry(sourceFolderPath.relativize(path).toString());
                    try {
                        zos.putNextEntry(zipEntry);
                        // Используем Files.copy с InputStream для копирования файла в ZipOutputStream
                        try (InputStream is = Files.newInputStream(path)) {
                            byte[] buffer = new byte[1024];
                            int length;
                            while ((length = is.read(buffer)) > 0) {
                                zos.write(buffer, 0, length);
                            }
                        }
                        zos.closeEntry();
                    } catch (IOException e) {
                        System.err.println(e);
                    }
                });
            }
        }
    }
}
