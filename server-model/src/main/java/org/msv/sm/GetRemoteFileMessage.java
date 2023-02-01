package org.msv.sm;

/**
 * Команда на получение удалённого файла
 */
public class GetRemoteFileMessage extends PathServerMessage {

    public GetRemoteFileMessage(String token, String path) {
        super(token, path);
    }

}
