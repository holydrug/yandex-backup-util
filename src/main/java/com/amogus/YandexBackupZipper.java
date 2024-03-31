package com.amogus;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static java.lang.String.format;

public class YandexBackupZipper {

    public static final String ARCHIVE_NAME = "archive_%s.zip";

    public void zipFolder(Path sourceFolderPath, Path zipPath) throws IOException {
        LocalDate currentDate = LocalDate.now();
        String isoDate = currentDate.format(DateTimeFormatter.BASIC_ISO_DATE);

        Path zipFileName = zipPath.resolve(format(ARCHIVE_NAME, isoDate));

        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipFileName))) {
            try (Stream<Path> paths = Files.walk(sourceFolderPath)) {
                paths.filter(path -> !Files.isDirectory(path)).forEach(path -> {
                    ZipEntry zipEntry = new ZipEntry(sourceFolderPath.relativize(path).toString());
                    try {
                        zos.putNextEntry(zipEntry);
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
