package org.msv.fm.net;

import org.msv.fm.fs.*;
import org.msv.sm.request.*;
import org.msv.sm.response.*;
import org.msv.sm.response.error.Error;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;




/**
 * Терминал файловой системы сервера Netty
 */
public class NettyServerFileSystemTerminalOutput implements FileSystemTerminalOutput {

    /**
     * Список сессий. Ключ - токен, значение - экземпляр класса сессии.
     */
    private final Map<String, NettyServerFileSystemTerminalSession> sessions = new HashMap<>();

    /**
     * Соединение с удалённым сервером
     */
    private final Network network;


    // Флаг аутентификации
    private boolean authFlag = false;


    public NettyServerFileSystemTerminalOutput(String host, int port) {
        this.network = new Network(host, port);
        this.network.setResponseConsumer(this::serverResponseParser);
        this.network.setErrorResponseConsumer(this::errorResponseParser);

        // Сессия, управляющая подключение к удалённому серверу (аутентификацией)
        NettyServerFileSystemTerminalSession connectionSession = new NettyServerFileSystemTerminalSession("0");
        sessions.put("0", connectionSession);
    }


    @Override
    public void connect(String login, String password, FileSystemTerminalInput input) {
        network.start();
        if (network.isStart()) {

            NettyServerFileSystemTerminalSession session = sessions.get("0");
            int requestID = session.getRequestID();

            AbstractRequest request = new OpenConnection(requestID, "0", login, password);

            if (input == null) {
                session.putRequest(requestID, request, null);
            } else {
                session.putRequest(requestID, request, List.of(input));
            }

            network.write(request);

        } else {
            input.error("Error connecting to remote server");
        }
    }


    @Override
    public void closeConnection() {
//        if (network.isStart()) {
//            network.write(new CloseConnection(""));
//        }
    }


    @Override
    public void startSession(FileSystemTerminalInput input, FileSystemLocation location) {
        if (network.isStart() && authFlag) {

            FileSystemTerminalToken token = new FileSystemTerminalToken();

            NettyServerFileSystemTerminalSession session = new NettyServerFileSystemTerminalSession(token.toString());
            sessions.put(token.toString(), session);

            session.setOutput(input);
            int requestID = session.getRequestID();
            AbstractRequest request = new OpenSession(requestID, session.getToken());

            session.putRequest(requestID, request, List.of(token));

            network.write(request);

        } else {
            input.connectionState(false);
        }
    }


    @Override
    public void stopSession(FileSystemTerminalToken token) {
        if (sessions.containsKey(token.toString())) {
            NettyServerFileSystemTerminalSession session = sessions.get(token.toString());

            if (network.isStart() && authFlag) {
                int requestID = session.getRequestID();
                AbstractRequest request = new CloseSession(requestID, session.getToken());
                session.putRequest(requestID, request, null);

                network.write(request);

            } else {
                session.getOutput().connectionState(false);
            }
        }
    }


    @Override
    public void cd(FileSystemTerminalToken token, String path) {
        if (sessions.containsKey(token.toString())) {
            NettyServerFileSystemTerminalSession session = sessions.get(token.toString());

            if (network.isStart() && authFlag) {
                int requestID = session.getRequestID();
                AbstractRequest request = new ChangeDirectory(requestID, session.getToken(), path);
                session.putRequest(requestID, request, null);

                network.write(request);

            } else {
                session.getOutput().connectionState(false);
            }
        } else {
            throw new RuntimeException("Запись о FileSystemTerminalToken отсутствует в файловом терминале.");
        }
    }


    @Override
    public void wd(FileSystemTerminalToken token) {
        if (sessions.containsKey(token.toString())) {
            NettyServerFileSystemTerminalSession session = sessions.get(token.toString());

            if (network.isStart() && authFlag) {
                int requestID = session.getRequestID();
                AbstractRequest request = new WorkingDirectory(requestID, session.getToken());
                session.putRequest(requestID, request, null);

                network.write(request);

            } else {
                session.getOutput().connectionState(false);
            }
        } else {
            throw new RuntimeException("Запись о FileSystemTerminalToken отсутствует в файловом терминале.");
        }
    }


