package org.msv.sfs.authentication;


/**
 * Интерфейс, описывающий сервис аутентификации на стороне сервера
 */
public interface AuthenticationService {

    /**
     * Запуск сервиса
     *
     * @return true - сервис запущен успешно, false - ошибка запуска сервиса
     */
    boolean start();


    /**
     * Остановка сервиса
     */
    void stop();


    /**
     * Получить идентификатор по логину и паролю
     *
     * @param login    логин пользователя
     * @param password пароль пользователя
     * @return идентификатор либо null, если пары логин/пароль не существует
     */
    String getID(String login, String password);

}
