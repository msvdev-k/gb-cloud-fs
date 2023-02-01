package org.msv.fm.fs;

import java.time.LocalDateTime;


/**
 * Класс, описывающий основную информацию о файле
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


    private final String name;
    private final FileType type;
    private final long size;
    private final LocalDateTime lastModified;


    public FileInfo(String filename, FileType type, long size, LocalDateTime lastModified) {
        this.name = filename;
        this.type = type;
        this.size = size;
        this.lastModified = lastModified;
    }


    public String getName() {
        return name;
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
