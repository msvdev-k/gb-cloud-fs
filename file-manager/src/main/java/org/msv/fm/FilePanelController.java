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
     * Инициализация контроллера.
     */
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
     * Очистить представление контроллера.
     */
    private void clear() {
        filesTable.getItems().clear();
        pathField.clear();
    }


    @Override
    public void connectionState(boolean state) {

        if (state) {
            selectDiskAction(null);

        } else {
            clear();
            error("Необходимо подключиться к удалённому серверу");
        }
    }


    @Override
    public void sessionState(FileSystemTerminalToken token, boolean state) {
        if (state && this.token == null) {
            this.token = token;
            terminal.cd(token, getDisk().getRoot());
        }
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
     * Обновить список доступных локаций файловых систем.
     *
     * @param list список доступных локаций
     */
    public void updateDiskBox(List<FileSystemLocation> list) {
        disksBox.getItems().clear();
        disksBox.getItems().addAll(list);
    }


    /**
     * Установить текущую локацию файловых систем.
     *
     * @param disk файловая локация
     */
    public void setDisk(FileSystemLocation disk) {
        disksBox.getSelectionModel().select(disk);
    }


    /**
     * Получить текущую файловую локацию.
     */
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

        clear();

        if (this.terminal != null && this.token != null) {
            this.terminal.stopSession(token);
        }

        this.terminal = location.getTerminal();
        this.token = null;
        terminal.startSession(this, location);
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
     * В контроллере выбран файл.
     *
     * @return true - выбран файл, false - файл не выбран.
     */
    public boolean isSelectedFile() {
        return filesTable.isFocused() && filesTable.getSelectionModel().getSelectedItem() != null;
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


    @Override
    public void workingDirectory(String path) {
        this.currentPath = Path.of(path);
        pathField.setText(path);
        terminal.ls(token);
    }


    @Override
    public void listOfFiles(List<FileInfo> fileInfoList) {
        filesTable.getItems().clear();
        filesTable.getItems().addAll(fileInfoList);
        filesTable.sort();
    }


    @Override
    public void fileAdded(String path) {
        if (currentPath.equals(Path.of(path).getParent())) {
            terminal.ls(token);
        }
    }


    @Override
    public void fileRenamed(String path) {
        if (currentPath.equals(Path.of(path).getParent())) {
            terminal.ls(token);
        }
    }


    @Override
    public void fileRemoved(String path) {
        if (currentPath.equals(Path.of(path).getParent())) {
            terminal.ls(token);
        }
    }


    @Override
    public void error(String errorMessage) {
        Alert alert = new Alert(Alert.AlertType.ERROR, errorMessage, ButtonType.OK);
        alert.showAndWait();
    }


    @Override
    public void info(String infoMessage) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, infoMessage, ButtonType.OK);
        alert.showAndWait();
    }


    @Override
    public void putFile(String sourcePath, String destinationPath) {
        terminal.put(token, sourcePath, destinationPath);
    }


    /**
     * Скопировать файл из текущего контроллера в контроллер приёмник.
     *
     * @param dst контроллер, принимающий файл
     */
    public void copy(FileSystemTerminalInput dst) {
        String fileName = filesTable.getSelectionModel().getSelectedItem().getName();
        terminal.copy(token, fileName, dst, fileName);
    }


    /**
     * Создать новый каталог в текущей директории.
     * @param directoryName название создаваемого каталога
     */
    public void makeDirectory(String directoryName) {
        terminal.makeDirectory(token, directoryName);
    }


    /**
     * Обновить список файлов.
     */
    public void update() {
        terminal.ls(token);
    }


    /**
     * Удалить выбранный файл или каталог.
     */
    public void removeSelected() {
        String fileName = filesTable.getSelectionModel().getSelectedItem().getName();
        terminal.remove(token, fileName);
    }


    /**
     * Переименовать выделенный файл или каталог.
     * @param newName новое название файла или каталога
     */
    public void renameSelected(String newName) {
        String fileName = filesTable.getSelectionModel().getSelectedItem().getName();
        if (!fileName.equals(newName)) {
            terminal.rename(token, fileName, newName);
        }
    }

}
