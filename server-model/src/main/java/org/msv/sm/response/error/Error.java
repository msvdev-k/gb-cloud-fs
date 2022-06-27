package org.msv.sm.response.error;

import org.msv.sm.request.AbstractRequest;
import org.msv.sm.response.AbstractResponse;


/**
 * Некоторый абстрактный ответ от сервера при возникновении ошибки.
 * Все ошибки сервера унаследуют этот абстрактный класс.
 */
public abstract class Error extends AbstractResponse {

    private final String errorMessage;


    public Error(AbstractRequest request, String errorMessage) {
        super(request);
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

}
