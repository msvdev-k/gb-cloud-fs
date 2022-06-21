package org.msv.sm.response.error;


/**
 * Ответ сервера при возникновении ошибки при получении списка файлов текущей директории.
 */
public class GetListOfFilesError extends Error {
    public GetListOfFilesError(String token, String errorMessage) {
        super(token, errorMessage);
    }
}
