package org.msv.sm;

/**
 * Команда на открытие терминального соединения.
 * У одного пользователя может быть несколько терминальных соединений,
 * то есть несколько независимых окон с разными текущими директориями
 */
public class OpenTerminalMessage extends ServerMessage {
    public OpenTerminalMessage(String token) {
        super(token);
    }
}
