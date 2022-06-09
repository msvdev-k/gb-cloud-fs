package org.msv.fm.net;

import org.msv.fm.fs.FileInfo;
import org.msv.fm.fs.FileInfo.FileType;
import org.msv.sm.RemoteFileDescription;

import java.time.LocalDateTime;


/**
 * Фабрика классов, описывающих основную информацию о файле расположенном в удалённой файловой системе
 */
public class NettyServerFileInfo {

    public static FileInfo get(RemoteFileDescription rfd) {

        String name = rfd.getName();

        FileType type = (rfd.getType() == RemoteFileDescription.FileType.DIRECTORY) ? FileType.DIRECTORY : FileType.FILE;

        long size = rfd.getSize();
        LocalDateTime lastModified = rfd.getLastModified();

        return new FileInfo(name, type, size, lastModified);
    }
}
