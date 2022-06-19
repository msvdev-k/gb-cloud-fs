package org.msv.sm.response;

import org.msv.sm.RemoteFileDescription;


/**
 * Ответ сервера, сигнализирующий об удалении файла или каталога
 * расположенных в текущей директории.
 */
public class FileRemoved extends AbstractResponse {

    private final RemoteFileDescription fileDescription;


    public FileRemoved(String token, RemoteFileDescription fileDescription) {
        super(token);
        this.fileDescription = fileDescription;
    }


    /**
     * Описание удалённого файла или каталога.
     */
    public RemoteFileDescription getFileDescription() {
        return fileDescription;
    }

}
