package org.msv.sm;

import java.io.Serializable;


/**
 * Некоторый абстрактный запрос к серверу.
 * Все запросы должны унаследовать этот абстрактный класс.
 */
public abstract class ServerMessage implements Serializable {

    private final String token;

    public ServerMessage(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
