package org.msv.sm.response;

import org.msv.sm.RemoteFileDescription;


/**
 * Ответ сервера, сигнализирующий о переименовании файла или каталога
 * расположенных в текущей директории.
 */
public class FileRenamed extends AbstractResponse {

    private final String oldName;
    private final RemoteFileDescription fileDescription;


    public FileRenamed(String token, String oldName, RemoteFileDescription fileDescription) {
        super(token);
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
}
