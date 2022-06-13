package org.msv.fm.fs;

import java.nio.file.Path;
import java.util.List;


/**
 * Интерфейс для отправки сообщений некоторой абстрактной файловой системе
 */
public interface FileSystemTerminalOutput {


    /**
     * Начать новую сессию в терминале
     *
     * @param input объект получающий сообщения от терминала
     * @return токен новой сессии
     */
    FileSystemTerminalToken startSession(FileSystemTerminalInput input);


    /**
     * Остановить сессию
     *
     * @param token токен сессии
     */
    void stopSession(FileSystemTerminalToken token);


    /**
     * Установить подключение к терминалу.
     * (Для терминалов которые поддерживают вход по логину и паролю)
     * @param login логин
     * @param password пароль
     */
    void connect(String login, String password);


    /**
     * Изменить текущую директорию
     *
     * @param token токен сессии
     * @param path  путь до директории
     */
    void cd(FileSystemTerminalToken token, String path);


    /**
     * Получить список файлов по указанному пути
     *
     * @param token токен сессии
     * @param path  путь из которого собираются файлы
     */
    void ls(FileSystemTerminalToken token, String path);


    /**
     * Скопировать файл из локальной файловой системы в файловую систему терминала
     * @param token токен сессии
     * @param sourcePath полный путь к файлу локальной файловой системы (источник)
     * @param destinationPath путь к файлу в файловой системе терминала (приёмник)
     */
    void put(FileSystemTerminalToken token, String sourcePath, String destinationPath);


    /**
     * Скопировать файл из файловой системы терминала на локальную файловую систему
     * @param token токен сессии
     * @param sourcePath путь к файлу в файловой системе терминала (источник)
     * @param destinationPath полный путь к файлу локальной файловой системы (приёмник)
     * @param destinationTerminal терминал в который производится копирование файла
     */
    void get(FileSystemTerminalToken token, String sourcePath, String destinationPath, FileSystemTerminalInput destinationTerminal);


    /**
     * Получить текущую корневую директорию
     *
     * @param token токен сессии
     */
    void root(FileSystemTerminalToken token);


    /**
     * Список доступных корневых директорий для данной файловой системы
     *
     * @return список корневых директорий
     */
    List<Path> roots();

}

