package org.msv.sfs.authentication;

import java.sql.*;
import java.util.Locale;


/**
 * Класс, реализующий аутентификацию клиента через БД SQLite
 */
public class SQLiteAuthService implements AuthenticationService {


    // Соединение с БД
    private Connection connection;


    /**
     * Конфигурация БД
     */
    private void configDB() throws SQLException {

        String sql = "CREATE TABLE users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "login TEXT," +
                "password TEXT);" +

                "INSERT INTO users(login, password) VALUES" +
                "('user1', 'pass1')," +
                "('user2', 'pass2')," +
                "('user3', 'pass3')," +
                "('user4', 'pass4');";


        Statement statement = connection.createStatement();
        statement.executeUpdate(sql);
        statement.close();
    }


    /**
     * Запуск сервиса
     *
     * @return true - сервис запущен успешно, false - ошибка запуска сервиса
     */
    @Override
    public boolean start() {

        try {
            connection = DriverManager.getConnection("jdbc:sqlite::memory:");
            configDB();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }


    /**
     * Остановка сервиса
     */
    @Override
    public void stop() {
        try {
            if (connection != null) {
                connection.close();
                connection = null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * Получить идентификатор по логину и паролю
     *
     * @param login    логин пользователя
     * @param password пароль пользователя
     * @return идентификатор либо null, если пары логин/пароль не существует
     */
    @Override
    public String getID(String login, String password) {

        int id = -1;
        String sql = "SELECT id FROM users WHERE login = ? AND password = ?;";

        try {
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setString(1, login);
            statement.setString(2, password);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                id = resultSet.getInt(1);
            }

            resultSet.close();
            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        return (id > 0) ? Integer.toHexString(id).toUpperCase(Locale.ROOT) : null;
    }
}
