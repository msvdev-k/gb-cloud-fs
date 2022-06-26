package org.msv.fm.fs;

import java.util.List;


/**
 * Интерфейс для отправки сообщений некоторой абстрактной файловой системе
 */
public interface FileSystemTerminalOutput {

    /**
     * Установить подключение к терминалу.
     * (Для терминалов которые поддерживают вход по логину и паролю)
     *
     * @param login    логин
     * @param password пароль
     * @param input    объект получающий сообщения от терминала
     */
    void connect(String login, String password, FileSystemTerminalInput input);


    /**
     * Запрос состояния подключения.
     * @param input объект получающий сообщения от терминала
     */
    //void connectionState(FileSystemTerminalInput input);


    /**
     * Закрыть подключение к терминалу.
     */
    void closeConnection();


    /**
     * Начать новую сессию для работы в терминале.
     *
     * @param input объект получающий сообщения от терминала
     * @param location локация файловой системы
     */
    void startSession(FileSystemTerminalInput input, FileSystemLocation location);


    /**
     * Остановить сессию, работающую в терминале.
     *
     * @param token токен сессии
     */
    void stopSession(FileSystemTerminalToken token);


    /**
     * Изменить текущую директорию.
     *
     * @param token токен сессии
     * @param path  путь до директории (".." - переход в родительскую директорию)
     */
    void cd(FileSystemTerminalToken token, String path);


    /**
     * Запросить текущую рабочую директорию.
     *
     * @param token токен сессии
     */
    void wd(FileSystemTerminalToken token);


    /**
     * Получить список файлов текущей директории.
     *
     * @param token токен сессии
     */
    void ls(FileSystemTerminalToken token);


    /**
     * Скопировать файл из локальной файловой системы в файловую систему терминала.
     *
     * @param token           токен сессии
     * @param sourcePath      полный путь к файлу локальной файловой системы (источник)
     * @param destinationPath путь к файлу в файловой системе терминала (приёмник)
     */
    void put(FileSystemTerminalToken token, String sourcePath, String destinationPath);


    /**
     * Скопировать файл из файловой системы терминала в файловую систему другого терминала.
     * Копирование осуществляется через временный файл на локальной файловой системе.
     *
     * @param token                    токен сессии
     * @param sourcePath               путь к файлу в файловой системе терминала источника
     * @param destinationTerminalInput получатель сообщений терминал приёмника
     * @param destinationPath          путь к файлу в файловой системе терминала приёмника
     */
    void copy(FileSystemTerminalToken token, String sourcePath, FileSystemTerminalInput destinationTerminalInput, String destinationPath);


    /**
     * Создать новый каталог в текущей директории.
     * @param token токен сессии
     * @param directoryName название новой директории
     */
    void makeDirectory(FileSystemTerminalToken token, String directoryName);


    /**
     * Удалить файл или каталог в текущей директории.
     * @param token токен сессии
     * @param fileName название удаляемого файла или директории
     */
    void remove(FileSystemTerminalToken token, String fileName);


    /**
     * Переименовать файл или каталог в текущей директории.
     * @param token токен сессии
     * @param fileName название переименовываемого файла или каталога
     * @param newFileName новое название файла или каталога
     */
    void rename(FileSystemTerminalToken token, String fileName, String newFileName);


    /**
     * Список доступных корневых директорий для данной файловой системы
     *
     * @return список корневых директорий
     */
    List<String> roots();

}

