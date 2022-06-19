package org.msv.sfs.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.msv.sm.request.*;
import org.msv.sm.response.ConnectionState;
import org.msv.sm.response.SessionSate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;


/**
 * Входящий обработчик отвечающий за аутентификацию.
 */
public class AuthenticationInboundHandler extends SimpleChannelInboundHandler<AbstractRequest> {

    // Экземпляр класса конфигурации сервера
    private final ServerConfiguration config;

    // Список сессий. Ключ - токен, значение - экземпляр класса сессии.
    private final Map<String, ServerSession> sessions = new HashMap<>();

    // Флаг аутентификации
    private boolean authFlag;

    // Корневая директория для аутентифицированного пользователя
    private Path root;


    public AuthenticationInboundHandler(ServerConfiguration config) {
        this.config = config;
        this.authFlag = false;
        this.root = null;
    }


    /**
     * Метод обрабатывающий принимаемые сообщения.
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AbstractRequest abstractRequest) throws Exception {

        // === OpenConnection ===

        if (abstractRequest instanceof OpenConnection request) {

            if (authFlag) {
                // Пользователь аутентифицирован
                ctx.writeAndFlush(new ConnectionState(request.getToken(), true));
                return;
            }

            String login = request.getLogin();
            String pass = request.getPassword();

            String id = config.getAuthenticationService().getID(login, pass);

            if (id == null) {
                // Аутентификация не удачная
                ctx.writeAndFlush(new ConnectionState(request.getToken(), false));
            } else {
                // Пользователь аутентифицирован
                authFlag = true;
                setRootDirectory(id);
                ctx.writeAndFlush(new ConnectionState(request.getToken(), true));
            }


        // === CloseConnection ===

        } else if (abstractRequest instanceof CloseConnection request) {
            authFlag = false;
            root = null;
            sessions.clear();
            ctx.writeAndFlush(new ConnectionState(request.getToken(), false));


        // === Проверка аутентификации ===

        } else if (!authFlag) {
            ctx.writeAndFlush(new ConnectionState(abstractRequest.getToken(), false));


        // === OpenSession ===

        } else if (abstractRequest instanceof OpenSession request) {

            String token = request.getToken();

            if (!sessions.containsKey(token)) {
                sessions.put(token, new ServerSession(root));
            }

            ctx.writeAndFlush(new SessionSate(token, true));


        // === CloseSession ===

        } else if (abstractRequest instanceof CloseSession request) {

            String token = request.getToken();
            sessions.remove(token);
            ctx.writeAndFlush(new SessionSate(token, false));


        // === Выполняемые сервером команды ===

        } else {

            String token = abstractRequest.getToken();

            if (!sessions.containsKey(token)) {
                ctx.writeAndFlush(new SessionSate(token, false));
                return;
            }

            ServerSession session = sessions.get(token);
            session.setRequest(abstractRequest);

            ctx.fireChannelRead(session);
        }

    }


    /**
     * Метод обрабатывающий полученные исключения.
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }


    /**
     * Установить корневую директорию пользователя по его идентификатору
     *
     * @param ID Идентификатор пользователя (его персональная папка в общем каталоге с данными)
     */
    private void setRootDirectory(String ID) throws IOException {

        root = config.getRoot().resolve(ID);

        if (Files.notExists(root)) {
            Files.createDirectory(root);
        }
    }


}
