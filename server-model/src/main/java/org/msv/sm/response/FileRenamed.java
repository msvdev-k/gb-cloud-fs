package org.msv.sm.response;

import org.msv.sm.RemoteFileDescription;
import org.msv.sm.request.AbstractRequest;


/**
 * Ответ сервера, сигнализирующий о переименовании файла или каталога
 * расположенных в текущей директории.
 */
public class FileRenamed extends AbstractResponse {

    private final String directoryPath;
    private final String oldName;
    private final RemoteFileDescription fileDescription;


    public FileRenamed(AbstractRequest request, String directoryPath, String oldName, RemoteFileDescription fileDescription) {
        super(request);
        this.directoryPath = directoryPath;
        this.oldName = oldName;
        this.fileDescription = fileDescription;
    }

    /**
     * Старое название файла или каталога.
     */
    public String getOldName() {
        return oldName;
    }


    /**
     * Текущее описание файла или каталога.
     */
    public RemoteFileDescription getFileDescription() {
        return fileDescription;
    }


    /**
     * Путь к директории в которой был переименован файл.
     */
    public String getDirectoryPath() {
        return directoryPath;
    }
}
