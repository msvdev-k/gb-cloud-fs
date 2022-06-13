package org.msv.sfs.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.msv.sfs.authentication.AuthenticationService;
import org.msv.sm.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
public class FileServerHandler extends SimpleChannelInboundHandler<ServerMessage> {

    // Сервис аутентификации
    private final AuthenticationService authenticationService;

    // Корневая директория пользователя
    private Path rootDir;

    // Список сессий. Ключ - токен, значение - экземпляр класса сессии.
    private final Map<String, Session> sessions = new HashMap<>();


    public FileServerHandler(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
        this.rootDir = null;
    }


    private void setRootDir(String rootDir) {
        this.rootDir = Path.of(System.getProperty("user.home")).resolve(rootDir);
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(new ConnectionRequest("Server connected", false));
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ServerMessage serverMessage) throws Exception {





        // === Аутентификация пользователя

        if (serverMessage instanceof ConnectMessage message) {

            log.debug("ConnectMessage with token: " + message.getToken());

            String id = authenticationService.getID(message.getLogin(), message.getPassword());

            log.debug("Connect with id: " + id);

            if (id != null) {
                setRootDir(id);
                // Пользователь аутентифицировался
                ctx.writeAndFlush(new ConnectionRequest(serverMessage.getToken(), true));

            } else {
                // Пользователь не аутентифицировался
                ctx.writeAndFlush(new ConnectionRequest(serverMessage.getToken(), false));
            }



        // === Проверка аутентификации пользователя по корневому каталогу ===

        } else if (this.rootDir == null) {
            // Пользователь не аутентифицировался
            log.debug("this.rootDir == null");
            ctx.writeAndFlush(new ConnectionRequest(serverMessage.getToken(), false));




        // === Открыть сессию ===

        } else if (serverMessage instanceof OpenTerminalMessage message) {
            Session session = new Session();
            session.currentDir = rootDir;
            sessions.put(message.getToken(), session);

            log.debug("OpenTerminalMessage with token: " + message.getToken());



        // === Изменить текущую директорию ===

        } else if (serverMessage instanceof ChangeDirectoryMessage message) {
            String token = message.getToken();

            log.debug("ChangeDirectoryMessage with token: " + token);
            log.debug("ChangeDirectoryMessage with path: " + message.getPath());

            if (!sessions.containsKey(token)) return;

            Session session = sessions.get(token);
            Path newDir = session.currentDir.resolve(message.getPath()).normalize();

            if (newDir.startsWith(rootDir) &&
                    Files.isDirectory(newDir) &&
                    Files.exists(newDir)) {

                session.currentDir = newDir;

                String path = "";
                if (rootDir.getNameCount() < newDir.getNameCount()) {
                    path = newDir.subpath(rootDir.getNameCount(), newDir.getNameCount()).toString();
                }

                log.debug("Send to user path: " + path + " and token: " + message.getToken());

                ctx.writeAndFlush(new RemoteDirectoryRequest(message.getToken(), path));
            }



        // === Запрос списка файлов в указанной директории ===

        } else if (serverMessage instanceof FileListingMessage message) {
            String token = message.getToken();

            log.debug("FileListingMessage with token " + token);

            if (!sessions.containsKey(token)) return;

            Path dir = rootDir.resolve(message.getPath()).normalize();

            if (dir.startsWith(rootDir) &&
                    Files.isDirectory(dir) &&
                    Files.exists(dir)) {

                List<RemoteFileDescription> files = Files.list(dir).map(ServerFileInfo::get).toList();

                String path = "";
                if (rootDir.getNameCount() < dir.getNameCount()) {
                    path = dir.subpath(rootDir.getNameCount(), dir.getNameCount()).toString();
                }
                log.debug("Send to user files from path: " + path + " and token: " + message.getToken());

                ctx.writeAndFlush(new RemoteFilesListRequest(message.getToken(), path, files));


            }



        // === Сохранить файл на сервере ===

        } else if (serverMessage instanceof FileDataMessage message) {
            String token = message.getToken();

            log.debug("FileDataMessage with token: " + token);
            log.debug("FileDataMessage with path: " + message.getPath());

            if (!sessions.containsKey(token)) return;

            Session session = sessions.get(token);
            Path fileName = session.currentDir.resolve(message.getPath()).normalize();

            if (fileName.startsWith(rootDir) &&
                    !Files.isDirectory(fileName) &&
                    !Files.exists(fileName)) {

                Files.write(fileName, message.getData());

                String path = "";
                if (rootDir.getNameCount() < fileName.getNameCount()) {
                    path = fileName.subpath(rootDir.getNameCount(), fileName.getNameCount()).toString();
                }

                log.debug("Send to user path: " + path + " and token: " + message.getToken());

                ctx.writeAndFlush(new AddFileRequest(message.getToken(), path));
            }



        // === Получить файл с сервера ===

        } else if (serverMessage instanceof GetRemoteFileMessage message) {
            String token = message.getToken();

            log.debug("GetRemoteFileMessage with token: " + token);
            log.debug("GetRemoteFileMessage with path: " + message.getPath());

            if (!sessions.containsKey(token)) return;

            Session session = sessions.get(token);
            Path fileName = session.currentDir.resolve(message.getPath()).normalize();

            if (fileName.startsWith(rootDir) &&
                    !Files.isDirectory(fileName) &&
                    Files.exists(fileName)) {

                String path = "";
                if (rootDir.getNameCount() < fileName.getNameCount()) {
                    path = fileName.subpath(rootDir.getNameCount(), fileName.getNameCount()).toString();
                }

                log.debug("Send to user path: " + path + " and token: " + message.getToken());

                ctx.writeAndFlush(FileDataMessage.of(message.getToken(), fileName.toString(), path));
            }



        // === Закрыть текущую сессию (соединение с сервером не закрывается) ===

        } else if (serverMessage instanceof CloseTerminalMessage message) {
            sessions.remove(message.getToken());

            log.debug("CloseTerminalMessage with token: " + message.getToken());
        }

    }

    /**
     * Описание текущей сессии
     */
    private class Session {
        public Path currentDir;
    }

}
