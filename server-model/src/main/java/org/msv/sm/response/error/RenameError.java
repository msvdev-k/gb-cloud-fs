package org.msv.sm.response.error;


import org.msv.sm.request.AbstractRequest;

/**
 * Ответ сервера при возникновении ошибки переименования файлового объекта.
 */
public class RenameError extends Error {
    public RenameError(AbstractRequest request, String errorMessage) {
        super(request, errorMessage);
    }
}
