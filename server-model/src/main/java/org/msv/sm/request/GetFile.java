package org.msv.sm.request;


/**
 * Запрос на получение файла от удалённого файлового сервера.
 * Команда выполняется для сессии, определённой своим токеном.
 *
 * fileName - имя запрашиваемого файла расположенного в текущей директории.
 */
public class GetFile extends AbstractRequest {

    private final String fileName;


    public GetFile(int requestID, String token, String fileName) {
        super(requestID, token);
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

}
