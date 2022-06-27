package org.msv.sm.response.error;


import org.msv.sm.request.AbstractRequest;

/**
 * Ответ сервера при возникновении ошибки при получении списка файлов текущей директории.
 */
public class GetListOfFilesError extends Error {
    public GetListOfFilesError(AbstractRequest request, String errorMessage) {
        super(request, errorMessage);
    }
}
