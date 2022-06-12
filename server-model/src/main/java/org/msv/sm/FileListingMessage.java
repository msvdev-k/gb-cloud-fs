package org.msv.sm;

/**
 * Команда на получения списка файлов директории по указанному пути
 */
public class FileListingMessage extends PathServerMessage {

    public FileListingMessage(String token, String path) {
        super(token, path);
    }
}
