package org.msv.sm.response.error;


import org.msv.sm.request.AbstractRequest;

/**
 * Ответ сервера при возникновении ошибки сохранения файла в текущей директории.
 */
public class PutFileError extends Error {
    public PutFileError(AbstractRequest request, String errorMessage) {
        super(request, errorMessage);
    }
}
