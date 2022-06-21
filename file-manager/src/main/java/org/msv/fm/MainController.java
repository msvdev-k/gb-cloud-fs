package org.msv.fm;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;
import org.msv.fm.fs.FileSystemLocation;
import org.msv.fm.fs.jvm.JVMFileSystemTerminalOutput;
//import org.msv.fm.net.NettyServerFileSystemTerminalOutput;

import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;


/**
 * Основной контроллер файлового менеджера.
 */
public class MainController implements Initializable {

    private static final String HOST = "localhost";
    private static final int PORT = 8189;

    private static final String DEFAULT_ROOT = FileSystems.getDefault().getRootDirectories().iterator().next().toString();
    private static final String CLOUD_ROOT = "Cloud:" + FileSystems.getDefault().getSeparator();

    @FXML
    public VBox leftFilesTable;
    public VBox rightFilesTable;


    FilePanelController leftFPL;
    FilePanelController rightFPL;


    // Терминал файловой системы доступной JVM
    private final JVMFileSystemTerminalOutput JVMTerminal = new JVMFileSystemTerminalOutput();

    // Терминал удалённой файловой системы сервера на Netty
    //private final NettyServerFileSystemTerminalOutput NSTerminal = new NettyServerFileSystemTerminalOutput(HOST, PORT);


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        ///////////////////////////////////////////////////
        // Настройка списка доступных файловых систем (локаций)
        ///////////////////////////////////////////////////

        List<FileSystemLocation> locations = new ArrayList<>();

        List<String> jvmRoots = JVMTerminal.roots();
        jvmRoots.stream()
                .map(p -> new FileSystemLocation(p, p, JVMTerminal))
                .forEach(locations::add);

        //locations.add(new FileSystemLocation("Cloud", "~", NSTerminal));


        ///////////////////////////////////////////////////
        // Настройка файловых панелей
        ///////////////////////////////////////////////////

        leftFPL = (FilePanelController) leftFilesTable.getProperties().get("ctrl");
        rightFPL = (FilePanelController) rightFilesTable.getProperties().get("ctrl");

        // Список корневых директорий
        leftFPL.updateDiskBox(locations);
        rightFPL.updateDiskBox(locations);

        leftFPL.setDisk(locations.get(0));
        rightFPL.setDisk(locations.get(0));

        leftFPL.selectDiskAction(null);
        rightFPL.selectDiskAction(null);

    }




    private void alertError(String errorMessage) {
        Alert alert = new Alert(Alert.AlertType.ERROR, errorMessage, ButtonType.OK);
        alert.showAndWait();
    }




    public void btmExitAction(ActionEvent actionEvent) {
        Platform.exit();
    }



    public void copyBtnAction(ActionEvent actionEvent) {

        FilePanelController leftPC = (FilePanelController) leftFilesTable.getProperties().get("ctrl");
        FilePanelController rightPC = (FilePanelController) rightFilesTable.getProperties().get("ctrl");

        // Сортировка источника и приёмника
        FilePanelController srcPC, dstPC;

        if (leftPC.isSelectedFile()) {
            srcPC = leftPC;
            dstPC = rightPC;

        } else if (rightPC.isSelectedFile()) {
            srcPC = rightPC;
            dstPC = leftPC;

        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING,
                    "Перед копирование необходимо выбрать файл",
                    ButtonType.OK);
            alert.showAndWait();
            return;
        }

        // Копирование от источника к приёмнику
        srcPC.copy(dstPC);

//        if (srcPC.getTerminal() == JVMTerminal) {
//            // Источник - локальная файловая система
//
//            // Приёмник - любая другая файловая система
//            Path src = srcPC.getCurrentPath().resolve(srcPC.getSelectedFileName());
//            dstPC.copy(src);
//        }

//        else if (srcPC.getTerminal() == NSTerminal) {
//            // Источник - удалённая файловая система сервера на Netty
//
//            if (dstPC.getTerminal() == JVMTerminal) {
//                // Приёмник - локальная файловая система
//
//                srcPC.copyTo(dstPC);
//            }
//            else {
//                Alert alert = new Alert(Alert.AlertType.WARNING,
//                        "Между удалёнными файловыми системами копирование файлов запрещено!",
//                        ButtonType.OK);
//                alert.showAndWait();
//            }
//        }


    }
}