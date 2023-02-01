package org.msv.sfs.echo;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class EchoServer {

    private static final int PORT = 8189;


    public static void main(String[] args) throws IOException {

        try (ServerSocket server = new ServerSocket(PORT)) {
            System.out.println("Server started");

            while (true) {
                Socket socket = server.accept();
                ClientHandler handler = new ClientHandler(socket);
                new Thread(handler).start();
            }
        }

    }

}
