package org.msv.fm.fs.jvm;

import org.msv.fm.fs.*;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;


/**
 * Терминал файловой системы доступной JVM
 */
public class JVMFileSystemTerminalOutput implements FileSystemTerminalOutput {

    /**
     * Список сессий. Ключ - токен, значение - экземпляр класса сессии.
     */
    private final Map<FileSystemTerminalToken, Session> sessions = new HashMap<>();


    @Override
    public void connect(String login, String password, FileSystemTerminalInput input) {
        input.connectionState(true);
    }


//    @Override
//    public void connectionState(FileSystemTerminalInput input) {
//        input.connectionState(true);
//    }


    @Override
    public void closeConnection() {
    }


    @Override
    public void startSession(FileSystemTerminalInput input, FileSystemLocation location) {
        FileSystemTerminalToken token = new FileSystemTerminalToken();

        Session session = new Session();
        session.output = input;
        session.currentDir = Path.of(location.getRoot());
        session.root = Path.of(location.getRoot());

        sessions.put(token, session);

        input.sessionState(token, true);
    }


    @Override
    public void stopSession(FileSystemTerminalToken token) {

        if (sessions.containsKey(token)) {
            sessions.get(token).output.sessionState(token, false);
        }

        sessions.remove(token);
    }


    @Override
    public void cd(FileSystemTerminalToken token, String path) {

        if (!sessions.containsKey(token)) {
            throw new RuntimeException("Запись о FileSystemTerminalToken отсутствует в файловом терминале.");
        }

        Session session = sessions.get(token);

        Path newDir = Path.of(path).normalize();

        if (newDir.getRoot() == null) {
            newDir = session.currentDir.resolve(newDir).normalize();
        }


        if (newDir.startsWith(session.root) &&
                Files.isDirectory(newDir) &&
                Files.exists(newDir)) {

            session.currentDir = newDir;
            session.output.workingDirectory(session.currentDir.toString());
        } else {
            session.output.error("Некорректный путь к директории");
        }
    }


    @Override
    public void wd(FileSystemTerminalToken token) {

        if (!sessions.containsKey(token)) {
            throw new RuntimeException("Запись о FileSystemTerminalToken отсутствует в файловом терминале.");
        }

        Session session = sessions.get(token);
        session.output.workingDirectory(session.currentDir.toString());
    }


    @Override
    public void ls(FileSystemTerminalToken token) {

        if (!sessions.containsKey(token)) {
            throw new RuntimeException("Запись о FileSystemTerminalToken отсутствует в файловом терминале.");
        }

        Session session = sessions.get(token);

        try (Stream<Path> stream = Files.list(session.currentDir)) {

            List<FileInfo> fileInfoList = stream.map(JVMFileInfo::get).toList();
            session.output.listOfFiles(fileInfoList);

        } catch (IOException e) {
            session.output.error("Ошибка обновления списка файлов");
        }
    }


    @Override
    public void put(FileSystemTerminalToken token, String sourcePath, String destinationPath) {

        if (!sessions.containsKey(token)) {
            throw new RuntimeException("Запись о FileSystemTerminalToken отсутствует в файловом терминале.");
        }

        Session session = sessions.get(token);

        Path distPath = Path.of(destinationPath).normalize();

        if (distPath.getRoot() == null) {
            distPath = session.currentDir.resolve(distPath).normalize();
        }

        if (distPath.startsWith(session.root) &&
                !Files.isDirectory(distPath) &&
                !Files.exists(distPath)) {

            try {
                Files.copy(Path.of(sourcePath), distPath);
                session.output.fileAdded(distPath.toString());

            } catch (IOException e) {
                session.output.error("Ошибка копирования файла");
            }

        } else {
            session.output.error("Некорректный путь к файлу");
        }
    }


    @Override
    public void copy(FileSystemTerminalToken token, String sourcePath, FileSystemTerminalInput destinationTerminalInput, String destinationPath) {

        if (!sessions.containsKey(token)) {
            throw new RuntimeException("Запись о FileSystemTerminalToken отсутствует в файловом терминале.");
        }

        Session session = sessions.get(token);

        Path sourceFile = Path.of(sourcePath).normalize();

        if (sourceFile.getRoot() == null) {
            sourceFile = session.currentDir.resolve(sourcePath).normalize();
        }

        if (sourceFile.startsWith(session.currentDir) &&
                !Files.isDirectory(sourceFile) &&
                Files.exists(sourceFile)) {

            destinationTerminalInput.putFile(sourceFile.toString(), destinationPath);
        }

    }


    @Override
    public void makeDirectory(FileSystemTerminalToken token, String directoryName) {

        if (!sessions.containsKey(token)) {
            throw new RuntimeException("Запись о FileSystemTerminalToken отсутствует в файловом терминале.");
        }

        Session session = sessions.get(token);

        Path directoryPath = Path.of(directoryName).normalize();

        if (directoryPath.getRoot() == null) {
            directoryPath = session.currentDir.resolve(directoryName).normalize();
        }

        if (directoryPath.startsWith(session.currentDir) &&
                !Files.exists(directoryPath)) {

            try {
                Files.createDirectory(directoryPath);
                session.output.fileAdded(directoryPath.toString());

            } catch (IOException e) {
                session.output.error("Ошибка добавления директории");
            }
        }
    }


    @Override
    public void remove(FileSystemTerminalToken token, String fileName) {

        if (!sessions.containsKey(token)) {
            throw new RuntimeException("Запись о FileSystemTerminalToken отсутствует в файловом терминале.");
        }

        Session session = sessions.get(token);

        Path filePath = Path.of(fileName).normalize();

        if (filePath.getRoot() == null) {
            filePath = session.currentDir.resolve(fileName).normalize();
        }

        if (filePath.startsWith(session.currentDir)) {

            try {
                Files.delete(filePath);
                session.output.fileRemoved(filePath.toString());

            } catch (IOException e) {
                session.output.error("Ошибка удаления файла");
            }
        }
    }


    @Override
    public void rename(FileSystemTerminalToken token, String fileName, String newFileName) {

        if (!sessions.containsKey(token)) {
            throw new RuntimeException("Запись о FileSystemTerminalToken отсутствует в файловом терминале.");
        }

        Session session = sessions.get(token);

        Path filePath = Path.of(fileName).normalize();

        if (filePath.getRoot() == null) {
            filePath = session.currentDir.resolve(fileName).normalize();
        }

        Path targetPath = filePath.resolveSibling(newFileName);

        if (filePath.startsWith(session.currentDir) && targetPath.startsWith(session.currentDir)) {

            try {
                Files.move(filePath, targetPath);
                session.output.fileRenamed(targetPath.toString());

            } catch (IOException e) {
                session.output.error("Ошибка переименования файла");
            }
        }
    }


    /**
     * Список доступных корневых директорий для данной файловой системы
     *
     * @return список корневых директорий
     */
    @Override
    public List<String> roots() {
        List<String> roots = new ArrayList<>();
        FileSystems.getDefault().getRootDirectories().forEach(path -> roots.add(path.toString()));
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
