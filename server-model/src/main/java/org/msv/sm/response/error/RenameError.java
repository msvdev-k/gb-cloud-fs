package org.msv.sm.response.error;


/**
 * Ответ сервера при возникновении ошибки переименования файлового объекта.
 */
public class RenameError extends Error {
    public RenameError(String token, String errorMessage) {
        super(token, errorMessage);
    }
}
