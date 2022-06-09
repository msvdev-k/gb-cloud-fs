package org.msv.fm.fs;

import java.nio.file.Path;
import java.util.List;


/**
 * Интерфейс для получения сообщений от некоторой абстрактной файловой системы
 */
public interface FileSystemTerminalInput {


    /**
     * Абсолютный путь к текущему каталогу
     *
     * @param path путь
     */
    void path(Path path);


    /**
     * Текущая корневая директория (для файловых систем с несколькими корневыми директориями)
     * @param root корневая директория
     */
    void root(Path root);


    /**
     * Список файлов
     *
     * @param fileInfoList список файлов
     * @param path         абсолютный путь к каталогу из которого собирается список
     */
    void fileList(List<FileInfo> fileInfoList, Path path);


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
