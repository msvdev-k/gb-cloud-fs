package org.msv.fm;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import org.msv.fm.net.Network;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ResourceBundle;


/**
 * Контроллер панели управления удалённой файловой системой на стороне сервера.
 * Реализована отправка файла от клиента к серверу.
 */
public class ServerFilePanelController implements Initializable {

    @FXML
    public ListView serverListView;


    private static final String HOST = "localhost";
    private static final int PORT = 8189;

    private Network network;
    private byte[] buf;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        try {
            network = new Network(HOST, PORT);
            buf = new byte[256];

            Thread readThread = new Thread(this::readLoop);
            readThread.setDaemon(true);
            readThread.start();

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }


    private void readLoop() {
        try {
            while (true) {
                String command = network.readMessage();

                if (command.equals("#list#")) {
                    Platform.runLater(() -> serverListView.getItems().clear());

                    int len = network.readInt();

                    for (int i = 0; i < len; i++) {
                        String file = network.readMessage();
                        Platform.runLater(() -> serverListView.getItems().add(file));
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("Connection lost");
        }
    }


    public void upload(Path path) throws IOException {

        network.getOut().writeUTF("#file#");
        network.getOut().writeUTF(path.getFileName().toString());

        File toSend = path.toFile();
        network.getOut().writeLong(toSend.length());

        try (FileInputStream fis = new FileInputStream(toSend)) {
            while (fis.available() > 0) {
                int read = fis.read(buf);
                network.getOut().write(buf, 0, read);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        network.getOut().flush();
    }



}
