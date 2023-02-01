package org.msv.sm;

/**
 * Команда на закрытия терминального соединения
 */
public class CloseTerminalMessage extends ServerMessage {
    public CloseTerminalMessage(String token) {
        super(token);
    }
}
