package org.msv.fm.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;


public class Network {

    private final DataInputStream in;
    private final DataOutputStream out;


    public Network(String host, int port) throws IOException {

        Socket socket = new Socket(host, port);
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());

    }


    public int readInt() throws IOException {
        return in.readInt();
    }


    public String readMessage() throws IOException {
        return in.readUTF();
    }


    public void writeMessage(String message) throws IOException {
        out.writeUTF(message);
        out.flush();
    }


    public DataOutputStream getOut() {
        return out;
    }


    public DataInputStream getIn() {
        return in;
    }

}
