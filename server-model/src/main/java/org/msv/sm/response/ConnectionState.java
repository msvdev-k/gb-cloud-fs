package org.msv.sm.response;


/**
 * Ответ сервера о состоянии подключения (аутентификации).
 *
 * true - клиент аутентифицирован
 * false - клиент не аутентифицирован
 */
public class ConnectionState extends AbstractResponse {

    private final boolean connection;


    public ConnectionState(String token, boolean connection) {
        super(token);
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
