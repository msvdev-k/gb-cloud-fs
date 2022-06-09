package org.msv.fm.net;

import org.msv.fm.fs.FileInfo;
import org.msv.fm.fs.FileSystemTerminalInput;
import org.msv.fm.fs.FileSystemTerminalOutput;
import org.msv.fm.fs.FileSystemTerminalToken;
import org.msv.sm.*;

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
     * @param message сообщение от сервера
     */
    private void serverMessageParser(ServerMessage message) {

        if (!sessions.containsKey(message.getToken())) {
            System.out.println(message.getToken());
            return;
        }

        Session session = sessions.get(message.getToken());

        if (message instanceof RemoteDirectoryRequest request) {
            session.output.path(Paths.get(request.getPath()));
        }

        else if (message instanceof RemoteFilesListRequest request) {
            List<FileInfo> fileInfoList = request.getFiles().stream()
                                                 .map(NettyServerFileInfo::get).toList();

            session.output.fileList(fileInfoList, Paths.get(request.getPath()));
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
    }


}
