package dgusev.apps.mass_email_sender.spammer.dataModel.dao;

import jdb.exceptions.DBConnectionException;
import jdb.model.applied.dao.DBConfigCommonDAO;
import jdb.processing.sql.execution.SqlExecutor;
import dgusev.apps.mass_email_sender.spammer.Defaults;
import dgusev.apps.mass_email_sender.spammer.dataModel.dto.DeliveryDTO;
import dgusev.apps.mass_email_sender.spammer.dataModel.dto.DeliveryFileDTO;
import dgusev.apps.mass_email_sender.spammer.dataModel.dto.RecipientTypeDTO;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;

/**
 * Класс для работы с таблицей рассылок и прикрепленных к ним файлов.
 *
 * @author Gusev Dmitry (019gus)
 * @version 4.0 (DATE: 21.12.2010)
 */

public class DeliveriesDAO extends DBConfigCommonDAO {
    /**
     * Логгер класса.
     */
    private Logger logger = Logger.getLogger(Defaults.LOGGER_NAME);

    /**
     * Конструктор. Поля инициализируются значениями по умолчанию.
     */
    public DeliveriesDAO() throws ConfigurationException {
        super(Defaults.LOGGER_NAME, Defaults.DBCONFIG_FILE);
    }

    /**
     * Конструктор. Значением по умолчанию инициализируется только наименование логгера, конфиг-файл для соединения
     * с СУБД указывается в качестве параметра конструктора.
     *
     * @param dbConfigFileName String конфиг-файл для соединения с СУБД.
     */
    public DeliveriesDAO(String dbConfigFileName) throws ConfigurationException {
        super(Defaults.LOGGER_NAME, dbConfigFileName);
    }

