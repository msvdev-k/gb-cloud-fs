package org.msv.sm;

import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * Описание файла, хранящегося на сервере
 */
public class RemoteFileDescription implements Serializable {

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


    public RemoteFileDescription(String filename, FileType type, long size, LocalDateTime lastModified) {
        this.name = filename;
        this.type = type;
        this.size = size;
        this.lastModified = lastModified;
    }

    /**
     * true - директория, false - не директория.
     */
    public boolean isDirectory() {
        return type == FileType.DIRECTORY;
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