    @Override
    public void ls(FileSystemTerminalToken token) {
        if (sessions.containsKey(token.toString())) {
            NettyServerFileSystemTerminalSession session = sessions.get(token.toString());

            if (network.isStart() && authFlag) {
                int requestID = session.getRequestID();
                AbstractRequest request = new GetListOfFiles(requestID, session.getToken());
                session.putRequest(requestID, request, null);

                network.write(request);

            } else {
                session.getOutput().connectionState(false);
            }
        } else {
            throw new RuntimeException("Запись о FileSystemTerminalToken отсутствует в файловом терминале.");
        }
    }


    @Override
    public void put(FileSystemTerminalToken token, String sourcePath, String destinationPath) {
        if (sessions.containsKey(token.toString())) {
            NettyServerFileSystemTerminalSession session = sessions.get(token.toString());

            if (network.isStart() && authFlag) {
                int requestID = session.getRequestID();

                try {
                    AbstractRequest request = new PutFile(requestID, session.getToken(), Path.of(sourcePath), destinationPath);
                    session.putRequest(requestID, request, null);

                    network.write(request);

                } catch (Exception e) {
                    session.getOutput().error("Ошибка копирования файла");
                    e.printStackTrace();
                }
            } else {
                session.getOutput().connectionState(false);
            }
        } else {
            throw new RuntimeException("Запись о FileSystemTerminalToken отсутствует в файловом терминале.");
        }
    }


    @Override
    public void copy(FileSystemTerminalToken token, String sourcePath, FileSystemTerminalInput destinationTerminalInput, String destinationPath) {
        if (sessions.containsKey(token.toString())) {
            NettyServerFileSystemTerminalSession session = sessions.get(token.toString());

            if (network.isStart() && authFlag) {
                int requestID = session.getRequestID();
                AbstractRequest request = new GetFile(requestID, session.getToken(), sourcePath);
                session.putRequest(requestID, request, List.of(destinationTerminalInput, destinationPath));

                network.write(request);

            } else {
                session.getOutput().connectionState(false);
            }
        } else {
            throw new RuntimeException("Запись о FileSystemTerminalToken отсутствует в файловом терминале.");
        }

    }


    @Override
    public void makeDirectory(FileSystemTerminalToken token, String directoryName) {
        if (sessions.containsKey(token.toString())) {
            NettyServerFileSystemTerminalSession session = sessions.get(token.toString());

            if (network.isStart() && authFlag) {
                int requestID = session.getRequestID();
                AbstractRequest request = new MakeDirectory(requestID, session.getToken(), directoryName);
                session.putRequest(requestID, request, null);

                network.write(request);

            } else {
                session.getOutput().connectionState(false);
            }
        } else {
            throw new RuntimeException("Запись о FileSystemTerminalToken отсутствует в файловом терминале.");
        }
    }


    @Override
    public void remove(FileSystemTerminalToken token, String fileName) {
        if (sessions.containsKey(token.toString())) {
            NettyServerFileSystemTerminalSession session = sessions.get(token.toString());

            if (network.isStart() && authFlag) {
                int requestID = session.getRequestID();
                AbstractRequest request = new Remove(requestID, session.getToken(), fileName);
                session.putRequest(requestID, request, null);

                network.write(request);

            } else {
                session.getOutput().connectionState(false);
            }
        } else {
            throw new RuntimeException("Запись о FileSystemTerminalToken отсутствует в файловом терминале.");
        }
    }


    @Override
    public void rename(FileSystemTerminalToken token, String fileName, String newFileName) {
        if (sessions.containsKey(token.toString())) {
            NettyServerFileSystemTerminalSession session = sessions.get(token.toString());

            if (network.isStart() && authFlag) {
                int requestID = session.getRequestID();
                AbstractRequest request = new Rename(requestID, session.getToken(), fileName, newFileName);
                session.putRequest(requestID, request, null);

                network.write(request);

            } else {
                session.getOutput().connectionState(false);
            }
        } else {
            throw new RuntimeException("Запись о FileSystemTerminalToken отсутствует в файловом терминале.");
        }
    }


