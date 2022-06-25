package org.msv.sm.response;

import org.msv.sm.RemoteFileDescription;
import org.msv.sm.request.AbstractRequest;


/**
 * Ответ сервера, сигнализирующий об удалении файла или каталога
 * расположенных в текущей директории.
 */
public class FileRemoved extends AbstractResponse {

    private final RemoteFileDescription fileDescription;


    public FileRemoved(AbstractRequest request, RemoteFileDescription fileDescription) {
        super(request);
        this.fileDescription = fileDescription;
    }

    /**
     * Описание удалённого файла или каталога.
     */
    public RemoteFileDescription getFileDescription() {
        return fileDescription;
    }

}
