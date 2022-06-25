package org.msv.sm.request;

import java.io.Serializable;


/**
 * Некоторый абстрактный запрос к серверу.
 * Все запросы должны унаследовать этот абстрактный класс.
 */
public abstract class AbstractRequest implements Serializable {

    private final int requestID;
    private final String token;

    public AbstractRequest(int requestID, String token) {
        this.requestID = requestID;
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public int getRequestID() {
        return requestID;
    }
}
