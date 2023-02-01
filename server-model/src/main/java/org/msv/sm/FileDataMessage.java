package org.msv.sm;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;


/**
 * Команда для передачи файла по сети
 */
public class FileDataMessage extends PathServerMessage {

    private final long size;
    private final byte[] data;


    public FileDataMessage(String token, String path, long size, byte[] data) {
        super(token, path);
        this.size = size;
        this.data = data;
    }


    public static FileDataMessage of(String token, String source, String destination) throws IOException {
        Path src = Path.of(source);
        long size = Files.size(src);
        byte[] data = Files.readAllBytes(src);

        return new FileDataMessage(token, destination, size, data);
    }


    public long getSize() {
        return size;
    }


    public byte[] getData() {
        return data;
    }
}
