package org.msv.sm.response;

import org.msv.sm.RemoteFileDescription;
import org.msv.sm.request.AbstractRequest;

import java.util.List;


/**
 * Ответ от сервера, содержащий список файлов и директорий текущего каталога.
 */
public class ListOfFiles extends AbstractResponse {

    private final List<RemoteFileDescription> files;


    public ListOfFiles(AbstractRequest request, List<RemoteFileDescription> files) {
        super(request);
        this.files = files;
    }

    public List<RemoteFileDescription> getFiles() {
        return files;
    }
}
