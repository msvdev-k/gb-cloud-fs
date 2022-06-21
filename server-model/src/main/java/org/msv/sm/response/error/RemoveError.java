package org.msv.sm.response.error;


/**
 * Ответ сервера при возникновении ошибки удаления файлового объекта.
 */
public class RemoveError extends Error {
    public RemoveError(String token, String errorMessage) {
        super(token, errorMessage);
    }
}
