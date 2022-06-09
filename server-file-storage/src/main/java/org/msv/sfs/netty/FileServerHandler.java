package org.msv.sfs.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.msv.sm.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
public class FileServerHandler extends SimpleChannelInboundHandler<ServerMessage> {

    // Корневая директория пользователя
    private final Path rootDir;

    // Список сессий. Ключ - токен, значение - экземпляр класса сессии.
    private final Map<String, Session> sessions = new HashMap<>();


    public FileServerHandler() {
        rootDir = Path.of(System.getProperty("user.home"));
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(new ServerMessage("Server connected") {
        });
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ServerMessage serverMessage) throws Exception {

        if (serverMessage instanceof OpenTerminalMessage message) {
            Session session = new Session();
            session.currentDir = rootDir;
            sessions.put(message.getToken(), session);

            log.debug("OpenTerminalMessage with token: " + message.getToken());

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
