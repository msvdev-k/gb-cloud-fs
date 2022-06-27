package org.msv.sm.request;


/**
 * Запрос на отключение от удалённого файлового сервера.
 */
public class CloseConnection extends AbstractRequest {
    public CloseConnection(int requestID, String token) {
        super(requestID, token);
    }
}
