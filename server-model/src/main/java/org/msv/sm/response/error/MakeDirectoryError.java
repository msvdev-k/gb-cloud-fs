package org.msv.sm.response.error;


/**
 * Ответ сервера при возникновении ошибки создания новой директории.
 */
public class MakeDirectoryError extends Error {
    public MakeDirectoryError(String token, String errorMessage) {
        super(token, errorMessage);
    }
}
