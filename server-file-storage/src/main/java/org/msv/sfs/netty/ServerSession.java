package org.msv.sfs.netty;

import org.msv.sm.request.AbstractRequest;

import java.nio.file.FileSystems;
import java.nio.file.Path;


/**
 * Описание текущей сессии
 */
class ServerSession {

    // Корневой каталог для текущей сессии
    private final Path root;

    // Текущий каталог
    private Path currentDirectory;

    // Текущее сообщение, которое необходимо обработать
    private AbstractRequest request;


    public ServerSession(Path root) {
        this.root = root;
        this.currentDirectory = root;
        this.request = null;
    }


    /**
     * Получить текущий каталог.
     * Путь указывается относительно корневого каталога пользователя.
     * Например: ~\Dir1\Dir12 или ~
     *
     * ~ - обозначение корневого каталога.
     */
    public String getCurrentDirectory() {

        String path = "~";

        if (root.getNameCount() < currentDirectory.getNameCount()) {
            path = "~" + FileSystems.getDefault().getSeparator() +
                    currentDirectory.subpath(root.getNameCount(), currentDirectory.getNameCount());
        }

        return path;
    }


    /**
     * Установить обрабатываемое сообщение.
     */
    public void setRequest(AbstractRequest request) {
        this.request = request;
    }


    /**
     * Получить обрабатываемое сообщение.
     */
    public AbstractRequest getRequest() {
        return request;
    }



}
