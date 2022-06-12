package org.msv.sm;

/**
 * Некоторый абстрактный запрос содержащий путь к файлу
 */
public abstract class PathServerMessage extends ServerMessage {

    private final String path;

    public PathServerMessage(String token, String path) {
        super(token);
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
