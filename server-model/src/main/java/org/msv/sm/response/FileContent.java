package org.msv.sm.response;

import org.msv.sm.RemoteFileDescription;


/**
 * Ответ сервера, содержащий запрашиваемый файл.
 */
public class FileContent extends AbstractResponse {

    private final RemoteFileDescription fileDescription;
    private final byte[] data;


    public FileContent(String token, RemoteFileDescription fileDescription, byte[] data) {
        super(token);
        this.fileDescription = fileDescription;
        this.data = data;
    }


    public RemoteFileDescription getFileDescription() {
        return fileDescription;
    }


    public byte[] getData() {
        return data;
    }

}
