package org.msv.fm;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.VBox;
import org.msv.fm.fs.FileSystemLocation;
import org.msv.fm.fs.jvm.JVMFileSystemTerminalOutput;
import org.msv.fm.net.NettyServerFileSystemTerminalOutput;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;


/**
 * Основной контроллер файлового менеджера.
 */
public class MainController implements Initializable {

    private static final String HOST = "localhost";
    private static final int PORT = 8189;

    @FXML
    public VBox leftFilesTable;
    public VBox rightFilesTable;


    FilePanelController leftFPL;
    FilePanelController rightFPL;


    // Терминал файловой системы доступной JVM
    private final JVMFileSystemTerminalOutput JVMTerminal = new JVMFileSystemTerminalOutput();

    // Терминал удалённой файловой системы сервера на Netty
    private final NettyServerFileSystemTerminalOutput NSTerminal = new NettyServerFileSystemTerminalOutput(HOST, PORT);


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        NSTerminal.setErrorListener(this::alertError);

        ///////////////////////////////////////////////////
        // Настройка списка доступных файловых систем (локаций)
        ///////////////////////////////////////////////////

        List<FileSystemLocation> locations = new ArrayList<>();

        List<String> jvmRoots = JVMTerminal.roots();
        jvmRoots.stream()
                .map(p -> new FileSystemLocation(p, p, JVMTerminal))
                .forEach(locations::add);

        locations.add(new FileSystemLocation("Cloud", "~", NSTerminal));

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


    /**
     * Вывод диалогового окна сообщения об ошибке.
     * @param errorMessage сообщение об ошибке
     */
    private void alertError(String errorMessage) {
        Platform.runLater(()->{
            Alert alert = new Alert(Alert.AlertType.ERROR, errorMessage, ButtonType.OK);
            alert.showAndWait();
        });
    }


    /**
     * Действие завершения работы приложения.
     */
    public void btmExitAction(ActionEvent actionEvent) {
        Platform.exit();
    }



    /**
     * Действие копирования файла между терминалами.
     */
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
    }


    /**
     * Действие подключения к удалённому серверу.
     */
    public void btmServerConnectionAction(ActionEvent actionEvent) {
        System.out.println("Метод btmServerConnectionAction() - " + Thread.currentThread().getName());
        NSTerminal.connect("user1", "pass1", null);
    }


    /**
     * Действие на разрыв соединения с удалённым сервером.
     */
    public void btmServerCloseConnectionAction(ActionEvent actionEvent) {
        NSTerminal.closeConnection();
    }


    /**
     * Действие на создание новой директории.
     */
    public void makeDirectoryBtnAction(ActionEvent actionEvent) {

        FilePanelController leftPC = (FilePanelController) leftFilesTable.getProperties().get("ctrl");
        FilePanelController rightPC = (FilePanelController) rightFilesTable.getProperties().get("ctrl");

        FilePanelController selectedController;

        if (leftPC.isSelectedFile()) {
            selectedController = leftPC;

        } else if (rightPC.isSelectedFile()) {
            selectedController = rightPC;

        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING,
                    "Перед создание каталога необходимо выбрать файловую панель",
                    ButtonType.OK);
            alert.showAndWait();
            return;
        }

        TextInputDialog inputDialog = new TextInputDialog();
        inputDialog.setTitle("Добавление нового каталога");
        inputDialog.setHeaderText("Введите название создаваемого каталога");

        Optional<String> result = inputDialog.showAndWait();
        if (result.isPresent()) {
             String dirName = result.get();
             selectedController.makeDirectory(dirName);
        }
    }


    /**
     * Действие на удаление файла или каталога.
     */
    public void removeBtnAction(ActionEvent actionEvent) {

        FilePanelController leftPC = (FilePanelController) leftFilesTable.getProperties().get("ctrl");
        FilePanelController rightPC = (FilePanelController) rightFilesTable.getProperties().get("ctrl");

        FilePanelController selectedController;

        if (leftPC.isSelectedFile()) {
            selectedController = leftPC;

        } else if (rightPC.isSelectedFile()) {
            selectedController = rightPC;

        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING,
                    "Перед создание каталога необходимо выбрать файловую панель",
                    ButtonType.OK);
            alert.showAndWait();
            return;
        }

        Alert dialog = new Alert(Alert.AlertType.WARNING,null,  ButtonType.OK, ButtonType.CANCEL);
        dialog.setTitle("Подтверждение удаления файла");
        dialog.setHeaderText("Вы действительно ходите удалить " + selectedController.getSelectedFileName() + " ?");
        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            selectedController.removeSelected();
        }
    }


    /**
     * Действие на обновление списка файлов.
     */
    public void updateBtnAction(ActionEvent actionEvent) {
        FilePanelController leftPC = (FilePanelController) leftFilesTable.getProperties().get("ctrl");
        FilePanelController rightPC = (FilePanelController) rightFilesTable.getProperties().get("ctrl");

        leftPC.update();
        rightPC.update();
    }


    /**
     * Действие на переименование файла или каталога.
     */
    public void renameBtnAction(ActionEvent actionEvent) {

        FilePanelController leftPC = (FilePanelController) leftFilesTable.getProperties().get("ctrl");
        FilePanelController rightPC = (FilePanelController) rightFilesTable.getProperties().get("ctrl");

        FilePanelController selectedController;

        if (leftPC.isSelectedFile()) {
            selectedController = leftPC;

        } else if (rightPC.isSelectedFile()) {
            selectedController = rightPC;

        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING,
                    "Перед создание каталога необходимо выбрать файловую панель",
                    ButtonType.OK);
            alert.showAndWait();
            return;
        }

        TextInputDialog inputDialog = new TextInputDialog(selectedController.getSelectedFileName());
        inputDialog.setTitle("Переименование файла");
        inputDialog.setHeaderText("Введите новое название файла");

        Optional<String> result = inputDialog.showAndWait();
        if (result.isPresent()) {
            String newName = result.get();
            selectedController.renameSelected(newName);
        }
    }

}
