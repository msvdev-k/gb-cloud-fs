package org.msv.fm;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


/**
 * Основной контроллер файлового менеджера.
 */
public class MainController {

    @FXML
    public VBox filesTable;
    public VBox filesTableServer;


    public void btmExitAction(ActionEvent actionEvent) {
        Platform.exit();
    }



    public void copyBtnAction(ActionEvent actionEvent) {
        FilePanelController filesPC = (FilePanelController) filesTable.getProperties().get("ctrl");
        ServerFilePanelController serverPC = (ServerFilePanelController) filesTableServer.getProperties().get("ctrl");

//        FilePanelController serverPC = (FilePanelController) filesTableServer.getProperties().get("ctrl");

        if (filesPC.getSelectedFileName() == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING,
                    "Перед копирование необходимо выбрать файл",
                    ButtonType.OK);
            alert.showAndWait();
            return;
        }

//        FilePanelController srcPC, dstPC;
//
//        if (filesPC.getSelectedFileName() != null) {
//            srcPC = filesPC;
//            dstPC = serverPC;
//        } else if (serverPC.getSelectedFileName() != null) {
//            srcPC = serverPC;
//            dstPC = filesPC;
//        } else {
//            Alert alert = new Alert(Alert.AlertType.WARNING,
//                    "Перед копирование необходимо выбрать файл",
//                    ButtonType.OK);
//            alert.showAndWait();
//            return;
//        }

        Path srcPath = Paths.get(filesPC.getCurrentPath(), filesPC.getSelectedFileName());
//        Path dstPath = Paths.get(dstPC.getCurrentPath()).resolve(srcPath.getFileName().toString());


        try {
            serverPC.upload(srcPath);

//            Files.copy(srcPath, dstPath);
//            dstPC.updateList(Paths.get(dstPC.getCurrentPath()));
//
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING,
                    "Не удалось скопировать указанный файл",
                    ButtonType.OK);
            alert.showAndWait();
        }

    }
}