package org.msv.sm.response;

import org.msv.sm.RemoteFileDescription;

import java.util.List;


/**
 * Ответ от сервера, содержащий список файлов и директорий текущего каталога.
 */
public class ListOfFiles extends AbstractResponse {

    private final List<RemoteFileDescription> files;


    public ListOfFiles(String token, List<RemoteFileDescription> files) {
        super(token);
        this.files = files;
    }


    public List<RemoteFileDescription> getFiles() {
        return files;
    }
}
