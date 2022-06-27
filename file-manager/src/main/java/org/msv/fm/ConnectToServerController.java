//package org.msv.fm;
//
//import javafx.fxml.FXML;
//import javafx.fxml.Initializable;
//import javafx.scene.control.ListView;
//import org.msv.fm.net.Network;
//
//import java.net.URL;
//import java.util.ResourceBundle;
//
//
///**
// * Контроллер панели управления удалённой файловой системой на стороне сервера.
// * Реализована отправка файла от клиента к серверу.
// */
//public class ConnectToServerController implements Initializable {
//
//    @FXML
//    public ListView serverListView;
//
//
//    private static final String HOST = "localhost";
//    private static final int PORT = 8189;
//
//    private Network network;
//    private byte[] buf;
//
//
//    @Override
//    public void initialize(URL url, ResourceBundle resourceBundle) {
//
//        try {
//            network = new Network(HOST, PORT);
//            buf = new byte[256];
//
//
//        } catch (Exception e) {
//            System.err.println(e.getMessage());
//        }
//    }
//
//
//
//
//}
