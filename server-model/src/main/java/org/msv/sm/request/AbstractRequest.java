package org.msv.sm.request;

import java.io.Serializable;


/**
 * Некоторый абстрактный запрос к серверу.
 * Все запросы должны унаследовать этот абстрактный класс.
 */
public abstract class AbstractRequest implements Serializable {

    private final String token;

    public AbstractRequest(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
