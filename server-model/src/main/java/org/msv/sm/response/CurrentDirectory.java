package org.msv.sm.response;


import org.msv.sm.request.AbstractRequest;

/**
 * Ответ сервера, содержащий полный путь к текущей директории.
 * Путь указывается относительно корневого каталога пользователя.
 * Например: ~\Dir1\Dir12 или ~
 *
 * ~ - обозначение корневого каталога.
 */
public class CurrentDirectory extends AbstractResponse {

    private final String path;


    public CurrentDirectory(AbstractRequest request, String path) {
        super(request);
        this.path = path;
    }

    /**
     * Полный путь к текущей директории.
     * Например: ~\Dir1\Dir12 или ~
     * ~ - обозначение корневого каталога.
     */
    public String getPath() {
        return path;
    }
}
