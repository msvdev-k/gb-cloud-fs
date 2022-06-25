package org.msv.sfs.netty;

import org.msv.sfs.authentication.AuthenticationService;
import org.msv.sfs.authentication.SQLiteAuthService;

import java.nio.file.Path;


/**
 * Класс содержащий все сведения о конфигурации сервера.
 */
public class ServerConfiguration {

    // Сервис аутентификации
    private final AuthenticationService authenticationService;

    // Порт сервера
    private final int port;

    // Корневая директория с данными
    private final Path root;


    /**
     * Основной конструктор, устанавливающий все параметры конфигурации
     * сервера по умолчанию.
     */
    public ServerConfiguration() {

        authenticationService = new SQLiteAuthService();

        port = 8189;

        //root = Path.of(System.getProperty("user.home"));
        root = Path.of("B:/");

    }


    public AuthenticationService getAuthenticationService() {
        return authenticationService;
    }


    public int getPort() {
        return port;
    }


    public Path getRoot() {
        return Path.of(root.toUri());
    }

}
