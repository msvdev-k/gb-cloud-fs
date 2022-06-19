package org.msv.sm.response;

import java.io.Serializable;


/**
 * Некоторый абстрактный ответ от сервера.
 * Все ответы унаследуют этот абстрактный класс.
 */
public abstract class AbstractResponse implements Serializable {

    private final String token;

    public AbstractResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
