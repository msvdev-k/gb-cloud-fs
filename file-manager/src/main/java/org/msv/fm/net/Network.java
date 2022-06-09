package org.msv.fm.net;

import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import org.msv.sm.ServerMessage;

import java.io.IOException;
import java.net.Socket;
import java.util.function.Consumer;


/**
 * Класс, управляющий соединение с удалённым сервером
 */
public class Network {

    private ObjectDecoderInputStream in;
    private ObjectEncoderOutputStream out;

    String host;
    int port;

    // Поток для приёма сообщений от сервера
    Thread readThread;
    boolean startFlag = false;

    // Функция принимающая сообщение от сервера
    Consumer<ServerMessage> readConsumer;

    // Функция принимающая сообщение об ошибке (для информирования пользователя)
    Consumer<String> errorConsumer;


    public Network(String host, int port) {
        this.host = host;
        this.port = port;
    }


    /**
     * Запуск соединения с сервером
     */
    public void start() {

        // Предотвращение повторного запуска
        if (startFlag) return;

        try {
            Socket socket = new Socket(host, port);
            in = new ObjectDecoderInputStream(socket.getInputStream());
            out = new ObjectEncoderOutputStream(socket.getOutputStream());

            readThread = new Thread(this::readLoop);
            readThread.setDaemon(true);
            readThread.start();

            startFlag = true;

        } catch (Exception e) {
            if (errorConsumer != null) {
                errorConsumer.accept("Connection error");
            }
            System.err.println("Connection error");
        }
    }


    /**
     * Остановка соединения с сервером
     */
    public void stop() {
        if (readThread != null) {
            readThread.interrupt();
            readThread = null;
        }
    }


    /**
     * Цикл чтения сообщений от сервера
     */
    private void readLoop() {

        try {
            while (startFlag && !Thread.currentThread().isInterrupted()) {

                Object object = in.readObject();

                if (object instanceof ServerMessage serverMessage) {
                    if (readConsumer != null) {
                        readConsumer.accept(serverMessage);
                    }
                }
                else {
                    if (errorConsumer != null) {
                        errorConsumer.accept("Server message error");
                    }
                }
            }

            readThread = null;

        } catch (Exception e) {

            if (e instanceof InterruptedException) {
                System.err.println("Connection stop");
            }
            else {
                if (errorConsumer != null) {
                    errorConsumer.accept("Connection lost");
                }
                System.err.println("Connection lost");
            }

            startFlag = false;
            readThread = null;
        }
    }



    /**
     * Установка функции, принимающей сообщения от сервера
     * @param readConsumer
     */
    public void setReadConsumer(Consumer<ServerMessage> readConsumer) {
        this.readConsumer = readConsumer;
    }


    /**
     * Установка функции, принимающей сообщение об ошибке
     * @param errorConsumer
     */
    public void setErrorConsumer(Consumer<String> errorConsumer) {
        this.errorConsumer = errorConsumer;
    }


    /**
     * Отправить сообщение на сторону сервера
     * @param message
     */
    public void write(ServerMessage message) {
        try {
            out.writeObject(message);
            out.flush();

        } catch (IOException e) {
            if (errorConsumer != null) {
                errorConsumer.accept("Ошибка отправления сообщения на сервер");
            }
            e.printStackTrace();
        }
    }


    /**
     * Соединение запущено
     * @return
     */
    public boolean isStart() {
        return startFlag;
    }

}
