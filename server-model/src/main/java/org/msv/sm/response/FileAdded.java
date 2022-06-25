package org.msv.sm.response;

import org.msv.sm.RemoteFileDescription;
import org.msv.sm.request.AbstractRequest;


/**
 * Ответ сервера, сигнализирующий о добавлении нового файла или каталога в текущую директорию.
 */
public class FileAdded extends AbstractResponse {

    private final String directoryPath;
    private final RemoteFileDescription fileDescription;


    public FileAdded(AbstractRequest request, String directoryPath, RemoteFileDescription fileDescription) {
        super(request);
        this.directoryPath = directoryPath;
        this.fileDescription = fileDescription;
    }

    /**
     * Получит описание добавленного файла.
     */
    public RemoteFileDescription getFileDescription() {
        return fileDescription;
    }


    /**
     * Путь к директории в которой был изменён файл.
     */
    public String getDirectoryPath() {
        return directoryPath;
    }
}
