package org.msv.sm.request;


/**
 * Запрос пути к текущей директории.
 * Команда выполняется для сессии, определённой своим токеном.
 */
public class WorkingDirectory extends AbstractRequest {
    public WorkingDirectory(int requestID, String token) {
        super(requestID, token);
    }
}
