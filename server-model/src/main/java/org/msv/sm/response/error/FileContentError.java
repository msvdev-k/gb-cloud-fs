package org.msv.sm.response.error;


/**
 * Ответ сервера при возникновении ошибки чтения содержимого файла.
 */
public class FileContentError extends Error {
    public FileContentError(String token, String errorMessage) {
        super(token, errorMessage);
    }
}
