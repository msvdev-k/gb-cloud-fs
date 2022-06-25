package org.msv.sm.request;

import org.msv.sm.RemoteFileDescription;
import org.msv.sm.RemoteFileDescription.FileType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneOffset;


/**
 * Запрос для передачи файла удалённому файловому серверу.
 * Команда выполняется для сессии, определённой своим токеном.
 * Файл копируется в текущую директорию.
 */
public class PutFile extends AbstractRequest {

    private final RemoteFileDescription fileDescription;
    private final byte[] data;


    public PutFile(int requestID, String token, RemoteFileDescription fileDescription, byte[] data) {
        super(requestID, token);
        this.fileDescription = fileDescription;
        this.data = data;
    }


    /**
     * Создать экземпляр класса PutFile на основе пути к файлу.
     * @param token токен.
     * @param source полный путь к копируемому файлу.
     * @param newFileName новое название файла, либо null если название не изменяется.
     */
    public PutFile(int requestID, String token, Path source, String newFileName) {
        super(requestID, token);

        try {

            if (Files.isDirectory(source)) {
                throw new RuntimeException();
            }

            String name = (newFileName == null) ? source.getFileName().toString() : newFileName;
            FileType type = FileType.FILE;
            long size = Files.size(source);
            LocalDateTime lastModified = LocalDateTime.ofInstant(
                    Files.getLastModifiedTime(source).toInstant(),
                    ZoneOffset.ofHours(0)
            );

            fileDescription = new RemoteFileDescription(name, type, size, lastModified);

            data = Files.readAllBytes(source);

        } catch (IOException e) {
            throw new RuntimeException("Unable to create PutFile object from source path");
        }
    }


    public RemoteFileDescription getFileDescription() {
        return fileDescription;
    }


    public byte[] getData() {
        return data;
    }

}
