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



        // === ChangeDirectory ===

        if (abstractRequest instanceof ChangeDirectory request) {

            String dirName = request.getDirectoryName();

            if (session.changeDirectory(dirName)) {
                ctx.writeAndFlush(new CurrentDirectory(abstractRequest, session.getCurrentDirectory()));

            } else {
                ctx.writeAndFlush(new ChangeDirectoryError(abstractRequest, "Error change directory to " + dirName));
            }



        // === WorkingDirectory ===

        } else if (abstractRequest instanceof WorkingDirectory) {

            ctx.writeAndFlush(new CurrentDirectory(abstractRequest, session.getCurrentDirectory()));



        // === GetListOfFiles ===

        } else if (abstractRequest instanceof GetListOfFiles) {

            List<RemoteFileDescription> files = session.getListOfFiles();

            if (files != null) {
                ctx.writeAndFlush(new ListOfFiles(abstractRequest, files));

            } else {
                ctx.writeAndFlush(new GetListOfFilesError(abstractRequest, "Error getting list of files in current directory"));
            }



        // === PutFile ===

        } else if (abstractRequest instanceof PutFile request) {

            RemoteFileDescription newFileDescription = session.putFile(request.getFileDescription(), request.getData());

            if (newFileDescription != null) {
                ctx.writeAndFlush(new FileAdded(abstractRequest, session.getCurrentDirectory(), newFileDescription));

            } else {
                ctx.writeAndFlush(new PutFileError(abstractRequest, "Error writing file"));
            }



        // === GetFile ===

        } else if (abstractRequest instanceof GetFile request) {

            FileContent fileContent = session.getFile(request);

            if (fileContent != null) {
                ctx.writeAndFlush(fileContent);

            } else {
                ctx.writeAndFlush(new FileContentError(abstractRequest, "File read error"));
            }



        // === MakeDirectory ===

        } else if (abstractRequest instanceof MakeDirectory request) {

            RemoteFileDescription description = session.makeDirectory(request.getNewDirectoryName());

            if (description != null) {
                ctx.writeAndFlush(new FileAdded(abstractRequest, session.getCurrentDirectory(), description));

            } else {
                ctx.writeAndFlush(new MakeDirectoryError(abstractRequest, "Directory creation error"));
            }



        // === Remove ===

        } else if (abstractRequest instanceof Remove request) {

            RemoteFileDescription description = session.remove(request.getFileName());

            if (description != null) {
                ctx.writeAndFlush(new FileRemoved(abstractRequest, description));

            } else {
                ctx.writeAndFlush(new RemoveError(abstractRequest, "Deletion error"));
            }



        // === Rename ===

        } else if (abstractRequest instanceof Rename request) {

            RemoteFileDescription description = session.rename(request.getName(), request.getNewName());

            if (description != null) {
                ctx.writeAndFlush(new FileRenamed(abstractRequest, request.getName(), description));

            } else {
                ctx.writeAndFlush(new RenameError(abstractRequest, "Renaming error"));
            }



        }

    }

}
