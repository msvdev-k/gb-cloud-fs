package org.msv.sm.response;


import org.msv.sm.request.AbstractRequest;

/**
 * Ответ сервера об открытии (закрытии) сессии.
 * В рамках одного соединения можно открыть несколько сессий.
 * Каждая сессия идентифицируется своим токеном.
 *
 * true - сессия открыта.
 * false - сессия закрыта.
 */
public class SessionSate extends AbstractResponse {

    private final boolean open;


    public SessionSate(AbstractRequest request, boolean open) {
        super(request);
        this.open = open;
    }

    /**
     * true - сессия открыта.
     * false - сессия закрыта.
     */
    public boolean isOpen() {
        return open;
    }
}
