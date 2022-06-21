package org.msv.sm.response.error;


/**
 * Ответ сервера при возникновении ошибки сохранения файла в текущей директории.
 */
public class PutFileError extends Error {
    public PutFileError(String token, String errorMessage) {
        super(token, errorMessage);
    }
}
