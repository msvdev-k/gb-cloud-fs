package org.msv.fm.fs;

import java.util.List;


/**
 * Интерфейс для получения сообщений от некоторой абстрактной файловой системы
 */
public interface FileSystemTerminalInput {


    /**
     * Метод вызывается при изменении или запросе состояния подключения.
     * Обязательный ответ на команды: connect(...), connectionState(), closeConnection().
     *
     * @param state true - терминал подключен, false - подключение отсутствует
     */
    void connectionState(boolean state);


    /**
     * Метод вызываемый при подключении или отключении терминальной сессии.
     * Обязательный ответ на команды: startSession(...), stopSession(...).
     *
     * @param token токен текущей сессии
     * @param state true - сессия активна, false - сессия удалена
     */
    void sessionState(FileSystemTerminalToken token, boolean state);


    /**
     * Метод вызываемый при изменении или запросе текущей директории.
     * Обязательный ответ на команды: cd(...), wd(...).
     *
     * @param path путь
     */
    void workingDirectory(String path);


    /**
     * Метод вызываемый при запросе списка файлов текущей директории.
     * Обязательный ответ на команду ls(...).
     *
     * @param fileInfoList список файлов
     */
    void listOfFiles(List<FileInfo> fileInfoList);


    /**
     * Метод вызываемый при добавлении файла в файловую систему терминала.
     *
     * @param path путь к добавленному файлу (в файловой системе терминала)
     */
    void fileAdded(String path);


    /**
     * Метод вызываемый при копировании файла из локальной файловой системы в файловую систему терминала.
     *
     * @param sourcePath      полный путь к файлу локальной файловой системы (источник)
     * @param destinationPath путь к файлу в файловой системе терминала (приёмник)
     */
    void putFile(String sourcePath, String destinationPath);


    /**
     * Сообщение об ошибке
     *
     * @param errorMessage описание ошибки
     */
    void error(String errorMessage);


    /**
     * Обычное сообщение
     *
     * @param infoMessage сообщение от файловой системы
     */
    void info(String infoMessage);

}
