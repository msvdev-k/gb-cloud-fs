package org.msv.sm.response;

import org.msv.sm.RemoteFileDescription;
import org.msv.sm.request.AbstractRequest;


/**
 * Ответ сервера, содержащий запрашиваемый файл.
 */
public class FileContent extends AbstractResponse {

    private final RemoteFileDescription fileDescription;
    private final byte[] data;


    public FileContent(AbstractRequest request, RemoteFileDescription fileDescription, byte[] data) {
        super(request);
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
