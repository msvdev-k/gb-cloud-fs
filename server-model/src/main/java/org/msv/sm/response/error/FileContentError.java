package org.msv.sm.response.error;


import org.msv.sm.request.AbstractRequest;

/**
 * Ответ сервера при возникновении ошибки чтения содержимого файла.
 */
public class FileContentError extends Error {
    public FileContentError(AbstractRequest request, String errorMessage) {
        super(request, errorMessage);
    }
}
