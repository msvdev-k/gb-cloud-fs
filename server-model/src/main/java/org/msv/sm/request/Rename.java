package org.msv.sm.request;


/**
 * Запрос на переименование файла или директории.
 * Команда выполняется для сессии, определённой своим токеном.
 *
 * name - название файла или директории.
 * newName - новое название файла или директории.
 */
public class Rename extends AbstractRequest {

    private final String name;
    private final String newName;


    public Rename(int requestID, String token, String name, String newName) {
        super(requestID, token);
        this.name = name;
        this.newName = newName;
    }

    public String getName() {
        return name;
    }


    public String getNewName() {
        return newName;
    }

}
