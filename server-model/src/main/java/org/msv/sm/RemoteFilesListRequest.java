package org.msv.sm;

import java.util.List;


/**
 * Ответ от сервера, содержащий список файлов текущего каталога
 */
public class RemoteFilesListRequest extends PathServerMessage {

    private final List<RemoteFileDescription> files;

    public RemoteFilesListRequest(String token, String path, List<RemoteFileDescription> files) {
        super(token, path);
        this.files = files;
    }

    public List<RemoteFileDescription> getFiles() {
        return files;
    }
}
