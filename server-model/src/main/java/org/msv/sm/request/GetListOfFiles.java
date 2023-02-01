package org.msv.sm.request;


/**
 * Запрос на получение списка файлов находящихся в текущей директории.
 * Команда выполняется для сессии, определённой своим токеном.
 */
public class GetListOfFiles extends AbstractRequest {
    public GetListOfFiles(int requestID, String token) {
        super(requestID, token);
    }
}
