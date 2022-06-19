package org.msv.sm.request;


/**
 * Запрос на отключение от удалённого файлового сервера.
 */
public class CloseConnection extends AbstractRequest {
    public CloseConnection(String token) {
        super(token);
    }
}
