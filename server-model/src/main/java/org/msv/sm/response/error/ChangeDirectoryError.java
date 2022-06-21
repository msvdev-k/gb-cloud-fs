package org.msv.sm.response.error;


/**
 * Ответ сервера при возникновении ошибки изменения текущего каталога.
 */
public class ChangeDirectoryError extends Error {
    public ChangeDirectoryError(String token, String errorMessage) {
        super(token, errorMessage);
    }
}
