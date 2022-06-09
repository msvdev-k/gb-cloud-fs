package org.msv.sm;

/**
 * Ответ от сервера, сигнализирующий об изменении текущего каталога
 */
public class RemoteDirectoryRequest extends PathServerMessage {

    public RemoteDirectoryRequest(String token, String path) {
        super(token, path);
    }

}
