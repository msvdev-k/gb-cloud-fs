package org.msv.fm.net;

import org.msv.fm.fs.FileSystemTerminalInput;
import org.msv.sm.request.AbstractRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Описание сессии терминала
 */
public class NettyServerFileSystemTerminalSession {

    // Счётчик запросов (для получения автоинкрементного идентификатора)
    private int requestCounter;

    // Токен сессии
    private final String token;

    // Пул отправленных на сервер запросов. Ключ - ID запроса, значение - экземпляр класса запроса
    private final Map<Integer, AbstractRequest> requestPool = new HashMap<>();

    // Параметры ассоциированные с запросом. Ключ - ID запроса, значение - список ассоциированных объектов
    private final Map<Integer, List<Object>> requestParameters = new HashMap<>();

    // Вывод файлового терминала
    private FileSystemTerminalInput output;


    public NettyServerFileSystemTerminalSession(String token) {
        this.token = token;
        requestCounter = 0;
    }


    /**
     * Добавить запрос в общий пул отправляемых на сервер запросов.
     *
     * @param ID         идентификатор запроса (получить с помощью метода getRequestID())
     * @param parameters список ассоциированных с запросом объектов (дополнительных параметров),
     *                   либо null если параметров нет
     * @param request    отправляемый на сервер запрос
     */
    public void putRequest(int ID, AbstractRequest request, List<Object> parameters) {
        requestPool.put(ID, request);
        if (parameters != null && !parameters.isEmpty()) {
            requestParameters.put(ID, parameters);
        }
    }


    /**
     * Получить запрос из общего пула отправленных на сервер запросов
     *
     * @param ID идентификатор отправленного запроса
     * @return отправленные на сервер запрос, либо null в случае отсутствия такового
     */
    public AbstractRequest getRequest(int ID) {
        if (requestPool.containsKey(ID)) {
            return requestPool.get(ID);
        }
        return null;
    }


    /**
     * Получить список объектов (параметров) ассоциированных с отправленным на сервер запросом
     *
     * @param ID идентификатор отправленного запроса
     * @return список ассоциированных объектов, либо null если списка нет
     */
    public List<Object> getRequestParameters(int ID) {
        if (requestParameters.containsKey(ID)) {
            return requestParameters.get(ID);
        }
        return null;
    }


    /**
     * Удалить запрос из общего пула отправляемых на сервер запросов.
     * В случае получения ожидаемого ответа запрос необходимо удалить.
     * При удалении запроса, список ассоциированных объектов также удаляется.
     *
     * @param ID идентификатор запроса
     */
    public void removeRequest(int ID) {
        requestPool.remove(ID);
        requestParameters.remove(ID);
    }


    /**
     * Идентификатор нового запроса.
     */
    public int getRequestID() {
        requestCounter++;
        return requestCounter;
    }


    /**
     * Токен сессии.
     */
    public String getToken() {
        return token;
    }


    /**
     * Установить вывод файлового терминала, ассоциированного с сессией.
     */
    public void setOutput(FileSystemTerminalInput output) {
        this.output = output;
    }


    /**
     * Получить вывод файлового терминала, ассоциированного с сессией.
     */
    public FileSystemTerminalInput getOutput() {
        return output;
    }


}
