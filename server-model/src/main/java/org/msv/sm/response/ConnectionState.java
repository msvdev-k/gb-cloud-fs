package org.msv.sm.response;


import org.msv.sm.request.AbstractRequest;

/**
 * Ответ сервера о состоянии подключения (аутентификации).
 *
 * true - клиент аутентифицирован
 * false - клиент не аутентифицирован
 */
public class ConnectionState extends AbstractResponse {

    private final boolean connection;


    public ConnectionState(AbstractRequest request, boolean connection) {
        super(request);
        this.connection = connection;
    }

    /**
     * true - клиент аутентифицирован
     * false - клиент не аутентифицирован
     */
    public boolean isConnection() {
        return connection;
    }

}
