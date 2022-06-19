package org.msv.sm.request;


/**
 * Запрос на закрытие открытой сессии.
 */
public class CloseSession extends AbstractRequest {
    public CloseSession(String token) {
        super(token);
    }
}
