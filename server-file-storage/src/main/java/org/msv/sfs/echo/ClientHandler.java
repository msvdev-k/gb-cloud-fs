package org.msv.sfs.echo;

import java.io.*;
import java.net.Socket;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ClientHandler implements Runnable {

    // Todo: настроить загрузку пути к папке сервера из конфигурационного файла
    private final String serverDir = "B:/fs";
    //    private final String serverDir = "server_files";

    private final DataInputStream in;
    private final DataOutputStream out;


    public ClientHandler(Socket socket) throws IOException {

        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());

        System.out.println("Client accepted");

        sendListOfFiles(serverDir);
    }


    @Override
    public void run() {

        byte[] buf = new byte[256];

        try {
            while (true) {

                String command = in.readUTF();
                System.out.println("received: " + command);

                if (command.equals("#file#")) {

                    String fileName = in.readUTF();
                    long len = in.readLong();

                    File file = Path.of(serverDir).resolve(fileName).toAbsolutePath().toFile();

                    try (FileOutputStream fos = new FileOutputStream(file)) {

                        for (int i = 0; i < (len + 255) / 256; i++) {
                            int read = in.read(buf);
                            fos.write(buf, 0, read);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    sendListOfFiles(serverDir);
                }
            }

        } catch (Exception e) {
            System.err.println("Connection was broken");
        }
    }


    private void sendListOfFiles(String dir) throws IOException {

        out.writeUTF("#list#");

        List<String> files = getFiles(dir);
        out.writeInt(files.size());

        for (String file : files) {
            out.writeUTF(file);
        }

        out.flush();
    }


    private List<String> getFiles(String dir) {
        String[] list = new File(dir).list();
        if (list == null) return new ArrayList<>();
        return Arrays.asList(list);
    }


}
