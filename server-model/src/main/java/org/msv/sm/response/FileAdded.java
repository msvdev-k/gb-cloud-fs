package org.msv.sm.response;

import org.msv.sm.RemoteFileDescription;


/**
 * Ответ сервера, сигнализирующий о добавлении нового файла или каталога в текущую директорию.
 */
public class FileAdded extends AbstractResponse {

    private final RemoteFileDescription fileDescription;


    public FileAdded(String token, RemoteFileDescription fileDescription) {
        super(token);
        this.fileDescription = fileDescription;
    }


    /**
     * Получит описание добавленного файла.
     */
    public RemoteFileDescription getFileDescription() {
        return fileDescription;
    }

}
