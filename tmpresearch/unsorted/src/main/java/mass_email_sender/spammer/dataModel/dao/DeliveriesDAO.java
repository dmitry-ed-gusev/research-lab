package spammer.dataModel.dao;

import jdb.exceptions.DBConnectionException;
import jdb.exceptions.DBModuleConfigException;
import jdb.model.applied.dao.DBConfigCommonDAO;
import jdb.processing.sql.execution.SqlExecutor;
import jlib.logging.InitLogger;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import spammer.Defaults;
import spammer.Defaults.DeliveryStatus;
import spammer.Defaults.DeliveryType;
import spammer.dataModel.dto.DeliveryDTO;
import spammer.dataModel.dto.DeliveryFileDTO;
import spammer.dataModel.dto.RecipientTypeDTO;

import java.sql.*;
import java.util.ArrayList;

/**
 * ����� ��� ������ � �������� �������� � ������������� � ��� ������.
 * @author Gusev Dmitry (019gus)
 * @version 4.0 (DATE: 21.12.2010)
*/

@SuppressWarnings({"JDBCResourceOpenedButNotSafelyClosed"})
public class DeliveriesDAO extends DBConfigCommonDAO
 {
  /** ������ ������. */
  private Logger logger = Logger.getLogger(Defaults.LOGGER_NAME);

  /** �����������. ���� ���������������� ���������� �� ���������. */
  public DeliveriesDAO() {super(Defaults.LOGGER_NAME, Defaults.DBCONFIG_FILE);}

  /**
   * �����������. ��������� �� ��������� ���������������� ������ ������������ �������, ������-���� ��� ����������
   * � ���� ����������� � �������� ��������� ������������.
   * @param dbConfigFileName String ������-���� ��� ���������� � ����. 
  */
  public DeliveriesDAO(String dbConfigFileName) {super(Defaults.LOGGER_NAME, dbConfigFileName);}

  /**
   * ����� ������� � ���������� ������ �� ������� �������� (dbo.deliveries). ���� ����� ������ �� ����� - �����
   * ���������� �������� NULL. ����� �� ������� �����, ������������� � ��������� (�� ������� ������ - dbo.deliveriesFiles),
   * ����� ��� �������� ������� ������ ����� ������ ����� ��������. ����� ����� �� ���������� � �������������� ��������-������
   * ������ ArrayList ���� ����������� ��� ������ �������� (�� ������� ����� ����������� - dbo.recipientsTypes).
   * @param type DeliveryType ��� ��������, ������� ������ ���� �������. ���� �������� �� ������� (NULL), �� ����� ������
   * ��� ������ ������� ��������.
   * @return ArrayList[DeliveryDTO] ������ �������� ��� NULL.
  */
  public ArrayList<DeliveryDTO> findAll(DeliveryType type)
   {
    logger.debug("DeliveriesDAO: findAll()");
    Connection             conn = null;
    ResultSet              rs   = null;
    ArrayList<DeliveryDTO> list = null;
    String                 sql   = "select id, subject, text, status, type, errorText, initiator, timestamp " +
                                   "from dbo.deliveries";
    // ��������� ��� �������� � �������
    if ((type != null) && (!DeliveryType.DELIVERY_TYPE_UNKNOWN.equals(type)))
     {
      logger.debug("Adding delivery type [" + type + "] to query.");
      sql += " where type = " + type.getIntValue();
     }
    // ��������� ���������� �������� (����� ���������� ���������� ���� ��������)
    sql += " order by timestamp desc";
    // ���������� ���������
    logger.debug("Generated sql: " + sql);
    try
     {
      conn = this.getConnection();
      rs = SqlExecutor.executeSelectQuery(conn, sql);
      // ���-�� �����
      if (rs.next())
       {
        logger.debug("Result set is not empty! Processing.");
        list = new ArrayList<DeliveryDTO>();
        do
         {
          DeliveryDTO delivery = new DeliveryDTO();
          delivery.setId(rs.getInt("id"));
          delivery.setSubject(rs.getString("subject"));
          delivery.setText(rs.getString("text"));
          delivery.setStatus(DeliveryStatus.findByIntValue(rs.getInt("status")));
          delivery.setType(DeliveryType.findByIntValue(rs.getInt("type")));
          delivery.setErrorText(rs.getString("errorText"));
          delivery.setInitiator(rs.getString("initiator"));
          delivery.setTimestamp(rs.getString("timestamp"));
          list.add(delivery);
         }
        while (rs.next());
       }
      // ���� �� �����
      else {logger.warn("Result set is empty! Deliveries not found!");}
     }
    catch (DBModuleConfigException e) {logger.error(e.getMessage());}
    catch (SQLException e)            {logger.error(e.getMessage());}
    catch (DBConnectionException e)   {logger.error(e.getMessage());}
    // ����������� �������
    finally
     {
      try {if(rs != null) {rs.close();} if(conn != null) {conn.close();}}
      catch (SQLException e) {logger.error("Can't free resources! Reason: " + e.getMessage());}
     }
    return list;
   }

  /**
   * ����� ������� � ���������� ��� ������ �� ������� �������� (dbo.deliveries), ��� ����������� �� ���� ��������. ���� �����
   * ������ �� ����� - ����� ���������� �������� NULL. ����� �� ������� �����, ������������� � ��������� (�� ������� ������ -
   * dbo.deliveriesFiles), ����� ��� �������� ������� ������ ����� ������ ����� ��������. ����� ����� �� ���������� � ��������������
   * ��������-������ ������ ArrayList ���� ����������� ��� ������ �������� (�� ������� ����� ����������� - dbo.recipientsTypes),
   * ���� ����������� ��� �������� ������� ������ ����� ������ ����� ��������.
   * @return ArrayList[DeliveryDTO] ������ �������� ��� NULL.
  */
  public ArrayList<DeliveryDTO> findAll()
   {return this.findAll(null);}

  /**
   * ����� �������� �� ��������������. ���� ������������ ��� ������ ������� - ������ 0 ��� ����� �������� ��� - �����
   * ���������� �������� NULL. ����� ����� ������� � ��������� � ��������� ��� ������������� � �������� ����� (������
   * �� ������� dbo.deliveriesFiles).
   * @param id int ������������� ������� ��������.
   * @return DeliveryDTO ��������� �������� ��� �������� NULL.
  */
  public DeliveryDTO findByID(int id)
   {
    logger.debug("DeliveriesDAO: findByID()");
    DeliveryDTO delivery = null;
    Connection  conn     = null;
    ResultSet   rs       = null;
    String      sql      = "select id, subject, text, status, type, errorText, initiator, timestamp, " +
                           "fileId, fileName, fileDeliveryId, " +
                           "recipientTypeId, recipientTypeDeliveryId, recipientType " +
                           "from dbo.allDeliveriesView where id = ";
    // ������ ���� ������ ������������� ������������� - ����� ����
    if (id > 0)
     {
      sql += id;
      logger.debug("Generated sql: " + sql);
      try
       {
        conn = this.getConnection();
        rs   = SqlExecutor.executeSelectQuery(conn, sql);
        if (rs.next())
         {
          // ���� ���-�� �����, �� ����� ������ ������ ������ �� ����������, ���� ������ � ������ ���� ����.
          logger.debug("Result set is not empty. Processing.");
          // ������ - �������� �� ������ � ��������. ���� �������� - ������ �������� ������ ������ � ������.
          boolean deliveryDataOK = false;
          delivery = new DeliveryDTO();
          // ������ �� ����� �����
          int    fileId;
          String fileName;
          // ������ �� ����� ���� ����������� ��������
          int    recipientTypeId;
          int    recipientType;
          // ��������������� � ����� ��������� ������� ������
          do
           {
            // ���������� �������� ������ � �������� �� �������. ������ �������� ������ ���� � ������.
            if (!deliveryDataOK)
             {
              delivery.setId(rs.getInt("id"));
              delivery.setSubject(rs.getString("subject"));
              delivery.setText(rs.getString("text"));
              delivery.setStatus(DeliveryStatus.findByIntValue(rs.getInt("status")));
              delivery.setType(DeliveryType.findByIntValue(rs.getInt("type")));
              delivery.setErrorText(rs.getString("errorText"));
              delivery.setInitiator(rs.getString("initiator"));
              delivery.setTimestamp(rs.getString("timestamp"));
              deliveryDataOK = true;
             }

            // �������� � ������������ ���� � ������� ����� ������ ��������. ���� ����� ���� ������ - �� �������� ������
            // �� ������������� � ����������� ������� � ���������.
            fileId   = rs.getInt("fileId");
            fileName = rs.getString("fileName");
            // ����� �������� ������������� �����. ��� ����� ��������� ��� ���������� ����� � ��������.
            if (fileId > 0)
             {
              logger.debug("Processing file: [" + fileId + ", " + fileName + "]");
              // ��������� ���� � ��������
              delivery.addFile(new DeliveryFileDTO(fileId, delivery.getId(), fileName));
             }
            // ���� ������������� ����� ����������� ��� ���� - ���������� ��������� (������ �� �������� �� ������������� �
            // �������������� ������� - �����. � �������� ����� �� ���� ������, � ���� ����� ����� �����������, �����. ������
            // � ������ ����� �������)
            else {logger.debug("Negative or 0 file ID! File id = [" + fileId + "]. File name = [" + fileName + "].");}

            // �������� � ������������ ���� � ������� ���� ����������� ������ ��������
            recipientTypeId = rs.getInt("recipientTypeId");
            recipientType   = rs.getInt("recipientType");
            // ����� �������� ������������� ���� �����������. ��� ����������� ����� �������� ��� ���������� ���� � ��������.
            if (recipientTypeId > 0)
             {
              logger.debug("Processing recipient type: int value = [" + recipientType + "], " +
                           "type value = [" + Defaults.RecipientType.findByIntValue(recipientType) + "].");
              // ��������� ��� ���������� � ��������
              delivery.addRecipient(new RecipientTypeDTO(recipientTypeId, delivery.getId(), recipientType));
             }
            // ���� ������������� ���� ����������� ��� ���� - ��������� �� ������ (���-�� �� ��� ��������...)
            else {logger.error("Negative or 0 recipient type ID! ID = [" + recipientTypeId + "]. Type  = [" +
                               Defaults.RecipientType.findByIntValue(recipientType)+ " (" + recipientType + ")].");}
           }
          while(rs.next());
         }
        else {logger.debug("Result set is empty! Delivery [" + id + "] not found!");}
       }
      catch(SQLException e)             {logger.error(e.getMessage());}
      catch(DBConnectionException e)    {logger.error(e.getMessage());}
      catch (DBModuleConfigException e) {logger.error(e.getMessage());}
      finally
       {
        try {if(rs != null) {rs.close();} if(conn != null) {conn.close();}}
        catch (SQLException e) {logger.error("Can't free resources! Reason: " + e.getMessage());}
       }
     }
    else {logger.warn("Negative identificator. Can't search!");}
    // ���������� ���������
    return delivery;
   }

  /**
   * ����� ������� ��� �������� ������ � ������� �������� (dbo.deliveries) � � ������� ������, ������������� � ���������
   * (dbo.deliveriesFiles), �� ������ ������ �� ���������� ��������� - ���������� ������ DeliveryDTO. ���� � ����������
   * ������ ������������� ������������� ����� ��������� ��������� ������ (update), ���� �� ������������� ���� ��� ������ -
   * ����� ��������� ���������� ������ (insert). ��� ���������� ���������� ������ (insert) ����� ���������� �������������
   * ����������� ������ � ������� dbo.deliveries.
   * @param delivery DeliveryDTO ��������� ������, �� ������ ������ �������� ����������� ���������� ������ � ��.
   * @return int ������������� ����������� � ������� dbo.deliveries ������. ��� ���������� ������ (update) �����
   * ���������� 0.
  */
  public int change(DeliveryDTO delivery)
   {
    logger.debug("DeliveriesDAO: change().");
    Connection        conn                   = null;
    PreparedStatement stmt                   = null;
    String            updateSql              = "update dbo.deliveries set subject = ?, text = ?, status = ?, type = ?, " +
                                               "errorText = ?, initiator = ? where id = ?";
    String            createFileSql          = "insert into dbo.deliveriesFiles(fileName, deliveryId) values(?, ?)";
    String            createRecipientTypeSql = "insert into dbo.recipientsTypes(recipientType, deliveryId) values(?, ?)";
    int               newDeliveryId          = 0;
    try
     {
      // ���� ���������� ������ �� ���� - �������/�������� ������.
      if ((delivery != null) && (!delivery.isEmpty()))
       {
        conn = this.getConnection();
        // �������� ��� ��������. ���� ���� �������������, �� ��������� ������
        if (delivery.getId() > 0)
         {
          logger.debug("Processing update.");
          stmt = conn.prepareStatement(updateSql);
          stmt.setString(1, delivery.getSubject());
          stmt.setString(2, delivery.getText());
          // ������ ��������
          if (delivery.getStatus() != null) {stmt.setInt(3, delivery.getStatus().getIntValue());}
          else                              {stmt.setInt(3, DeliveryStatus.DELIVERY_STATUS_UNKNOWN.getIntValue());}
          // ��� ��������
          if (delivery.getType() != null)   {stmt.setInt(4, delivery.getType().getIntValue());}
          else                              {stmt.setInt(4, DeliveryType.DELIVERY_TYPE_UNKNOWN.getIntValue());}
          stmt.setString(5, delivery.getErrorText());
          stmt.setString(6, delivery.getInitiator());
          stmt.setInt(7,    delivery.getId());
          // ��������� ������
          stmt.executeUpdate();
         }
        // ���� �������������� ��� - ��������� ������
        else
         {
          logger.debug("Processing create.");
          // ����� �������� ��������� ��� ���������� ������ � �������� (MS SQL 2005)
          CallableStatement cstmt = conn.prepareCall("{call dbo.addDelivery(?, ?, ?, ?, ?)}");
          cstmt.setString(1, delivery.getSubject());
          cstmt.setString(2, delivery.getText());
          // ��� ��������. ���� ������ - ��������� ���.
          if (delivery.getType() != null) {cstmt.setInt(3, delivery.getType().getIntValue());}
          // ���� �� ��� �������� �� ������ (=NULL) - ������ ��� UNKNOWN
          else                            {cstmt.setInt(3, DeliveryType.DELIVERY_TYPE_UNKNOWN.getIntValue());}
          cstmt.setString(4, delivery.getInitiator());
          cstmt.registerOutParameter(5, java.sql.Types.INTEGER);
          cstmt.execute();
          // ������������� ����������� ������ (������������ �������� ����������)
          newDeliveryId = cstmt.getInt(5);
          logger.debug("New delivery ID: " + newDeliveryId);

          // ���� � ������ Delivery ������� ����� - ��������� �� � ������� ������
          if ((delivery.getFiles() != null) && (!delivery.getFiles().isEmpty()))
           {
            logger.debug("There are files for new delivery. Processing.");
            for (DeliveryFileDTO deliveryFile : delivery.getFiles())
             {
              // ������������ ����, ���� �� �� ����
              if (deliveryFile != null)
               {
                stmt = conn.prepareStatement(createFileSql);
                // ������� ������������ � ������ �������� ����� ��������� ������������� ��������
                deliveryFile.setDeliveryId(newDeliveryId);
                logger.debug("Delivery file: " + deliveryFile);
                // ��������� ������ � ����� � ��, ���� �� ��������
                if ((deliveryFile.getDeliveryId() > 0) && (!StringUtils.isBlank(deliveryFile.getFileName())))
                 {
                  stmt.setString(1, deliveryFile.getFileName());
                  stmt.setInt(2,    deliveryFile.getDeliveryId());
                  stmt.executeUpdate();
                 }
                // ���� ���� �� ������� - �������!
                else {logger.warn("Can't add delivery file [" + deliveryFile + "] to DB!");}
               }
              // ���� �� ���������� ���� NULL - ��� ������
              else {logger.error("Null-file in delivery's files list!");}
             }
           }
          // ���� ������ ��� - ������ ������� ���������� ��������� � ���
          else {logger.debug("No files for this delivery.");}

          // ���� � ������ Delivery ������� ���� ����������� - ��������� �� � ������� �����
          if ((delivery.getRecipients() != null) && (!delivery.getRecipients().isEmpty()))
           {
            logger.debug("There are recipients types for this delivery. Processing.");
            for (RecipientTypeDTO type : delivery.getRecipients())
             {
              // ������������ ��� ���� �� �� ����
              if (type != null)
               {
                stmt = conn.prepareStatement(createRecipientTypeSql);
                // ��������� ������������� ��������
                type.setDeliveryId(newDeliveryId);
                logger.debug("Recipient type: " + type.getRecipientType());
                // ��������� ������ � ���� � ��, ���� ��� ��������
                if ((type.getDeliveryId() > 0) && (type.getRecipientType() != null))
                 {
                  stmt.setInt(1, type.getRecipientType().getIntValue());
                  stmt.setInt(2, type.getDeliveryId());
                  stmt.executeUpdate();
                 }
                // ���� ��� �� ������� - �������!
                else {logger.warn("Can't add delivery recipient type [" + type + "] to DB!");}
               }
              // ���� ��� NULL - ��� ������
              else {logger.error("Null-recipient type in delivery's recipients types list!");}
             }
           }
          // ���� ����� ����������� ��� - ������� � ���
          else {logger.debug("No recipients types for this delivery.");}
         }
       }
      // ���� ������ ���� - ������!
      else {logger.error("Can't process empty object.");}
     }
    // �������� ��
    catch (SQLException e)            {logger.error(e.getMessage());}
    catch (DBModuleConfigException e) {logger.error(e.getMessage());}
    catch (DBConnectionException e)   {logger.error(e.getMessage());}
    // ������������ ��������
    finally
     {
      try {if(stmt != null) {stmt.close();} if(conn != null) {conn.close();}}
      catch (SQLException e) {logger.error("Can't free resources! Reason: " + e.getMessage());}
     }
    // ���������� ���������
    return newDeliveryId;
   }

  /**
   * ��������� �������� �������(���� DeliveryStatus) � ���������(�����) � ��������� ��������� ��������. ����
   * ������������� �������� ������� - ����� ������ �� ��������. �������� ������� ������ ���� �� ��������� ��������
   * ������ ������-������������ DeliveryStatus, ���� �������� �� ������ � ���� �������� ������ ��������� �� ����� (��������
   * ��� �������� NULL).
   * @param deliveryId int ������������ ��������.
   * @param status int �������� ������� ��� ��������.
   * @param errorText String ����� ������ ��� ��������.
  */
  public void setStatusAndError(int deliveryId, DeliveryStatus status, String errorText)
   {
    logger.debug("DeliveriesDAO: setStatusAndError().");
    Connection        conn      = null;
    PreparedStatement stmt      = null;
    String            updateSql     = "update dbo.deliveries set status = ?, errorText = ? where id = ?";
    try
     {
      // ���� ������ ������ �� �������������� � ������ �� ������ ������ - ��������
      if (this.findByID(deliveryId) != null)
       {
        // �������� �������
        if (status != null)
         {
          conn = this.getConnection();
          stmt = conn.prepareStatement(updateSql);
          stmt.setInt(1,    status.getIntValue());
          stmt.setString(2, errorText);
          stmt.setInt(3,    deliveryId);
          // ��������� ������
          stmt.executeUpdate();
         }
        // �������� ������� ��������
        else {logger.error("Can't set status [" + status + "]!");}
       }
      // ���� ������ ���� - ������!
      else {logger.error("Can't find delivery with ID [" + deliveryId + "]!");}
     }
    // �������� ��
    catch (SQLException e)            {logger.error(e.getMessage());}
    catch (DBModuleConfigException e) {logger.error(e.getMessage());}
    catch (DBConnectionException e)   {logger.error(e.getMessage());}
    // ������������ ��������
    finally
     {
      try {if(stmt != null) {stmt.close();} if(conn != null) {conn.close();}}
      catch (SQLException e) {logger.error("Can't free resources! Reason: " + e.getMessage());}
     }
   }

  /**
   * ����� ��� ������������.
   * @param args String[] ��������� ������.
  */
  public static void main(String[] args)
   {
    InitLogger.initLoggers(new String[] {"jdb", "org", "jlib", Defaults.LOGGER_NAME});
    Logger logger = Logger.getLogger(Defaults.LOGGER_NAME);

    // ���������� ��������
    //DeliveryDTO delivery = new DeliveryDTO();
    //delivery.setSubject("����");
    //delivery.setText("����� ���������");
    //delivery.setInitiator("019gus");
    //delivery.addFile(new DeliveryFileDTO("file1.zip"));
    //delivery.addFile(new DeliveryFileDTO("file2.zip"));
    //delivery.addRecipient(new RecipientTypeDTO(Defaults.RecipientType.RECIPIENT_TYPE_SHIPOWNERS_ENG));
    // ����������
    //new DeliveriesDAO().change(delivery);

    logger.info(new DeliveriesDAO().findAll(Defaults.DeliveryType.DELIVERY_TYPE_TEST));
    //logger.info(new DeliveriesDAO().findByID(6));

    // ����� �������
    //new DeliveriesDAO().setStatus(3, 100);
   }

 }