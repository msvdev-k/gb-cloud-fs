package org.msv.sm.response;

import org.msv.sm.RemoteFileDescription;
import org.msv.sm.request.AbstractRequest;


/**
 * Ответ сервера, сигнализирующий об удалении файла или каталога
 * расположенных в текущей директории.
 */
public class FileRemoved extends AbstractResponse {

    private final String directoryPath;
    private final RemoteFileDescription fileDescription;


    public FileRemoved(AbstractRequest request, String directoryPath, RemoteFileDescription fileDescription) {
        super(request);
        this.directoryPath = directoryPath;
        this.fileDescription = fileDescription;
    }

    /**
     * Описание удалённого файла или каталога.
     */
    public RemoteFileDescription getFileDescription() {
        return fileDescription;
    }


    /**
     * Путь к директории в которой был удалён файл.
     */
    public String getDirectoryPath() {
        return directoryPath;
    }
}
