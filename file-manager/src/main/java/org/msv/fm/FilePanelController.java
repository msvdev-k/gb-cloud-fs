package org.msv.fm;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import org.msv.fm.fs.*;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;


/**
 * Контроллер панели управления файловой системой
 */
public class FilePanelController implements Initializable, FileSystemTerminalInput {

    @FXML
    public TableView<FileInfo> filesTable;
    public ComboBox<FileSystemLocation> disksBox;
    public TextField pathField;


    // Терминал файловой системы
    private FileSystemTerminalOutput terminal;

    // Токен текущей сессии терминала
    private FileSystemTerminalToken token;

    // Путь к текущему каталогу
    private Path currentPath;


    /**
     * Привязка терминала файловой системы к контроллеру
     *
     * @param terminal терминал файловой системы
     */
    public void setFileSystemTerminal(FileSystemTerminalOutput terminal) {
        if (this.terminal != null) {
            this.terminal.stopSession(token);
        }

        this.terminal = terminal;
        this.token = terminal.startSession(this);
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        ///////////////////////////////////////////////////////////
        // Настройка таблицы файлов
        ///////////////////////////////////////////////////////////


        // Столбец: тип файлового объекта
        TableColumn<FileInfo, String> fileTypeColumn = new TableColumn<>();
        fileTypeColumn.setCellValueFactory(param ->
                new SimpleStringProperty(param.getValue().getType().getName()));
        fileTypeColumn.setPrefWidth(24);


        // Столбец: имя файлового объекта
        TableColumn<FileInfo, String> fileNameColumn = new TableColumn<>("Имя");
        fileNameColumn.setCellValueFactory(param ->
                new SimpleStringProperty(param.getValue().getName()));
        fileNameColumn.setPrefWidth(240);


        // Столбец: размер файлового объекта
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


        // Столбец: дата изменения файлового объекта
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        TableColumn<FileInfo, String> fileDateColumn = new TableColumn<>("Дата изменения");
        fileDateColumn.setCellValueFactory(param ->
                new SimpleStringProperty(param.getValue().getLastModified().format(dtf)));
        fileDateColumn.setPrefWidth(120);


        // Порядок столбцов и сортировка: Тип* | Название | Размер | Дата изменения
        filesTable.getColumns().addAll(fileTypeColumn, fileNameColumn, fileSizeColumn, fileDateColumn);
        filesTable.getSortOrder().add(fileTypeColumn);


        // Обработка кликов мыши
        filesTable.setOnMouseClicked(this::onMouseClicked);

    }


    /**
     * Обработка пользовательского клика мышкой
     *
     * @param mouseEvent событие от мышки
     */
    private void onMouseClicked(MouseEvent mouseEvent) {

        if (mouseEvent.getClickCount() == 2) {

            FileInfo fileInfo = filesTable.getSelectionModel().getSelectedItem();

            if (fileInfo == null) return;

            if (fileInfo.getType() == FileInfo.FileType.DIRECTORY) {
                terminal.cd(token, fileInfo.getName());

            } else if (fileInfo.getType() == FileInfo.FileType.FILE) {
                // TODO: добавить обработку файлов
            }
        }
    }


    /**
     * Обновить список доступных локаций файловых систем
     *
     * @param list список доступных локаций
     */
    public void updateDiskBox(List<FileSystemLocation> list) {
        disksBox.getItems().clear();
        disksBox.getItems().addAll(list);
    }


    public void setDisk(FileSystemLocation disk) {
        disksBox.getSelectionModel().select(disk);
    }


    public FileSystemLocation getDisk() {
        return disksBox.getSelectionModel().getSelectedItem();
    }


    /**
     * Пользователь выбрал другую файловую локацию
     *
     * @param actionEvent событие
     */
    public void selectDiskAction(ActionEvent actionEvent) {
        FileSystemLocation location = disksBox.getSelectionModel().getSelectedItem();
        setFileSystemTerminal(location.getTerminal());
        terminal.cd(token, location.getRoot());
    }


    /**
     * Пользователь активировал переход на директорию выше
     *
     * @param actionEvent событие нажатия кнопки
     */
    public void btnPathUpAction(ActionEvent actionEvent) {
        terminal.cd(token, "..");
    }


    /**
     * Выбранный пользователем файл
     *
     * @return имя файла, либо null
     */
    public String getSelectedFileName() {
        if (!filesTable.isFocused()) {
            return null;
        }
        return filesTable.getSelectionModel().getSelectedItem().getName();
    }


    /**
     * Путь к текущей директории
     *
     * @return строка с описанием пути
     */
    public Path getCurrentPath() {
        return Paths.get(currentPath.toUri());
    }


    /**
     * Установить новый абсолютный путь к текущему каталогу
     *
     * @param path путь
     */
    @Override
    public void path(Path path) {
        this.currentPath = path;
        pathField.setText(path.toString());
        terminal.ls(token, path.toString());
    }


    /**
     * Установить текущую корневую директорию (для файловых систем с несколькими корневыми директориями)
     * Метод не реализован
     *
     * @param root корневая директория
     */
    @Override
    public void root(Path root) {

    }


    /**
     * Обновить список файлов
     *
     * @param fileInfoList список файлов
     * @param path         абсолютный путь к каталогу из которого собирается список
     */
    @Override
    public void fileList(List<FileInfo> fileInfoList, Path path) {
        if (currentPath.equals(path)) {
            filesTable.getItems().clear();
            filesTable.getItems().addAll(fileInfoList);
            filesTable.sort();
        }
    }


    /**
     * Сообщение об ошибке
     *
     * @param errorMessage описание ошибки
     */
    @Override
    public void error(String errorMessage) {
        Alert alert = new Alert(Alert.AlertType.ERROR, errorMessage, ButtonType.OK);
        alert.showAndWait();
    }


    /**
     * Обычное сообщение
     *
     * @param infoMessage сообщение от файловой системы
     */
    @Override
    public void info(String infoMessage) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, infoMessage, ButtonType.OK);
        alert.showAndWait();
    }
}
