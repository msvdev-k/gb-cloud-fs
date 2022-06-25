package org.msv.sm.response;

import org.msv.sm.request.AbstractRequest;

import java.io.Serializable;


/**
 * Некоторый абстрактный ответ от сервера.
 * Все ответы унаследуют этот абстрактный класс.
 */
public abstract class AbstractResponse implements Serializable {

    private final int requestID;
    private final String token;

    public AbstractResponse(AbstractRequest request) {
        this.requestID = request.getRequestID();
        this.token = request.getToken();
    }

    public int getRequestID() {
        return requestID;
    }

    public String getToken() {
        return token;
    }
}