    /**
     * Метод находит и возвращает записи из таблицы рассылок (dbo.deliveries). Если метод ничего не нашел - метод
     * возвращает значение NULL. Метод не находит файлы, прикрепленные к рассылкам (из таблицы файлов - dbo.deliveriesFiles),
     * файлы для рассылки находит только метод поиска одной рассылки. Также метод не возвращает в результирующих объектах-членах
     * списка ArrayList типы получателей для каждой рассылки (из таблицы типов получателей - dbo.recipientsTypes).
     *
     * @param type DeliveryType тип рассылок, которые должны быть найдены. Если значение не указано (NULL), то метод вернет
     *             ВСЕ записи таблицы рассылок.
     * @return ArrayList[DeliveryDTO] список рассылок или NULL.
     */
    public ArrayList<DeliveryDTO> findAll(Defaults.DeliveryType type) throws IllegalAccessException, ClassNotFoundException, InstantiationException {
        logger.debug("DeliveriesDAO: findAll()");
        Connection conn = null;
        ResultSet rs = null;
        ArrayList<DeliveryDTO> list = null;
        String sql = "select id, subject, text, status, type, errorText, initiator, timestamp " +
                "from dbo.deliveries";
        // Добавляем тип рассылки к запросу
        if ((type != null) && (!Defaults.DeliveryType.DELIVERY_TYPE_UNKNOWN.equals(type))) {
            logger.debug("Adding delivery type [" + type + "] to query.");
            sql += " where type = " + type.getIntValue();
        }
        // Добавляем сортировку рассылок (после возможного добавления типа рассылки)
        sql += " order by timestamp desc";
        // Отладочное сообщение
        logger.debug("Generated sql: " + sql);
        try {
            conn = this.getConnection();
            rs = SqlExecutor.executeSelectQuery(conn, sql);
            // Что-то нашли
            if (rs.next()) {
                logger.debug("Result set is not empty! Processing.");
                list = new ArrayList<DeliveryDTO>();
                do {
                    DeliveryDTO delivery = new DeliveryDTO();
                    delivery.setId(rs.getInt("id"));
                    delivery.setSubject(rs.getString("subject"));
                    delivery.setText(rs.getString("text"));
                    delivery.setStatus(Defaults.DeliveryStatus.findByIntValue(rs.getInt("status")));
                    delivery.setType(Defaults.DeliveryType.findByIntValue(rs.getInt("type")));
                    delivery.setErrorText(rs.getString("errorText"));
                    delivery.setInitiator(rs.getString("initiator"));
                    delivery.setTimestamp(rs.getString("timestamp"));
                    list.add(delivery);
                }
                while (rs.next());
            }
            // Ничо не нашли
            else {
                logger.warn("Result set is empty! Deliveries not found!");
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
        } catch (DBConnectionException e) {
            logger.error(e.getMessage());
        }
        // Освобождаем ресурсы
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                logger.error("Can't free resources! Reason: " + e.getMessage());
            }
        }
        return list;
    }

    /**
     * Метод находит и возвращает ВСЕ записи из таблицы рассылок (dbo.deliveries), вне зависимости от типа рассылок. Если метод
     * ничего не нашел - метод возвращает значение NULL. Метод не находит файлы, прикрепленные к рассылкам (из таблицы файлов -
     * dbo.deliveriesFiles), файлы для рассылки находит только метод поиска одной рассылки. Также метод не возвращает в результирующих
     * объектах-членах списка ArrayList типы получателей для каждой рассылки (из таблицы типов получателей - dbo.recipientsTypes),
     * типы получателей для рассылки находит только метод поиска одной рассылки.
     *
     * @return ArrayList[DeliveryDTO] список рассылок или NULL.
     */
    public ArrayList<DeliveryDTO> findAll() throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        return this.findAll(null);
    }

    /**
     * Поиск рассылки по идентификатору. Если идентифкатор для поиска неверен - меньше 0 или такой рассылки нет - метод
     * возвращает значение NULL. Метод также находит и добавляет в результат все прикрепленные к рассылке файлы (данные
     * из таблицы dbo.deliveriesFiles).
     *
     * @param id int идентификатор искомой рассылки.
     * @return DeliveryDTO найденная рассылка или значение NULL.
     */
    public DeliveryDTO findByID(int id) throws IllegalAccessException, ClassNotFoundException, InstantiationException {
        logger.debug("DeliveriesDAO: findByID()");
        DeliveryDTO delivery = null;
        Connection conn = null;
        ResultSet rs = null;
        String sql = "select id, subject, text, status, type, errorText, initiator, timestamp, " +
                "fileId, fileName, fileDeliveryId, " +
                "recipientTypeId, recipientTypeDeliveryId, recipientType " +
                "from dbo.allDeliveriesView where id = ";
        // Только если указан положительный идентификатор - тогда ищем
        if (id > 0) {
            sql += id;
            logger.debug("Generated sql: " + sql);
            try {
                conn = this.getConnection();
                rs = SqlExecutor.executeSelectQuery(conn, sql);
                if (rs.next()) {
                    // Если что-то нашли, то берем только первую запись из найденного, хотя запись и должна быть одна.
                    logger.debug("Result set is not empty. Processing.");
                    // Флажок - получены ли данные о рассылке. Если получены - дальше получаем только данные о файлах.
                    boolean deliveryDataOK = false;
                    delivery = new DeliveryDTO();
                    // Данные об одном файле
                    int fileId;
                    String fileName;
                    // Данные об одном типе получателей рассылки
                    int recipientTypeId;
                    int recipientType;
                    // Непосредственно в цикле обработка курсора данных
                    do {
                        // Однократно получаем данные о рассылке из курсора. Дальше получаем только инфу о файлах.
                        if (!deliveryDataOK) {
                            delivery.setId(rs.getInt("id"));
                            delivery.setSubject(rs.getString("subject"));
                            delivery.setText(rs.getString("text"));
                            delivery.setStatus(Defaults.DeliveryStatus.findByIntValue(rs.getInt("status")));
                            delivery.setType(Defaults.DeliveryType.findByIntValue(rs.getInt("type")));
                            delivery.setErrorText(rs.getString("errorText"));
                            delivery.setInitiator(rs.getString("initiator"));
                            delivery.setTimestamp(rs.getString("timestamp"));
                            deliveryDataOK = true;
                        }

                        // Получаем и обрабатываем инфу о текущем файле данной рассылки. Инфа может быть пустой - мы получаем данные
                        // из представления с избыточными данными о рассылках.
                        fileId = rs.getInt("fileId");
                        fileName = rs.getString("fileName");
                        // Сразу проверим идентификатор файла. Имя будет проверено при добавлении файла в рассылку.
                        if (fileId > 0) {
                            logger.debug("Processing file: [" + fileId + ", " + fileName + "]");
                            // Добавляем файл к рассылке
                            delivery.addFile(new DeliveryFileDTO(fileId, delivery.getId(), fileName));
                        }
                        // Если идентификатор файла отрицателен или ноль - отладочное сообщение (данные мы выбираем из представления с
                        // дублирующимися данными - соотв. у рассылки может не быть файлов, а быть много типов получателей, соотв. записи
                        // о файлах будут пустыми)
                        else {
                            logger.debug("Negative or 0 file ID! File id = [" + fileId + "]. File name = [" + fileName + "].");
                        }

                        // Получаем и обрабатываем инфу о текущем типе получателей данной рассылки
                        recipientTypeId = rs.getInt("recipientTypeId");
                        recipientType = rs.getInt("recipientType");
                        // Сразу проверим идентификатор типа получателей. Тип получателей будет проверен при добавлении типа в рассылку.
                        if (recipientTypeId > 0) {
                            logger.debug("Processing recipient type: int value = [" + recipientType + "], " +
                                    "type value = [" + Defaults.RecipientType.findByIntValue(recipientType) + "].");
                            // Добавляем тип получателя к рассылке
                            delivery.addRecipient(new RecipientTypeDTO(recipientTypeId, delivery.getId(), recipientType));
                        }
                        // Если идентификатор типа отрицателен или ноль - сообщение об ошибке (что-то не так работает...)
                        else {
                            logger.error("Negative or 0 recipient type ID! ID = [" + recipientTypeId + "]. Type  = [" +
                                    Defaults.RecipientType.findByIntValue(recipientType) + " (" + recipientType + ")].");
                        }
                    }
                    while (rs.next());
                } else {
                    logger.debug("Result set is empty! Delivery [" + id + "] not found!");
                }
            } catch (SQLException e) {
                logger.error(e.getMessage());
            } catch (DBConnectionException e) {
                logger.error(e.getMessage());
            } finally {
                try {
                    if (rs != null) {
                        rs.close();
                    }
                    if (conn != null) {
                        conn.close();
                    }
                } catch (SQLException e) {
                    logger.error("Can't free resources! Reason: " + e.getMessage());
                }
            }
        } else {
            logger.warn("Negative identificator. Can't search!");
        }
        // Возвращаем результат
        return delivery;
    }

    /**
     * Метод создает или изменяет запись в таблице рассылок (dbo.deliveries) и в таблице файлов, прикрепленных к рассылкам
     * (dbo.deliveriesFiles), на основе данных из указанного параметра - экземпляра класса DeliveryDTO. Если в экземпляре
     * указан положительный идентификатор метод ваполняет изменение данных (update), если же идентификатор ноль или меньше -
     * метод выполняет добавление записи (insert). При выполнении добавления данных (insert) метод возвращает идентификатор
     * вставленной записи в таблицу dbo.deliveries.
     *
     * @param delivery DeliveryDTO экземпляр класса, на основе данных которого выполняется пробивание данных в БД.
     * @return int идентификатор вставленной в таблицу dbo.deliveries записи. При обновлении данных (update) метод
     * возвращает 0.
     */
    public int change(DeliveryDTO delivery) throws IllegalAccessException, ClassNotFoundException, InstantiationException {
        logger.debug("DeliveriesDAO: change().");
        Connection conn = null;
        PreparedStatement stmt = null;
        String updateSql = "update dbo.deliveries set subject = ?, text = ?, status = ?, type = ?, " +
                "errorText = ?, initiator = ? where id = ?";
        String createFileSql = "insert into dbo.deliveriesFiles(fileName, deliveryId) values(?, ?)";
        String createRecipientTypeSql = "insert into dbo.recipientsTypes(recipientType, deliveryId) values(?, ?)";
        int newDeliveryId = 0;
        try {
            // Если полученный объект не пуст - создаем/изменяем запись.
            if ((delivery != null) && (!delivery.isEmpty())) {
                conn = this.getConnection();
                // Выбираем тип действия. Если есть идентификатор, то обновляем запись
                if (delivery.getId() > 0) {
                    logger.debug("Processing update.");
                    stmt = conn.prepareStatement(updateSql);
                    stmt.setString(1, delivery.getSubject());
                    stmt.setString(2, delivery.getText());
                    // Статус рассылки
                    if (delivery.getStatus() != null) {
                        stmt.setInt(3, delivery.getStatus().getIntValue());
                    } else {
                        stmt.setInt(3, Defaults.DeliveryStatus.DELIVERY_STATUS_UNKNOWN.getIntValue());
                    }
                    // Тип рассылки
                    if (delivery.getType() != null) {
                        stmt.setInt(4, delivery.getType().getIntValue());
                    } else {
                        stmt.setInt(4, Defaults.DeliveryType.DELIVERY_TYPE_UNKNOWN.getIntValue());
                    }
                    stmt.setString(5, delivery.getErrorText());
                    stmt.setString(6, delivery.getInitiator());
                    stmt.setInt(7, delivery.getId());
                    // Выполняем запрос
                    stmt.executeUpdate();
                }
                // Если идентификатора нет - добавляем запись
                else {
                    logger.debug("Processing create.");
                    // Вызов хранимой процедуры для добавления записи о рассылке (MS SQL 2005)
                    CallableStatement cstmt = conn.prepareCall("{call dbo.addDelivery(?, ?, ?, ?, ?)}");
                    cstmt.setString(1, delivery.getSubject());
                    cstmt.setString(2, delivery.getText());
                    // Тип рассылки. Если указан - добавляем его.
                    if (delivery.getType() != null) {
                        cstmt.setInt(3, delivery.getType().getIntValue());
                    }
                    // Если же тип рассылки не указан (=NULL) - ставим тип UNKNOWN
                    else {
                        cstmt.setInt(3, Defaults.DeliveryType.DELIVERY_TYPE_UNKNOWN.getIntValue());
                    }
                    cstmt.setString(4, delivery.getInitiator());
                    cstmt.registerOutParameter(5, java.sql.Types.INTEGER);
                    cstmt.execute();
                    // Идентификатор вставленной записи (возвращается хранимой процедурой)
                    newDeliveryId = cstmt.getInt(5);
                    logger.debug("New delivery ID: " + newDeliveryId);

                    // Если у класса Delivery указаны файлы - добавляем их в таблицу файлов
                    if ((delivery.getFiles() != null) && (!delivery.getFiles().isEmpty())) {
                        logger.debug("There are files for new delivery. Processing.");
                        for (DeliveryFileDTO deliveryFile : delivery.getFiles()) {
                            // Обрабатываем файл, если он не пуст
                            if (deliveryFile != null) {
                                stmt = conn.prepareStatement(createFileSql);
                                // Каждому добавляемому к данной рассылке файлу добавляем идентификатор рассылки
                                deliveryFile.setDeliveryId(newDeliveryId);
                                logger.debug("Delivery file: " + deliveryFile);
                                // Добавляем запись о файле в БД, если он подходит
                                if ((deliveryFile.getDeliveryId() > 0) && (!StringUtils.isBlank(deliveryFile.getFileName()))) {
                                    stmt.setString(1, deliveryFile.getFileName());
                                    stmt.setInt(2, deliveryFile.getDeliveryId());
                                    stmt.executeUpdate();
                                }
                                // Если файл не подошел - варнинг!
                                else {
                                    logger.warn("Can't add delivery file [" + deliveryFile + "] to DB!");
                                }
                            }
                            // Если же полученный файл NULL - это ошибка
                            else {
                                logger.error("Null-file in delivery's files list!");
                            }
                        }
                    }
                    // Если файлов нет - просто сделаем отладочное сообщение в лог
                    else {
                        logger.debug("No files for this delivery.");
                    }

                    // Если у класса Delivery указаны типы получателей - добавляем их в таблицу типов
                    if ((delivery.getRecipients() != null) && (!delivery.getRecipients().isEmpty())) {
                        logger.debug("There are recipients types for this delivery. Processing.");
                        for (RecipientTypeDTO type : delivery.getRecipients()) {
                            // Обрабатываем тип если он не пуст
                            if (type != null) {
                                stmt = conn.prepareStatement(createRecipientTypeSql);
                                // Добавляем идентификатор рассылки
                                type.setDeliveryId(newDeliveryId);
                                logger.debug("Recipient type: " + type.getRecipientType());
                                // Добавляем запись о типе в БД, если тип подходит
                                if ((type.getDeliveryId() > 0) && (type.getRecipientType() != null)) {
                                    stmt.setInt(1, type.getRecipientType().getIntValue());
                                    stmt.setInt(2, type.getDeliveryId());
                                    stmt.executeUpdate();
                                }
                                // Если тип не подошел - варнинг!
                                else {
                                    logger.warn("Can't add delivery recipient type [" + type + "] to DB!");
                                }
                            }
                            // Если тип NULL - это ошибка
                            else {
                                logger.error("Null-recipient type in delivery's recipients types list!");
                            }
                        }
                    }
                    // Если типов получателей нет - сообщим в лог
                    else {
                        logger.debug("No recipients types for this delivery.");
                    }
                }
            }
            // Если объект пуст - ошибка!
            else {
                logger.error("Can't process empty object.");
            }
        }
        // Перехват ИС
        catch (SQLException e) {
            logger.error(e.getMessage());
        }

        // Освобождение ресурсов
        finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                logger.error("Can't free resources! Reason: " + e.getMessage());
            }
        }
        // Возвращаем результат
        return newDeliveryId;
    }

    /**
     * Установка значений статуса(типа DeliveryStatus) и сообщения(текст) о состоянии указанной рассылки. Если
     * идентификатор рассылки неверен - метод ничего не выполнит. Значения статуса должны быть из диапазона значений
     * членов класса-перечисления DeliveryStatus, если значение не входит в этот диапазон ничего выполнено не будет (например
     * при значении NULL).
     *
     * @param deliveryId int идентифкатор рассылки.
     * @param status     int значение статуса для рассылки.
     * @param errorText  String текст ошибки для рассылки.
     */
    public void setStatusAndError(int deliveryId, Defaults.DeliveryStatus status, String errorText) throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        logger.debug("DeliveriesDAO: setStatusAndError().");
        Connection conn = null;
        PreparedStatement stmt = null;
        String updateSql = "update dbo.deliveries set status = ?, errorText = ? where id = ?";
        try {
            // Если найдем запись по идентификатору и указан не пустой статус - работаем
            if (this.findByID(deliveryId) != null) {
                // Проверка статуса
                if (status != null) {
                    conn = this.getConnection();
                    stmt = conn.prepareStatement(updateSql);
                    stmt.setInt(1, status.getIntValue());
                    stmt.setString(2, errorText);
                    stmt.setInt(3, deliveryId);
                    // Выполняем запрос
                    stmt.executeUpdate();
                }
                // Проверка статуса неудачна
                else {
                    logger.error("Can't set status [" + status + "]!");
                }
            }
            // Если объект пуст - ошибка!
            else {
                logger.error("Can't find delivery with ID [" + deliveryId + "]!");
            }
        }
        // Перехват ИС
        catch (SQLException e) {
            logger.error(e.getMessage());
        }
        // Освобождение ресурсов
        finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                logger.error("Can't free resources! Reason: " + e.getMessage());
            }
        }
    }

}