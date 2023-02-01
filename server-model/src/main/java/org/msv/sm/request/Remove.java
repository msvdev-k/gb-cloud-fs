package org.msv.sm.request;


/**
 * Запрос на удаление файла или директории.
 * Команда выполняется для сессии, определённой своим токеном.
 * Файл или директория удаляются из текущего каталога.
 * Удаление производиться только пустой директории не содержащей других файловых объектов!
 *
 * fileName - название удаляемого файла или директории расположенных в текущем каталоге.
 */
public class Remove extends AbstractRequest {

    private final String fileName;


    public Remove(int requestID, String token, String fileName) {
        super(requestID, token);
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

}
