package org.msv.sm.request;


/**
 * Запрос на открытие новой сессии.
 * В рамках одного соединения можно открыть несколько сессий.
 * Каждая сессия идентифицируется своим токеном.
 */
public class OpenSession extends AbstractRequest {
    public OpenSession(int requestID, String token) {
        super(requestID, token);
    }
}
