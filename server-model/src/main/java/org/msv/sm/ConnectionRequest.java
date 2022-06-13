package org.msv.sm;

/**
 * Ответ сервера о состоянии подключения
 */
public class ConnectionRequest extends ServerMessage {

    private final boolean connect;

    public ConnectionRequest(String token, boolean connect) {
        super(token);
        this.connect = connect;
    }

    public boolean isConnect() {
        return connect;
    }
}
