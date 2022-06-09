package org.msv.fm.fs;

/**
 * Класс, описывающий основную информацию о локации файловой системы
 */
public class FileSystemLocation {

    private final String name;
    private final String rootPath;
    private final FileSystemTerminalOutput terminal;

    public FileSystemLocation(String name, String root, FileSystemTerminalOutput terminal) {
        this.name = name;
        this.rootPath = root;
        this.terminal = terminal;
    }

    public String getName() {
        return name;
    }

    public String getRoot() {
        return rootPath;
    }

    public FileSystemTerminalOutput getTerminal() {
        return terminal;
    }

    @Override
    public String toString() {
        return name;
    }
}
