package org.msv.fm.fs.jvm;

import org.msv.fm.fs.FileInfo;
import org.msv.fm.fs.FileSystemTerminalInput;
import org.msv.fm.fs.FileSystemTerminalOutput;
import org.msv.fm.fs.FileSystemTerminalToken;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Терминал файловой системы доступной JVM
 */
public class JVMFileSystemTerminalOutput implements FileSystemTerminalOutput {

    /**
     * Список сессий. Ключ - токен, значение - экземпляр класса сессии.
     */
    private final Map<FileSystemTerminalToken, Session> sessions = new HashMap<>();


    /**
     * Начать новую сессию в терминале
     *
     * @param input объект получающий сообщения от терминала
     * @return токен новой сессии
     */
    @Override
    public FileSystemTerminalToken startSession(FileSystemTerminalInput input) {
        FileSystemTerminalToken token = new FileSystemTerminalToken();

        Session session = new Session();
        session.output = input;
        session.currentDir = Paths.get(System.getProperty("user.home")).toAbsolutePath();
        session.root = session.currentDir.getRoot();

        sessions.put(token, session);

        return token;
    }


    /**
     * Остановить сессию
     *
     * @param token токен сессии
     */
    @Override
    public void stopSession(FileSystemTerminalToken token) {
        sessions.remove(token);
    }


    /**
     * Изменить текущую директорию
     *
     * @param token токен сессии
     * @param path путь до директории
     */
    @Override
    public void cd(FileSystemTerminalToken token, String path) {

        if (!sessions.containsKey(token)) {
            throw new RuntimeException("Запись о FileSystemTerminalToken отсутствует в файловом терминале.");
        }

        Session session = sessions.get(token);

        Path newDir = Path.of(path).normalize();

        Path dirRoot = newDir.getRoot();
        if (dirRoot == null) {
            newDir = session.currentDir.resolve(newDir).normalize();

        } else {
            List<Path> roots = new ArrayList<>();
            FileSystems.getDefault().getRootDirectories().forEach(roots::add);

            if (roots.stream().anyMatch(p -> p.equals(dirRoot))) {
                session.root = dirRoot;
                session.output.root(dirRoot);
            }
            else {
                session.output.error("Некорректный путь к директории");
                return;
            }
        }

        if (newDir.startsWith(session.root) &&
                Files.isDirectory(newDir) &&
                Files.exists(newDir)) {

            session.currentDir = newDir;
            session.output.path(newDir);
        }
        else {
            session.output.error("Некорректный путь к директории");
        }
    }


    /**
     * Получить список файлов по указанному пути
     *
     * @param token токен сессии
     * @param path  путь из которого собираются файлы
     */
    @Override
    public void ls(FileSystemTerminalToken token, String path) {

        if (!sessions.containsKey(token)) {
            throw new RuntimeException("Запись о FileSystemTerminalToken отсутствует в файловом терминале.");
        }

        Session session = sessions.get(token);

        Path dir = Path.of(path).normalize();

        if (dir.getRoot() == null) {
            dir = session.currentDir.resolve(dir).normalize();
        }

        if (dir.startsWith(session.root) &&
                Files.isDirectory(dir) &&
                Files.exists(dir)) {

            try {
                List<FileInfo> fileInfoList = Files.list(dir).map(JVMFileInfo::get).toList();
                session.output.fileList(fileInfoList, dir);

            } catch (IOException e) {
                session.output.error("Ошибка обновления списка файлов");
            }

        }
        else {
            session.output.error("Некорректный путь к директории");
        }
    }


    /**
     * Получить текущую корневую директорию
     *
     * @param token токен сессии
     */
    @Override
    public void root(FileSystemTerminalToken token) {

        if (!sessions.containsKey(token)) {
            throw new RuntimeException("Запись о FileSystemTerminalToken отсутствует в файловом терминале.");
        }

        Session session = sessions.get(token);
        session.output.root(Paths.get(session.root.toUri()));
    }


    /**
     * Список доступных корневых директорий для данной файловой системы
     *
     * @return список корневых директорий
     */
    @Override
    public List<Path> roots() {
        List<Path> roots = new ArrayList<>();
        FileSystems.getDefault().getRootDirectories().forEach(roots::add);
        return roots;
    }


    /**
     * Описание текущей сессии
     */
    private class Session {
        public FileSystemTerminalInput output;
        public Path root;
        public Path currentDir;
    }


}
