package org.msv.sm.request;


/**
 * Запрос на подключение к удалённому файловому серверу.
 */
public class OpenConnection extends AbstractRequest {

    private final String login;
    private final String password;


    public OpenConnection(String token, String login, String password) {
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
