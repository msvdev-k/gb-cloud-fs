package org.msv.fm.fs.jvm;
import org.msv.fm.fs.FileInfo;
import org.msv.fm.fs.FileInfo.FileType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * Фабрика классов, описывающих основную информацию о файле в файловой системе JVM
 */
public class JVMFileInfo {

    public static FileInfo get(Path path) {

        String name;
        FileType type;
        long size;
        LocalDateTime lastModified;

        try {

            name = path.getFileName().toString();
            type = Files.isDirectory(path) ? FileType.DIRECTORY : FileType.FILE;

            if (type == FileType.FILE) {
                size = Files.size(path);
            } else {
                size = -1L;
            }

            lastModified = LocalDateTime.ofInstant(
                    Files.getLastModifiedTime(path).toInstant(),
                    ZoneOffset.ofHours(0)
            );

        } catch (IOException e) {
            throw new RuntimeException("Unable to create file info from path");
        }

        return new FileInfo(name, type, size, lastModified);

    }
}
