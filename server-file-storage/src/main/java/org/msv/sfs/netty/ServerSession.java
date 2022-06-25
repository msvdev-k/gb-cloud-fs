package org.msv.sfs.netty;

import org.msv.sm.RemoteFileDescription;
import org.msv.sm.request.AbstractRequest;
import org.msv.sm.request.GetFile;
import org.msv.sm.response.FileContent;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Stream;


/**
 * Описание текущей сессии
 */
class ServerSession {

    // Токен сессии
    private final String token;

    // Корневой каталог для текущей сессии
    private final Path root;

    // Текущий каталог
    private Path currentDirectory;

    // Текущее сообщение, которое необходимо обработать
    private AbstractRequest request;


    public ServerSession(String token, Path root) {
        this.token = token;
        this.root = root;
        this.currentDirectory = root;
        this.request = null;
    }


    /**
     * Получить абсолютный путь к файловому объекту с учётом текущей директории
     *
     * @param path путь или имя
     * @return абсолютный путь, либо null в случае указания некорректного пути
     */
    private Path getAbsolutePath(String path) {

        Path absolutePath;

        if (path.startsWith("~")) {
            path = path.replaceFirst("~", root.toString().replace("\\", "/"));
            absolutePath = Path.of(path).normalize();

        } else {
            absolutePath = currentDirectory.resolve(path).normalize();
        }

        if (absolutePath.startsWith(root)) {
            return absolutePath;
        }

        return null;
    }


    /**
     * Получить текущий каталог.
     * Путь указывается относительно корневого каталога пользователя.
     * Например: ~\Dir1\Dir12 или ~
     * <p>
     * ~ - обозначение корневого каталога.
     */
    public String getCurrentDirectory() {

        String path = "~";

        if (root.getNameCount() < currentDirectory.getNameCount()) {
            path = "~" + FileSystems.getDefault().getSeparator() +
                    currentDirectory.subpath(root.getNameCount(), currentDirectory.getNameCount());
        }

        return path;
    }


    /**
     * Установить обрабатываемое сообщение.
     */
    public void setRequest(AbstractRequest request) {
        this.request = request;
    }


    /**
     * Получить обрабатываемое сообщение.
     */
    public AbstractRequest getRequest() {
        return request;
    }


    /**
     * Изменить текущую директорию.
     *
     * @param directoryName название новой директории.
     * @return true - директория изменена, false - ошибка изменения (путь остался прежним)
     */
    public boolean changeDirectory(String directoryName) {

        Path newDir = getAbsolutePath(directoryName);

        if (newDir != null &&
                Files.isDirectory(newDir) &&
                Files.exists(newDir)) {

            currentDirectory = newDir;
            return true;
        }

        return false;
    }


    /**
     * Получить список файлов текущей директории.
     *
     * @return список файлов; пустой список если файлов нет; null если список не удалось получить.
     */
    public List<RemoteFileDescription> getListOfFiles() {

        if (Files.exists(currentDirectory)) {

            List<RemoteFileDescription> files;

            try (Stream<Path> stream = Files.list(currentDirectory)) {
                files = stream.map(ServerFileInfo::get).toList();

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

            return files;
        }

        return null;
    }


    /**
     * Записать данные в файл текущей директории.
     *
     * @param description описание файла в котором нужно сохранить данные.
     * @param data        сохраняемые данные.
     * @return описание сохранённого файла, либо null в случае ошибки.
     */
    public RemoteFileDescription putFile(RemoteFileDescription description, byte[] data) {

        Path fileName = currentDirectory.resolve(description.getName()).normalize();

        if (fileName.startsWith(root) &&
                !Files.isDirectory(fileName) &&
                !Files.exists(fileName)) {

            RemoteFileDescription fileDescription;

            try {
                Files.write(fileName, data);

                Files.setLastModifiedTime(fileName, FileTime.from(description.getLastModified()
                        .toInstant(ZoneOffset.ofHours(0))));

                fileDescription = ServerFileInfo.get(fileName);

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

            return fileDescription;
        }

        return null;
    }


    /**
     * Получить контент запрашиваемого файла.
     *
     * @param request запрос на получение файла
     * @return содержание запрашиваемого файла, либо null в случае возникновении ошибки
     */
    public FileContent getFile(GetFile request) {

        if (!request.getToken().equals(token)) {
            return null;
        }

        Path file = getAbsolutePath(request.getFileName());

        if (file != null &&
                !Files.isDirectory(file) &&
                Files.exists(file)) {

            FileContent fileContent;

            try {
                RemoteFileDescription fileDescription = ServerFileInfo.get(file);
                byte[] data = Files.readAllBytes(file);

                fileContent = new FileContent(request, fileDescription, data);

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

            return fileContent;
        }

        return null;
    }


    /**
     * Добавить новую директорию.
     *
     * @param newDirectoryName название новой директории
     * @return описание созданной директории, либо null в случае ошибки.
     */
    public RemoteFileDescription makeDirectory(String newDirectoryName) {

        Path newDirectory = getAbsolutePath(newDirectoryName);

        if (newDirectory != null && Files.notExists(newDirectory)) {

            RemoteFileDescription description;

            try {
                Files.createDirectory(newDirectory);
                description = ServerFileInfo.get(newDirectory);

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

            return description;
        }

        return null;
    }


    /**
     * Удалить существующий файл или директорию.
     * Директория удаляется в случае отсутствия дочерних файловых объектов.
     *
     * @param fileName название удаляемого файла или директории
     * @return описание удалённого файлового объекта, либо null в случае ошибки.
     */
    public RemoteFileDescription remove(String fileName) {

        Path file = getAbsolutePath(fileName);

        if (file != null) {

            RemoteFileDescription description;

            try {
                description = ServerFileInfo.get(file);
                Files.delete(file);

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

            return description;
        }

        return null;
    }


    /**
     * Переименовать файл или директорию.
     *
     * @param name    название переименовываемого файла или директории
     * @param newName новое название файла или директории
     * @return описание переименованного файлового объекта, либо null в случае ошибки.
     */
    public RemoteFileDescription rename(String name, String newName) {

        Path path = getAbsolutePath(name);
        Path newPath = getAbsolutePath(newName);

        if (path != null && newPath != null && path.getNameCount() == newPath.getNameCount()) {

            // Пути корректные и находятся в одной и той же директории

            RemoteFileDescription description;

            try {
                Files.move(path, newPath);
                description = ServerFileInfo.get(newPath);

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

            return description;
        }

        return null;
    }


    public String getToken() {
        return token;
    }
}
