package org.msv.sm.response;


/**
 * Ответ сервера, содержащий полный путь к текущей директории.
 * Путь указывается относительно корневого каталога пользователя.
 * Например: ~\Dir1\Dir12 или ~
 *
 * ~ - обозначение корневого каталога.
 */
public class CurrentDirectory extends AbstractResponse {

    private final String path;


    public CurrentDirectory(String token, String path) {
        super(token);
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
