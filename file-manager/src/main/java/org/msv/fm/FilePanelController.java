package org.msv.fm;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;


/**
 * Контроллер панели управления локальной файловой системой на стороне пользователя.
 * (создан на основе примера Александра Фисунова)
 */
public class FilePanelController implements Initializable {


    @FXML
    public TableView<FileInfo> filesTable;
    public ComboBox<String> disksBox;
    public TextField pathField;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        TableColumn<FileInfo, String> fileTypeColumn = new TableColumn<>();
        fileTypeColumn.setCellValueFactory(param ->
                new SimpleStringProperty(param.getValue().getType().getName()));
        fileTypeColumn.setPrefWidth(24);


        TableColumn<FileInfo, String> fileNameColumn = new TableColumn<>("Имя");
        fileNameColumn.setCellValueFactory(param ->
                new SimpleStringProperty(param.getValue().getFilename()));
        fileNameColumn.setPrefWidth(240);


        TableColumn<FileInfo, Long> fileSizeColumn = new TableColumn<>("Размер");
        fileSizeColumn.setCellValueFactory(param ->
                new SimpleObjectProperty<>(param.getValue().getSize()));
        fileSizeColumn.setPrefWidth(120);
        fileSizeColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Long item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setText(null);
                    setStyle("");
                } else {
                    String text = "[DIR]";
                    if (item >= 0) {
                        text = String.format("%,d bytes", item);
                    }
                    setText(text);
                }

            }
        });


        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        TableColumn<FileInfo, String> fileDateColumn = new TableColumn<>("Дата изменения");
        fileDateColumn.setCellValueFactory(param ->
                new SimpleStringProperty(param.getValue().getLastModified().format(dtf)));
        fileDateColumn.setPrefWidth(120);


        filesTable.getColumns().addAll(fileTypeColumn, fileNameColumn, fileSizeColumn, fileDateColumn);
        filesTable.getSortOrder().add(fileTypeColumn);


        disksBox.getItems().clear();
        for (Path p : FileSystems.getDefault().getRootDirectories()) {
            disksBox.getItems().add(p.toString());
        }
        disksBox.getSelectionModel().select(0);


        filesTable.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getClickCount() == 2) {
                Path path = Paths.get(pathField.getText())
                        .resolve(filesTable.getSelectionModel().getSelectedItem().getFilename());
                if (Files.isDirectory(path)) {
                    updateList(path);
                }
            }
        });


        updateList(Paths.get(disksBox.getSelectionModel().getSelectedItem()));
//        updateList(Paths.get(System.getProperty("user.home")));
    }


    public void updateList(Path path) {

        try {

            pathField.setText(path.normalize().toAbsolutePath().toString());

            filesTable.getItems().clear();
            filesTable.getItems().addAll(Files.list(path).map(FileInfo::new).toList());
            filesTable.sort();

        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Ошибка обновления списка файлов", ButtonType.OK);
            alert.showAndWait();
        }

    }


    public void btnPathUpAction(ActionEvent actionEvent) {
        Path upperPath = Paths.get(pathField.getText()).getParent();
        if (upperPath != null) {
            updateList(upperPath);
        }
    }


    public void selectDiskAction(ActionEvent actionEvent) {
        ComboBox<String> element = (ComboBox<String>) actionEvent.getSource();
        updateList(Paths.get(element.getSelectionModel().getSelectedItem()));
    }


    public String getSelectedFileName() {
        if (!filesTable.isFocused()) {
            return null;
        }
        return filesTable.getSelectionModel().getSelectedItem().getFilename();
    }


    public String getCurrentPath() {
        return pathField.getText();
    }


}
