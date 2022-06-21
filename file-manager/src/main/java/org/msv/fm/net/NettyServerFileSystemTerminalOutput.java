package org.msv.fm.net;

import org.msv.fm.fs.*;
import org.msv.sm.*;
import org.msv.sm.request.*;
import org.msv.sm.response.AbstractResponse;

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


    // Флаг аутентификации
    private boolean authFlag = false;


    public NettyServerFileSystemTerminalOutput(String host, int port) {
        this.network = new Network(host, port);
        this.network.setReadConsumer(this::serverMessageParser);
    }


    @Override
    public void connect(String login, String password, FileSystemTerminalInput input) {
        network.start();
        if (network.isStart()) {
            network.write(new OpenConnection("", login, password));

        } else {
            input.error("Error connecting to remote server");
        }
    }


//    @Override
//    public void connectionState(FileSystemTerminalInput input) {
//        input.connectionState(authFlag);
//    }


    @Override
    public void closeConnection() {
        if (network.isStart()) {
            network.write(new CloseConnection(""));
        }
    }


    @Override
    public void startSession(FileSystemTerminalInput input, FileSystemLocation location) {

        if (network.isStart()) {

            FileSystemTerminalToken token = new FileSystemTerminalToken();

            Session session = new Session();
            session.output = input;
            session.currentDir = Path.of(location.getRoot());
            session.root = Path.of(location.getRoot());

            sessions.put(token.toString(), session);

            network.write(new OpenSession(token.toString()));

        } else {
            input.connectionState(false);
        }
    }


    @Override
    public void stopSession(FileSystemTerminalToken token) {
        network.write(new CloseSession(token.toString()));
    }


    @Override
    public void cd(FileSystemTerminalToken token, String path) {
        if (!sessions.containsKey(token.toString())) {
            throw new RuntimeException("Запись о FileSystemTerminalToken отсутствует в файловом терминале.");
        }
        network.write(new ChangeDirectory(token.toString(), path));
    }


    @Override
    public void wd(FileSystemTerminalToken token) {
        if (!sessions.containsKey(token.toString())) {
            throw new RuntimeException("Запись о FileSystemTerminalToken отсутствует в файловом терминале.");
        }
        network.write(new WorkingDirectory(token.toString()));
    }


    @Override
    public void ls(FileSystemTerminalToken token) {
        if (!sessions.containsKey(token.toString())) {
            throw new RuntimeException("Запись о FileSystemTerminalToken отсутствует в файловом терминале.");
        }
        network.write(new GetListOfFiles(token.toString()));
    }


    @Override
    public void put(FileSystemTerminalToken token, String sourcePath, String destinationPath) {

        if (!sessions.containsKey(token.toString())) {
            throw new RuntimeException("Запись о FileSystemTerminalToken отсутствует в файловом терминале.");
        }

        Session session = sessions.get(token.toString());

        try {
            PutFile file = new PutFile(token.toString(), Path.of(sourcePath), destinationPath);
            network.write(file);

        } catch (Exception e) {
            session.output.error("Ошибка копирования файла");
            e.printStackTrace();
        }
    }


    @Override
    public void copy(FileSystemTerminalToken token, String sourcePath, FileSystemTerminalInput destinationTerminalInput, String destinationPath) {



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
     * Метод получающий сообщения от сервера
     *
     * @param message сообщение от сервера
     */
    private void serverMessageParser(AbstractResponse message) {

        // === Аутентификация ===
        if (message instanceof ConnectionRequest request) {
            authFlag = request.isConnect();
        }


        if (!sessions.containsKey(message.getToken())) {
            System.out.println(message.getToken());
            return;
        }

        Session session = sessions.get(message.getToken());


        if (!authFlag) {
            session.output.error("Для удалённого подключения необходимо ввести логин и пароль");
            return;
        }


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
    public List<String> roots() {
        return null;
    }


    /**
     * Описание текущей сессии
     */
    private class Session {
        public FileSystemTerminalInput output;
        public Path root;
        public Path currentDir;

        // Последняя отправляемая команда на сервер
        public AbstractRequest lastRequest;

        // Путь для сохранения полученного файла и терминал для уведомления о сохранении
        public Path getFilePath;
        public FileSystemTerminalInput getFileTerminal;

    }


}
