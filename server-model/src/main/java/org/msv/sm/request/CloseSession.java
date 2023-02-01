package org.msv.sm.request;


/**
 * Запрос на закрытие открытой сессии.
 */
public class CloseSession extends AbstractRequest {
    public CloseSession(int requestID, String token) {
        super(requestID, token);
    }
}
