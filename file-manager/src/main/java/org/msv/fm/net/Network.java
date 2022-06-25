package org.msv.fm.net;

import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import org.msv.sm.request.AbstractRequest;
import org.msv.sm.response.AbstractResponse;
import org.msv.sm.response.error.Error;

import java.io.IOException;
import java.net.Socket;
import java.util.function.Consumer;


/**
 * Класс, управляющий соединение с удалённым сервером
 */
public class Network {

    private Socket socket;
    private ObjectDecoderInputStream in;
    private ObjectEncoderOutputStream out;

    private final String host;
    private final int port;

    // Поток для приёма сообщений от сервера
    private Thread readThread;
    private boolean startFlag = false;

    // Функция принимающая сообщение от сервера
    private Consumer<AbstractResponse> responseConsumer;

    // Функция принимающая сообщение об ошибке от сервера
    private Consumer<Error> errorResponseConsumer;

    // Функция принимающая сообщение об ошибке (для информирования пользователя)
    private Consumer<String> errorConsumer;


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
            socket = new Socket(host, port);
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

                if (object instanceof Error error) {
                    if (errorResponseConsumer != null) {
                        errorResponseConsumer.accept(error);
                    }

                } else if (object instanceof AbstractResponse response) {
                    if (responseConsumer != null) {
                        responseConsumer.accept(response);
                    }

                } else {
                    if (errorConsumer != null) {
                        errorConsumer.accept("Server response error");
                    }
                }
            }

        } catch (Exception e) {

            if (e instanceof InterruptedException) {
                System.out.println("Connection stop");
            } else {
                if (errorConsumer != null) {
                    errorConsumer.accept("Connection lost");
                }
                System.out.println("Connection lost");
            }
            e.printStackTrace();
            System.out.println(e);

        } finally {

            startFlag = false;
            readThread = null;

            try {
                in.close();
                out.close();
                socket.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Установка функции, принимающей сообщения от сервера
     */
    public void setResponseConsumer(Consumer<AbstractResponse> responseConsumer) {
        this.responseConsumer = responseConsumer;
    }


    /**
     * Установить функцию, принимающую сообщения об ошибках со стороны сервера
     */
    public void setErrorResponseConsumer(Consumer<Error> errorResponseConsumer) {
        this.errorResponseConsumer = errorResponseConsumer;
    }


    /**
     * Установка функции, принимающей сообщение об ошибке
     */
    public void setErrorConsumer(Consumer<String> errorConsumer) {
        this.errorConsumer = errorConsumer;
    }


    /**
     * Отправить сообщение на сторону сервера
     *
     * @param request отправляемое сообщение
     */
    public void write(AbstractRequest request) {
        try {
            out.writeObject(request);
            out.flush();

        } catch (IOException e) {
            if (errorConsumer != null) {
                errorConsumer.accept("Ошибка отправления сообщения на сервер");
            }
            e.printStackTrace();
        }
    }


    /**
     * Состояние потока чтения сообщений от сервера
     *
     * @return true - сообщения от сервера читаются, false - чтение закончено
     */
    public boolean isStart() {
        return startFlag;
    }

}