    /**
     * Метод получающий сообщения от сервера
     *
     * @param abstractResponse сообщение от сервера
     */
    private void serverResponseParser(AbstractResponse abstractResponse) {

        // Если токен не актуальный, то выход
        if (!sessions.containsKey(abstractResponse.getToken())) return;


        String token = abstractResponse.getToken();
        int requestID = abstractResponse.getRequestID();

        // Сессия, запрос на который пришёл ответ и параметры запроса
        NettyServerFileSystemTerminalSession session = sessions.get(token);
        AbstractRequest request = session.getRequest(requestID);
        List<Object> requestParameters = session.getRequestParameters(requestID);




        // === ConnectionState (состояние аутентификации) ===

        if (abstractResponse instanceof ConnectionState response) {

            authFlag = response.isConnection();

            if (requestParameters != null) {
                FileSystemTerminalInput input = (FileSystemTerminalInput) requestParameters.get(0);
                input.connectionState(authFlag);
            }

            session.removeRequest(requestID);
        }




        // === SessionSate (открытие/закрытие сессии) ===

        else if (abstractResponse instanceof SessionSate response) {

            if (requestParameters != null) {
                FileSystemTerminalToken terminalToken = (FileSystemTerminalToken) requestParameters.get(0);
                session.getOutput().sessionState(terminalToken, response.isOpen());
            }
            session.removeRequest(requestID);
        }




        // === SessionSate (открытие/закрытие сессии) ===

        else if (abstractResponse instanceof CurrentDirectory response) {

            session.getOutput().workingDirectory(response.getPath());
            session.removeRequest(requestID);
        }




        // === ListOfFiles (список файлов текущей директории) ===

        else if (abstractResponse instanceof ListOfFiles response) {

            List<FileInfo> fileInfoList = response.getFiles().stream()
                    .map(NettyServerFileInfo::get).toList();

            session.getOutput().listOfFiles(fileInfoList);
            session.removeRequest(requestID);
        }




        // === FileAdded (добавление нового файла или каталога) ===

        else if (abstractResponse instanceof FileAdded response) {

            session.getOutput().fileAdded(
                    Path.of(response.getDirectoryPath())
                        .resolve(response.getFileDescription().getName()).toString());
            session.removeRequest(requestID);
        }




        // === FileRemoved (удаление файла или каталога) ===

        else if (abstractResponse instanceof FileRemoved response) {

            session.getOutput().fileAdded(
                    Path.of(response.getDirectoryPath())
                            .resolve(response.getFileDescription().getName()).toString());
            session.removeRequest(requestID);
        }




        // === FileRenamed (переименование файла или каталога) ===

        else if (abstractResponse instanceof FileRenamed response) {

            session.getOutput().fileAdded(
                    Path.of(response.getDirectoryPath())
                            .resolve(response.getFileDescription().getName()).toString());
            session.removeRequest(requestID);
        }




        // === FileContent (получение файла со стороны сервера) ===

        else if (abstractResponse instanceof FileContent response) {


            FileSystemTerminalInput destinationTerminalInput = (FileSystemTerminalInput) requestParameters.get(0);
            String destinationPath = (String) requestParameters.get(1);

            try {
                File tmpFile = File.createTempFile("upload-", "");
                tmpFile.deleteOnExit();

                Files.write(tmpFile.toPath(), response.getData());

                destinationTerminalInput.putFile(tmpFile.toString(), destinationPath);

                session.removeRequest(requestID);

            } catch (IOException e) {
                e.printStackTrace();
                session.getOutput().error("Ошибка получения файла с сервера");
            }


        }

    }


    private void errorResponseParser(Error error) {


    }


    /**
     * Установить обработчик уведомление об ошибках.
     */
    public void setErrorListener(Consumer<String> listener) {
        network.setErrorConsumer(listener);
    }


    /**
     * Список доступных корневых директорий для данной файловой системы
     * Метод неопределённы для серверной файловой системы
     *
     * @return null
     */
    @Override
    public List<String> roots() {
        return null;
    }

}
