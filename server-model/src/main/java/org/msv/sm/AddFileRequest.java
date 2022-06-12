package org.msv.sm;

/**
 * Ответ от сервера, сигнализирующий о добавлении нового файла
 */
public class AddFileRequest extends PathServerMessage {

    public AddFileRequest(String token, String path) {
        super(token, path);
    }

}
