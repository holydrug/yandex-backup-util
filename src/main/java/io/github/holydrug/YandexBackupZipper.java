package io.github.holydrug;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class YandexBackupZipper {

    public static final String ARCHIVE_NAME = "archive_%s.zip";

    public Path zipFolder(Path sourceFolderPath, Path zipPath) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipPath))) {
            Files.walkFileTree(sourceFolderPath, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Path relativePath = sourceFolderPath.relativize(file);
                    ZipEntry zipEntry = new ZipEntry(relativePath.toString());
                    zos.putNextEntry(zipEntry);
                    Files.copy(file, zos);
                    zos.closeEntry();
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    Path relativeDirPath = sourceFolderPath.relativize(dir);
                    if (!relativeDirPath.toString().isEmpty()) {
                        ZipEntry zipEntry = new ZipEntry(relativeDirPath + "/");
                        zos.putNextEntry(zipEntry);
                        zos.closeEntry();
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        }
        return zipPath;
    }
}
