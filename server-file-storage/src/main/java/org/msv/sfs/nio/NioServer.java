package org.msv.sfs.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Set;


public class NioServer {

    public static void main(String[] args) throws IOException {
        NioServer nioServer = new NioServer();

        System.out.println("Start NIO server");
        System.out.printf("root: %s\n", nioServer.getRootDir().toString());

        nioServer.start();
    }


    private static final int PORT = 8189;

    private final ServerSocketChannel server;
    private final Selector selector;


    // Путь к корневому каталогу на сервере. Относительно него отсчитываются
    // все директории пользователя
    private final Path rootDir;

    // Текущая директория пользователя
    private Path currentDir;


    public NioServer() throws IOException {
        server = ServerSocketChannel.open();
        selector = Selector.open();

        server.bind(new InetSocketAddress(PORT));
        server.configureBlocking(false);
        server.register(selector, SelectionKey.OP_ACCEPT);

        rootDir = Paths.get(System.getProperty("user.home"));
        currentDir = Paths.get(rootDir.toString());
    }


    /**
     * Запуск сервера
     *
     * @throws IOException
     */
    public void start() throws IOException {

        while (server.isOpen()) {

            selector.select();
            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = keys.iterator();

            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                if (key.isAcceptable()) {
                    handleAccept();
                }
                if (key.isReadable()) {
                    handleRead(key);
                }
                iterator.remove();
            }
        }
    }


    /**
     * Подключение нового пользователя
     *
     * @throws IOException
     */
    private void handleAccept() throws IOException {
        SocketChannel channel = server.accept();
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_READ);
        channel.write(ByteBuffer.wrap("Welcome to NIO Server terminal!\n\r-> ".getBytes(StandardCharsets.UTF_8)));
    }


    /**
     * Чтение сообщений от пользователя
     *
     * @param key SelectionKey
     * @throws IOException
     */
    private void handleRead(SelectionKey key) throws IOException {

        SocketChannel channel = (SocketChannel) key.channel();

        // Расширяемый буфер для более корректной склейки многобайтовых символов.
        // StringBuilder может при склейке буферов разрубить символ на две части
        // и неправильно его интерпретировать
        byte[] buffer = new byte[16];
        int limit = 0;
        ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);

        // Цикл чтения всех пришедших данных из канала
        while (channel.isOpen()) {

            int read = channel.read(byteBuffer);

            if (read < 0) {
                channel.close();
                return;
            }
            if (read == 0) {
                break;
            }

            limit += read;

            if (limit == buffer.length) {

                // Размер буфера увеличивается в 1.5 раза
                byte[] newBuffer = new byte[buffer.length + buffer.length / 2];
                System.arraycopy(buffer, 0, newBuffer, 0, buffer.length);
                byteBuffer = ByteBuffer.wrap(newBuffer, buffer.length, newBuffer.length - buffer.length);
                buffer = newBuffer;
            }
        }

        // Удаление из конца строки символов \n\r
        while (buffer[limit - 1] == (byte) '\n' || buffer[limit - 1] == (byte) '\r') limit--;

        // Полученная строка с сообщением
        String msg = new String(buffer, 0, limit, StandardCharsets.UTF_8);

        // StringBuilder для формирования ответа
        StringBuilder sb = new StringBuilder();

        // Основное условие выбора исполняемых команд
        if (msg.startsWith("ls")) {
            listOfFiles(sb);

        } else if (msg.startsWith("cd ")) {
            changeDirectory(msg, sb);

        } else if (msg.startsWith("cat ")) {
            readFile(msg, sb);

        } else {
            sb.append("Отправленная команда неизвестна:\n\r");
            sb.append(msg);
        }

        // Завершение сообщения и отправка клиенту
        handleWrite(key, sb);
    }


    /**
     * Окончательная подготовка и отправка сообщения пользователю
     *
     * @param key     SelectionKey
     * @param message StringBuilder отправляемое сообщение
     * @throws IOException
     */
    private void handleWrite(SelectionKey key, StringBuilder message) throws IOException {

        // Показываем пользователю текущую директорию
        message.append("\n\r[");
        message.append(FileSystems.getDefault().getSeparator());

        if (rootDir.getNameCount() != currentDir.getNameCount()) {
            Path path = currentDir.subpath(rootDir.getNameCount(), currentDir.getNameCount());
            message.append(path);
        } else {
            message.append(".");
        }
        message.append("]\n\r-> ");

        byte[] bytes = message.toString().getBytes(StandardCharsets.UTF_8);

        if (key.isValid() && key.channel() instanceof SocketChannel sc) {
            sc.write(ByteBuffer.wrap(bytes));
        }
    }


    /**
     * Формирование списка файлов
     * @param stringBuilder
     */
    private void listOfFiles(StringBuilder stringBuilder) {

        StringBuilder sb = new StringBuilder();

        try {
            for (Object path : Files.list(currentDir).toArray()) {
                sb.append(((Path) path).getFileName().toString());
                sb.append("\n\r");
            }

            stringBuilder.append(sb);

        } catch (IOException e) {
            stringBuilder.append("Ошибка чтения списка файлов");
        }
    }

    /**
     * Установка текущей директории
     * @param command команда: cd dir_name
     * @param stringBuilder
     */
    private void changeDirectory(String command, StringBuilder stringBuilder) {

       String strPath = command.replaceFirst("cd ", "").trim();
       Path path = currentDir.resolve(Paths.get(strPath)).normalize();

        // !!! Для исключения инъекций позволяющих перейти выше rootDir и выйти за пределы
        // своего каталога (например при передаче команд: cd ../other_user/file или cd /other_user/file
        // необходимо дополнительное условие проверки startsWith(rootDir)!!!
        if (path.startsWith(rootDir) && Files.isDirectory(path)) {

            currentDir = path;
        }
        else {
            stringBuilder.append("Неверно указано имя каталога");
        }
    }


    /**
     * Чтение содержимого файла
     * @param command команда: cat file_name
     * @param stringBuilder
     */
    private void readFile(String command, StringBuilder stringBuilder) {

        String strPath = command.replaceFirst("cat ", "").trim();
        Path path = currentDir.resolve(Paths.get(strPath)).normalize();

        // !!! Для исключения инъекций позволяющих перейти выше rootDir и выйти за пределы
        // своего каталога (например при передаче команд: cd ../other_user/file или cd /other_user/file
        // необходимо дополнительное условие проверки startsWith(rootDir)!!!
        if (path.startsWith(rootDir) && !Files.isDirectory(path)) {

            if (Files.isReadable(path)) {

                try {

                    StringBuilder sb = new StringBuilder();
                    Files.readAllLines(path).stream().map(s -> s+"\n\r").forEach(sb::append);

                    stringBuilder.append(sb);

                } catch (IOException e) {
                    stringBuilder.append("Ошибка чтения файла");
                }
            }
            else {
                stringBuilder.append("Файл не доступен для чтения");
            }
        }
        else {
            stringBuilder.append("Неверно указано имя файла");
        }

    }


    public Path getRootDir() {
        return Paths.get(rootDir.toUri());
    }





}
