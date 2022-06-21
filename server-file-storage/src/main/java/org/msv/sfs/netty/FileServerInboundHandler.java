package org.msv.sfs.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.msv.sm.RemoteFileDescription;
import org.msv.sm.request.*;
import org.msv.sm.response.*;
import org.msv.sm.response.error.*;

import java.util.List;


/**
 * Входящий обработчик отвечающий за манипуляции с файловой системой.
 */
@Slf4j
public class FileServerInboundHandler extends SimpleChannelInboundHandler<ServerSession> {


    /**
     * Метод обрабатывающий принимаемые сообщения.
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ServerSession session) throws Exception {

        // Обрабатываемый запрос
        AbstractRequest abstractRequest = session.getRequest();

        // Токен
        String token = session.getToken();



        // === ChangeDirectory ===

        if (abstractRequest instanceof ChangeDirectory request) {

            String dirName = request.getDirectoryName();

            if (session.changeDirectory(dirName)) {
                ctx.writeAndFlush(new CurrentDirectory(token, session.getCurrentDirectory()));

            } else {
                ctx.writeAndFlush(new ChangeDirectoryError(token, "Error change directory to " + dirName));
            }



        // === WorkingDirectory ===

        } else if (abstractRequest instanceof WorkingDirectory) {

            ctx.writeAndFlush(new CurrentDirectory(token, session.getCurrentDirectory()));



        // === GetListOfFiles ===

        } else if (abstractRequest instanceof GetListOfFiles) {

            List<RemoteFileDescription> files = session.getFistOfFiles();

            if (files != null) {
                ctx.writeAndFlush(new ListOfFiles(token, files));

            } else {
                ctx.writeAndFlush(new GetListOfFilesError(token, "Error getting list of files in current directory"));
            }



        // === PutFile ===

        } else if (abstractRequest instanceof PutFile request) {

            RemoteFileDescription newFileDescription = session.putFile(request.getFileDescription(), request.getData());

            if (newFileDescription != null) {
                ctx.writeAndFlush(new FileAdded(token, newFileDescription));

            } else {
                ctx.writeAndFlush(new PutFileError(token, "Error writing file"));
            }



        // === GetFile ===

        } else if (abstractRequest instanceof GetFile request) {

            FileContent fileContent = session.getFile(request.getFileName());

            if (fileContent != null) {
                ctx.writeAndFlush(fileContent);

            } else {
                ctx.writeAndFlush(new FileContentError(token, "File read error"));
            }



        // === MakeDirectory ===

        } else if (abstractRequest instanceof MakeDirectory request) {

            RemoteFileDescription description = session.makeDirectory(request.getNewDirectoryName());

            if (description != null) {
                ctx.writeAndFlush(new FileAdded(token, description));

            } else {
                ctx.writeAndFlush(new MakeDirectoryError(token, "Directory creation error"));
            }



        // === Remove ===

        } else if (abstractRequest instanceof Remove request) {

            RemoteFileDescription description = session.remove(request.getFileName());

            if (description != null) {
                ctx.writeAndFlush(new FileRemoved(token, description));

            } else {
                ctx.writeAndFlush(new RemoveError(token, "Deletion error"));
            }



        // === Rename ===

        } else if (abstractRequest instanceof Rename request) {

            RemoteFileDescription description = session.rename(request.getName(), request.getNewName());

            if (description != null) {
                ctx.writeAndFlush(new FileRenamed(token, request.getName(), description));

            } else {
                ctx.writeAndFlush(new RenameError(token, "Renaming error"));
            }



        }

    }

}
