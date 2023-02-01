package org.msv.sm.response.error;


import org.msv.sm.request.AbstractRequest;

/**
 * Ответ сервера при возникновении ошибки создания новой директории.
 */
public class MakeDirectoryError extends Error {
    public MakeDirectoryError(AbstractRequest request, String errorMessage) {
        super(request, errorMessage);
    }
}
