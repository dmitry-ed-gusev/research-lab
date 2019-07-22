package dgusev.dbpilot.config;

import dgusev.dbpilot.config.ExtendedDBConfig;
import jdb.exceptions.DBModuleConfigException;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Данный класс реализует полнофункциональную модель конфигурации для соединения с СУБД. Класс наследует от других
 * классов {@link jdb.config.connection.BaseDBConfig BaseJdbcConfig} и {@link ExtendedDBConfig JdbcConfig}.
 * Умеет загружать/сохранять свою конфигурацию в файле на диске, умеет хранить списки ограничений и проверять их,
 * хранит флажок демо-режима, реализует различные проверки параметров. Из всей иерархии классов конфигурирования соединения
 * с СУБД (BaseJdbcConfig -> JdbcConfig -> DBConfig) рекомендуется пользоваться именно данным классом.
 * <br>
 * Важное дополнение. Списки ограничений для таблиц ("разрешенные" и "запрещенные" таблицы) предполагают работу именно
 * с именами таблиц, без учета наименований схем, владельцев таблиц и т.п. Поэтому не рекомендуется в разных схемах данных
 * создавать таблицы с одинаковыми именами - касается в основном СУБД MS SQL Server 2005. Например: dbo.table1 и schema1.table1
 *
 * @author Gusev Dmitry (019gus)
 * @version 3.0 (DATE: 21.12.2009)
*/

