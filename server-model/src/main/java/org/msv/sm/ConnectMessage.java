package org.msv.sm;


/**
 * Команда на подключение к удалённой директории
 */
public class ConnectMessage extends ServerMessage {

    private final String login;
    private final String password;

    public ConnectMessage(String token, String login, String password) {
        super(token);
        this.login = login;
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }
}
