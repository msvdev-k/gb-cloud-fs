package org.msv.sm;

/**
 * Команда на изменение текущей директории
 */
public class ChangeDirectoryMessage extends PathServerMessage {

    public ChangeDirectoryMessage(String token, String path) {
        super(token, path);
    }
}
