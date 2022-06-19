package org.msv.sm.request;


/**
 * Запрос на изменение текущей директории.
 * Команда выполняется для сессии, определённой своим токеном.
 *
 * token - токен сессии.
 * directoryName - название директории. Новая директория устанавливается относительно текущей директории.
 *                 Если передано значение "..", то производится переход на директорию выше.
 */
public class ChangeDirectory extends AbstractRequest {

    private final String directoryName;


    public ChangeDirectory(String token, String directoryName) {
        super(token);
        this.directoryName = directoryName;
    }


    public String getDirectoryName() {
        return directoryName;
    }

}
