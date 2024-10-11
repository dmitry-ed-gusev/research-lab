package jdb.model.applied.dao;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Данный класс будет являться родительским для классов, реализующих DAO-компоненты приложений. В основном эти
 * компоненты будут использоваться для J2EE приложений. Данный класс предназначен для работы с источником данных
 * (Data Source), который находится на дереве ресурсов JNDI. Имя ресурса-источника данных указывается в конструкторе
 * класса. Класс хранит имя JNDI-источника данных и один его инициализированный экземпляр (для всего приложения).
 * Также класс реализует метод получения соединения с СУБД (Connection) с помощью хранящегося источника данных.
 *
 * @author Gusev Dmitry (019gus)
 * @version 2.0 (DATE: 22.11.2010)
*/

public class DSCommonDAO
 {
  /** Компонент-логгер данного класса. */
  private        Logger     logger         = null;
  /** Поле для хранения имени источника данных. */
  private static String     dataSourceName = null;
  /** Поле для хранения ссылки на JNDI-источник данных. */
  private static DataSource dataSource     = null;

  /**
   * Конструктор компонента. В конструкторе иниицализируется поле, хранящее ссылку на источник данных, а также
   * компонент-логгер для данного класса. Также метод инициализирует внутреннее поле для хранения имени источника
   * данных. Если источник данных уже инициализирован (ссылка на класс DataSource не пуста), ничего не происходит,
   * только пишется отладочная запись в лог.
   * @param loggerName String
   * @param dataSourceName String
  */
  @SuppressWarnings({"JNDIResourceOpenedButNotSafelyClosed"})
  public DSCommonDAO(String loggerName, String dataSourceName)
   {
    // Получаем ссылку на логгер для данного модуля
    if (StringUtils.isBlank(loggerName)) {logger = Logger.getLogger(getClass().getName());}
    else                                 {logger = Logger.getLogger(loggerName);}
    // Если источник данных еще не инициализирован - инициализация.
    if (dataSource == null)
     {
      // Инициализация поля для хранения ссылки на источник данных, только если указано не пустое имя источника
      if (!StringUtils.isBlank(dataSourceName))
       {
        logger.debug("Data source [" + dataSourceName + "] is not initialized yet. Processing.");
        // Сохраним имя источника данных
        DSCommonDAO.dataSourceName = dataSourceName;
        // Непосредственная инициализация контекста JNDI и поиск необходимого ресурса по имени
        Context context = null;
        try
         {
          context = new InitialContext();
          DSCommonDAO.dataSource = (DataSource) context.lookup(DSCommonDAO.dataSourceName);
          logger.info("Data source [" + DSCommonDAO.dataSourceName + "] initialized!");
         }
        // Перехват ИС
        catch (NamingException e)
         {logger.error("Can't get datasource [" + DSCommonDAO.dataSourceName + "]: " + e.getMessage());}
        // Закрываем контекст. Хотя, если его и не закрывать, то разницы особой нет (ну типа экономим ресурсы).
        finally
         {
          try {if (context != null) {context.close();}}
          catch (NamingException e) {logger.error("Can't close context! Reason: " + e.getMessage());}
         }
       }
      // Если указанное имя источника данных пусто - ничего не инициализируем, в лог пишем ошибку
      else {logger.error("Data source name is EMPTY!");}
     }
    // Если же источник данных (ссылка на него) уже инициализирован - сообщим об этом
    else {logger.debug("Data source already initialized!");}
   }

  /**
   * Конструктор. В конструкторе иниицализируется поле, хранящее ссылку на источник данных, а также компонент-логгер
   * для данного класса. Внутреннее поле для хранения имени источника данных остается пустым. Если источник данных уже
   * инициализирован (ссылка на класс DataSource не пуста), ничего не происходит, только пишется отладочная запись в лог.
   * @param loggerName String
   * @param ds DataSource
  */
  public DSCommonDAO(String loggerName, DataSource ds)
   {
    // Получаем ссылку на логгер для данного модуля
    if (StringUtils.isBlank(loggerName)) {logger = Logger.getLogger(getClass().getName());}
    else                                 {logger = Logger.getLogger(loggerName);}
    // Если источник данных еще не инициализирован - инициализация.
    if (DSCommonDAO.dataSource == null)
     {
      logger.debug("Starting DataSource initialization.");
      // Инициализация ссылки на источник данных, если в качестве параметра указан непустой (не NULL) экземпляр класса
      if (ds != null)
       {
        logger.debug("Data source is not initialized yet. Processing.");
        DSCommonDAO.dataSource = ds;
        logger.info("Data source [" + DSCommonDAO.dataSourceName + "] initialized!");
       }
      // Если указанная ссылка на источник данных пуста - ничего не инициализируем, в лог пишем ошибку
      else {logger.error("Data source link is NULL!");}
     }
    // Если же источник данных (ссылка на него) уже инициализирован - сообщим об этом
    else {logger.debug("Data source already initialized!");}
   }

  /**
   * Метод возвращает соединение, полученное у источника данных данного приложения, ссылка на который хранится
   * в поле данного класса.
   * @return Connection соединение с СУБД, полученное от источника данных или значение null.
   * @throws java.sql.SQLException ИС, возникающая в процессе получения соединения.
  */
  public Connection getConnection() throws SQLException
   {
    // Если источник данных не пуст - получаем от него соединение
    if (dataSource != null)
     {
      logger.debug("Data source OK. Getting connection.");
      // Получаем соединение от источника данных.
      Connection conn = DSCommonDAO.dataSource.getConnection();
      // Если полученное соединение не пусто - все ок, возвращаем его
      if (conn != null) {return conn;}
      // Если же соединение пусто - ошибка
      else {throw new SQLException("Connection received from data source [" + DSCommonDAO.dataSourceName + "] is empty!");}
     }
    // Если же источник пуст - ошибка
    else {throw new SQLException("Data source [" + DSCommonDAO.dataSourceName + "] is null (maybe not initialized)!");}
   }

  /**
   * Метод возвращает ссылку на источник данных (DataSource), ссылка на который хранится в данном классе. Если источник
   * данных не инициализирован, то метд вызовет ИС SQLException.
   * @return DataSource возвращаемая ссылка на источник данных.
   * @throws SQLException ИС, возникающая в процессе получения ссылки на  источник данных.
  */
  public DataSource getDataSource() throws SQLException {return dataSource;}

 }