package org.msv.fm.net;

import org.msv.fm.fs.FileInfo;
import org.msv.fm.fs.FileSystemTerminalInput;
import org.msv.fm.fs.FileSystemTerminalOutput;
import org.msv.fm.fs.FileSystemTerminalToken;
import org.msv.sm.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Терминал файловой системы сервера Netty
 */
public class NettyServerFileSystemTerminalOutput implements FileSystemTerminalOutput {

    /**
     * Список сессий. Ключ - токен, значение - экземпляр класса сессии.
     */
    private final Map<String, Session> sessions = new HashMap<>();

    /**
     * Соединение с удалённым сервером
     */
    private Network network;


    public NettyServerFileSystemTerminalOutput(String host, int port) {
        this.network = new Network(host, port);
        this.network.setReadConsumer(this::serverMessageParser);
    }


    /**
     * Метод получающий сообщения от сервера
     *
     * @param message сообщение от сервера
     */
    private void serverMessageParser(ServerMessage message) {

        if (!sessions.containsKey(message.getToken())) {
            System.out.println(message.getToken());
            return;
        }

        Session session = sessions.get(message.getToken());

        // === RemoteDirectoryRequest ===
        if (message instanceof RemoteDirectoryRequest request) {
            session.output.path(Paths.get(request.getPath()));


        // === RemoteFilesListRequest ===
        } else if (message instanceof RemoteFilesListRequest request) {
            List<FileInfo> fileInfoList = request.getFiles().stream()
                    .map(NettyServerFileInfo::get).toList();

            session.output.fileList(fileInfoList, Paths.get(request.getPath()));


        // === AddFileRequest ===
        } else if (message instanceof AddFileRequest request) {
            session.output.addFile(Path.of(request.getPath()));


        // === FileDataMessage ===
        } else if (message instanceof FileDataMessage request) {

            if (session.getFilePath == null || session.getFileTerminal == null) return;

            try {
                Files.write(session.getFilePath, request.getData());

                session.getFileTerminal.addFile(session.getFilePath);
                session.getFilePath = null;
                session.getFileTerminal = null;

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Начать новую сессию в терминале
     *
     * @param input объект получающий сообщения от терминала
     * @return токен новой сессии
     */
    @Override
    public FileSystemTerminalToken startSession(FileSystemTerminalInput input) {

        network.start();

        FileSystemTerminalToken token = new FileSystemTerminalToken();

        Session session = new Session();
        session.output = input;
        session.currentDir = Paths.get("");
        session.root = null;

        sessions.put(token.toString(), session);
        network.write(new OpenTerminalMessage(token.toString()));

        return token;
    }


    /**
     * Остановить сессию
     *
     * @param token токен сессии
     */
    @Override
    public void stopSession(FileSystemTerminalToken token) {
        sessions.remove(token.toString());
        network.write(new CloseTerminalMessage(token.toString()));

        if (sessions.isEmpty()) {
            network.stop();
        }
    }


    /**
     * Изменить текущую директорию
     *
     * @param token токен сессии
     * @param path  путь до директории
     */
    @Override
    public void cd(FileSystemTerminalToken token, String path) {

        if (!sessions.containsKey(token.toString())) {
            throw new RuntimeException("Запись о FileSystemTerminalToken отсутствует в файловом терминале.");
        }

        network.write(new ChangeDirectoryMessage(token.toString(), path));
    }

    /**
     * Получить список файлов по указанному пути
     *
     * @param token токен сессии
     * @param path  путь из которого собираются файлы
     */
    @Override
    public void ls(FileSystemTerminalToken token, String path) {

        if (!sessions.containsKey(token.toString())) {
            throw new RuntimeException("Запись о FileSystemTerminalToken отсутствует в файловом терминале.");
        }

        network.write(new FileListingMessage(token.toString(), path));
    }


    /**
     * Скопировать файл из локальной файловой системы в файловую систему терминала.
     *
     * @param token           токен сессии
     * @param sourcePath      полный путь к файлу локальной файловой системы (источник)
     * @param destinationPath путь к файлу в файловой системе терминала (приёмник)
     */
    @Override
    public void put(FileSystemTerminalToken token, String sourcePath, String destinationPath) {

        if (!sessions.containsKey(token.toString())) {
            throw new RuntimeException("Запись о FileSystemTerminalToken отсутствует в файловом терминале.");
        }

        Session session = sessions.get(token.toString());

        try {
            FileDataMessage fdm = FileDataMessage.of(token.toString(), sourcePath, destinationPath);
            network.write(fdm);

        } catch (IOException e) {
            session.output.error("Ошибка копирования файла");
            e.printStackTrace();
        }
    }

    /**
     * Скопировать файл из файловой системы терминала на локальную файловую систему
     *
     * @param token           токен сессии
     * @param sourcePath      путь к файлу в файловой системе терминала (источник)
     * @param destinationPath полный путь к файлу локальной файловой системы (приёмник)
     */
    @Override
    public void get(FileSystemTerminalToken token, String sourcePath, String destinationPath, FileSystemTerminalInput destinationTerminal) {

        if (!sessions.containsKey(token.toString())) {
            throw new RuntimeException("Запись о FileSystemTerminalToken отсутствует в файловом терминале.");
        }

        Session session = sessions.get(token.toString());

        session.getFilePath = Path.of(destinationPath);
        session.getFileTerminal = destinationTerminal;

        network.write(new GetRemoteFileMessage(token.toString(), sourcePath));
    }


    /**
     * Получить текущую корневую директорию
     * Метод неопределённы для серверной файловой системы
     *
     * @param token токен сессии
     */
    @Override
    public void root(FileSystemTerminalToken token) {

    }

    /**
     * Список доступных корневых директорий для данной файловой системы
     * Метод неопределённы для серверной файловой системы
     *
     * @return null
     */
    @Override
    public List<Path> roots() {
        return null;
    }


    /**
     * Описание текущей сессии
     */
    private class Session {
        public FileSystemTerminalInput output;
        public Path root;
        public Path currentDir;

        // Путь для сохранения полученного файла и терминал для уведомления о сохранении
        public Path getFilePath;
        public FileSystemTerminalInput getFileTerminal;

    }


}
