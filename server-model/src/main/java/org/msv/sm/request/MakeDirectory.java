package org.msv.sm.request;


/**
 * Запрос на добавление новой директории.
 * Команда выполняется для сессии, определённой своим токеном.
 * Новая директория создаётся в текущей директории.
 */
public class MakeDirectory extends AbstractRequest {

    private final String newDirectoryName;


    public MakeDirectory(String token, String newDirectoryName) {
        super(token);
        this.newDirectoryName = newDirectoryName;
    }


    public String getNewDirectoryName() {
        return newDirectoryName;
    }

}