public class DBConfig extends ExtendedDBConfig
 {
  /** Компонент-логгер данного класса. */
  private Logger                logger           = Logger.getLogger(getClass().getName());
  /** Список разрешенных к обработке таблиц. */
  private ArrayList<String> allowedTables    = null;
  /** Список запрещенных к обработке таблиц. */
  private ArrayList<String> deprecatedTables = null;
  /** Включен или выключен демо-режим. */
  private boolean               isDemo           = false;

  public ArrayList<String> getAllowedTables() {
   return allowedTables;
  }

  public void setAllowedTables(ArrayList<String> allowedTables) {
   this.allowedTables = allowedTables;
  }

  public ArrayList<String> getDeprecatedTables() {
   return deprecatedTables;
  }

  public void setDeprecatedTables(ArrayList<String> deprecatedTables) {
   this.deprecatedTables = deprecatedTables;
  }

  public boolean isDemo() {
   return isDemo;
  }

  public void setDemo(boolean demo) {
   isDemo = demo;
  }

  /** Сброс списка "разрешенных" таблиц. */
  public void              resetAllowedTables()                                    {this.allowedTables = null;}
  /** Сброс списка "запрещенных" таблиц. */
  public void              resetDeprecatedTables()                                 {this.deprecatedTables = null;}
  /** Сброс всех ограничений (списков "разрешенных" и "запрещенных" таблиц). */
  public void              resetConstraints()                                      {this.resetAllowedTables(); this.resetDeprecatedTables();}
  
  /** Конструктор по умолчанию - создает пустой экземпляр данного класса и больше ничего не делает. */
  public DBConfig() {}

  /**
   * Конструктор загружает данные из указанного конфигурационного xml-файла.
   * @param fileName String xml-файл, из которого будем загружать конфигурацию.
   * @param sectionName String имя подсекции секции &lt;db&gt; в конфиг-файле, из которой будут читаться параметры.
   * @param usePlainPass boolean использовать ли открытый пароль к БД или нет. Если используется открытый пароль, то
   * в соотв. теге конфига будет указан пароль, если же открытый пароль не используем, то в соотв. теге будет указан
   * файл, в котором находится пароль (файл специализированного формата).
   * @throws java.io.IOException ИС - указано пустое имя файла, файл не существует и т.п.
   * @throws org.apache.commons.configuration.ConfigurationException ИС - ошибка при загрузке конфигурации из xml-файла.
   * @throws jdb.exceptions.DBModuleConfigException - ошибка конфигурационного файла - неверный тип СУБД, тип СУБД отсутствует
   * и т.п. ошибки (связанные с конфигурированием).
  */
  public DBConfig(String fileName, String sectionName, boolean usePlainPass)
   throws DBModuleConfigException, IOException, ConfigurationException
   {this.loadFromFile(fileName, sectionName, usePlainPass);}

  /**
   * Конструктор загружает данные из конфигурационного xml-файла. В конструкторе из тега с паролем читается имя
   * файла пароля или открытый пароль - данная функциональность зависит от значения константы DBConfigConsts.USE_PLAIN_PASS.
   * @param fileName String xml-файл, из которого будем загружать конфигурацию.
   * @param sectionName String имя подсекции секции &lt;db&gt; в конфиг-файле, из которой будут читаться параметры.
   * @throws java.io.IOException ИС - указано пустое имя файла, файл не существует и т.п.
   * @throws org.apache.commons.configuration.ConfigurationException ИС - ошибка при загрузке конфигурации из xml-файла.
   * @throws jdb.exceptions.DBModuleConfigException - ошибка конфигурационного файла - неверный тип СУБД, тип СУБД отсутствует
   * и т.п. ошибки (связанные с конфигурированием).
  */
  public DBConfig(String fileName, String sectionName) throws DBModuleConfigException, IOException, ConfigurationException
   {this.loadFromFile(fileName, sectionName);}

  /**
   * Конструктор загружает данные из конфигурационного xml-файла. Параметры данным методом читаются из корня
   * элемента &lt;db&gt;.
   * @param fileName String xml-файл, из которого будем загружать конфигурацию.
   * @param usePlainPass boolean использовать ли открытый пароль к БД или нет. Если используется открытый пароль, то
   * в соотв. теге конфига будет указан пароль, если же открытый пароль не используем, то в соотв. теге будет указан
   * файл, в котором находится пароль (файл специализированного формата).
   * @throws java.io.IOException ИС - указано пустое имя файла, файл не существует и т.п.
   * @throws org.apache.commons.configuration.ConfigurationException ИС - ошибка при загрузке конфигурации из xml-файла.
   * @throws jdb.exceptions.DBModuleConfigException - ошибка конфигурационного файла - неверный тип СУБД, тип СУБД отсутствует
   * и т.п. ошибки (связанные с конфигурированием).
  */
  public DBConfig(String fileName, boolean usePlainPass) throws DBModuleConfigException, IOException, ConfigurationException
   {this.loadFromFile(fileName, usePlainPass);}

  /**
   * Конструктор загружает данные из конфигурационного xml-файла. В конструкторе из тега с паролем читается имя
   * файла пароля или открытый пароль - данная функциональность зависит от значения константы DBConfigConsts.USE_PLAIN_PASS.
   * Параметры данным конструктором читаются из элемента &lt;db&gt;.
   * @param fileName String xml-файл, из которого будем загружать конфигурацию.
   * @throws java.io.IOException ИС - указано пустое имя файла, файл не существует и т.п.
   * @throws org.apache.commons.configuration.ConfigurationException ИС - ошибка при загрузке конфигурации из xml-файла.
   * @throws jdb.exceptions.DBModuleConfigException - ошибка конфигурационного файла - неверный тип СУБД, тип СУБД отсутствует
   * и т.п. ошибки (связанные с конфигурированием).
  */
  public DBConfig(String fileName) throws DBModuleConfigException, IOException, ConfigurationException
   {this.loadFromFile(fileName);}
  
  /**
   * Данный метод добавляет одну таблицу к списку "запрещенных" таблиц. Таблица добавляется, только если указанное
   * имя таблицы не пустое.
   * @param tableName String имя добавляемой таблицы.
  */
  public void addDeprecatedTable(String tableName)
   {
    if (!StringUtils.isBlank(tableName))
     {
      if (this.deprecatedTables == null) {this.deprecatedTables = new ArrayList<String>();}
      this.deprecatedTables.add(tableName.toUpperCase());
     }
    else {logger.warn("addDeprecatedTable(): can't add empty deprecated name!");}
   }

  /**
   * Метод добавляет целый список таблиц к списку "запрещенных". Список добавляется только если он не пуст.
   * @param tablesNames String[] список таблиц, добавляемых к списку "запрещенных".
  */
  public void addDeprecatedTables(String[] tablesNames)
   {
    if ((tablesNames != null) && (tablesNames.length > 0))
     {
      if (this.deprecatedTables == null) {this.deprecatedTables = new ArrayList<String>();}
      this.deprecatedTables.addAll(Arrays.asList(tablesNames));
     }
    else {logger.warn("addDeprecatedTables(): can't add empty deprecated list!");}
   }

  /**
   * Метод добавляет одну таблицу к списку "разрешенных" таблиц. Таблица добавляется только если указано
   * непустое имя.
   * @param tableName String имя добавляемой таблицы.
  */
  public void addAllowedTable(String tableName)
   {
    if (!StringUtils.isBlank(tableName))
     {
      if (this.allowedTables == null) {this.allowedTables = new ArrayList<String>();}
      this.allowedTables.add(tableName.toUpperCase());
     }
    else {logger.warn("addAllowedTable(): can't add empty allowed name!");}
   }

  /**
   * Метод добавляет целый список таблиц к списку "разрешенных". Список добавляется только если он не пуст.
   * @param tablesNames String[] список таблиц, добавляемых к списку "разрешенных".
  */
  public void addAllowedTables(String[] tablesNames)
   {
    if ((tablesNames != null) && (tablesNames.length > 0))
     {
      if (this.allowedTables == null) {this.allowedTables = new ArrayList<String>();}
      this.allowedTables.addAll(Arrays.asList(tablesNames));
     }
    else {logger.warn("addAllowedTables(): can't add empty allowed list!");}
   }

  /**
   * Метод устанавливает оба ограничения на таблицы для данного метода - для разрешенных и для запрещенных
   * таблиц. Ограничения устанавливаются, только если они не пусты (рассматриваются каждое в отдельности).
   * @param allowedTables ArrayList ограничение - список разрешенных таблиц.
   * @param deprecatedTables ArrayList ограничение - список запрещенных таблиц.
  */
  public void setConstraints(ArrayList<String> allowedTables, ArrayList<String> deprecatedTables)
   {
    if ((allowedTables != null) && (!allowedTables.isEmpty()))       {this.allowedTables = allowedTables;}
    if ((deprecatedTables != null) && (!deprecatedTables.isEmpty())) {this.deprecatedTables = deprecatedTables;}
   }

  /**
   * Проверка валидности обработки таблицы с именем tableName. Если имя таблицы пусто - метод возвращает значение false.
   * При принятии решения о валидности обработки участвуют списки разрешенных и запрещенных таблиц, а также список
   * таблиц системного каталога для данного типа СУБД. Если же до использования данного метода поле класса dbType не
   * имеет значение - будет возбуждена ИС - DBModuleConfigException.
   * @param tableName String имя таблицы, валидность обработки которой мы проверяем.
   * @return boolean результат проверки валидности обработки таблицы.
   * @throws DBModuleConfigException ИС - перед использованием данного метода не указан тип СУБД для
   * данного конфига.
  */
  public boolean isTableAllowed(String tableName) throws DBModuleConfigException
   {
    boolean result = false;
    // Проверяем указанное для проверки имя - если оно не пусто, то работаем
    if (!StringUtils.isBlank(tableName))
     {
      // Проверяем тип СУБД - если он пуст, то возбуждаем ИС
      if (getDbType() == null) {throw new DBModuleConfigException("Empty DBMS type!");}
      // Если же тип СУБД не пуст - работаем далее
      else
       {
        if
          // Если список "запрещенных" пуст или таблицы в нем нет - ОК
          (((deprecatedTables == null) || (!deprecatedTables.contains(tableName.toUpperCase()))) &&
          // Если список "разрешенных" пуст или таблица в нем есть - ОК
          ((allowedTables == null) || (allowedTables.contains(tableName.toUpperCase()))))
         // Результат положителен
         {
          logger.debug("isTableAllowed(): table [" + tableName + "] is allowed.");
          result = true;
         }
       }
     }
    // Если имя пусто - сообщаем об этом в лог и все (возвращаем false)
    else {logger.warn("isTableAllowed: empty table name!");}
    // Возвращаем результат
    return result;
   }

  @Override
  public String toString()
   {
    return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).
            appendSuper(super.toString()).
            append("allowedTables", allowedTables).
            append("deprecatedTables", deprecatedTables).
            append("isDemo", isDemo).
            toString();
   }

 }