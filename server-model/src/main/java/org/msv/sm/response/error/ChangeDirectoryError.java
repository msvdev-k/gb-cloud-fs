package org.msv.sm.response.error;


import org.msv.sm.request.AbstractRequest;

/**
 * Ответ сервера при возникновении ошибки изменения текущего каталога.
 */
public class ChangeDirectoryError extends Error {
    public ChangeDirectoryError(AbstractRequest request, String errorMessage) {
        super(request, errorMessage);
    }
}
