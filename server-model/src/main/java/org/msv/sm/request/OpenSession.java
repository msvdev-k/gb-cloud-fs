package org.msv.sm.request;


/**
 * Запрос на открытие новой сессии.
 * В рамках одного соединения можно открыть несколько сессий.
 * Каждая сессия идентифицируется своим токеном.
 */
public class OpenSession extends AbstractRequest {
    public OpenSession(String token) {
        super(token);
    }
}
