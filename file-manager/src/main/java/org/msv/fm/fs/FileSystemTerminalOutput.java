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

