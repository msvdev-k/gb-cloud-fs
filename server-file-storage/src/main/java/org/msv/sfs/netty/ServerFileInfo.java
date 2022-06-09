package org.msv.sfs.netty;

import org.msv.sm.RemoteFileDescription;
import org.msv.sm.RemoteFileDescription.FileType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneOffset;


/**
 * Фабрика классов, описывающих основную информацию о файле расположенном в удалённой файловой системе
 */
public class ServerFileInfo {

    public static RemoteFileDescription get(Path path) {

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

        return new RemoteFileDescription(name, type, size, lastModified);

    }
}
