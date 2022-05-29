package org.msv.fm;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneOffset;


/**
 * Класс, описывающий основную информацию о файле.
 */
public class FileInfo {

    public enum FileType {
        FILE("F"), DIRECTORY("D");

        private final String name;

        FileType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }


    private final String filename;
    private final FileType type;
    private final long size;
    private final LocalDateTime lastModified;


    public FileInfo(String filename, FileType type, long size, LocalDateTime lastModified) {
        this.filename = filename;
        this.type = type;
        this.size = size;
        this.lastModified = lastModified;
    }


    public FileInfo(Path path) {
        try {

            this.filename = path.getFileName().toString();
            this.type = Files.isDirectory(path) ? FileType.DIRECTORY : FileType.FILE;

            if (this.type == FileType.FILE) {
                this.size = Files.size(path);
            } else {
                this.size = -1L;
            }

            this.lastModified = LocalDateTime.ofInstant(
                    Files.getLastModifiedTime(path).toInstant(),
                    ZoneOffset.ofHours(0)
            );

        } catch (IOException e) {
            throw new RuntimeException("Unable to create file info from path");
        }
    }


    public String getFilename() {
        return filename;
    }

    public FileType getType() {
        return type;
    }

    public long getSize() {
        return size;
    }

    public LocalDateTime getLastModified() {
        return lastModified;
    }
}
