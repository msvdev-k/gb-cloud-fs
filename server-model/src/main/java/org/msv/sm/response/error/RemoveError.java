package org.msv.sm.response.error;


import org.msv.sm.request.AbstractRequest;

/**
 * Ответ сервера при возникновении ошибки удаления файлового объекта.
 */
public class RemoveError extends Error {
    public RemoveError(AbstractRequest request, String errorMessage) {
        super(request, errorMessage);
    }
}
