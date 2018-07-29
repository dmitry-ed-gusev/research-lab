package jdb.processing.sql.generation;

import jdb.DBConsts.DBType;
import jdb.model.DBModelConsts;
import jdb.model.structure.FieldStructureModel;
import jdb.model.structure.TableStructureModel;
import jdb.model.structure.key.IndexedField;
import org.apache.log4j.Logger;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * Данный класс содержит различные вспомогательные методы для работы генератора sql-запросов (класс SQLGenerator).
 * @author Gusev Dmitry (019gus)
 * @version 2.0 (DATE: 04.02.2009)
*/

public class SQLUtils
 {
  /** Компонент-логгер данного класса. */
  private static Logger logger = Logger.getLogger(SQLUtils.class.getName());

  /**
   * Метод генерирует и возвращает SQL-описание поля таблицы, структура которого представлена экземпляром
   * класса FieldStructureModel. Если образец пуст - метод вернет значение NULL.
   * @param field FieldStructureModel образец для создания описания.
   * @param targetDBType DBType тип СУБД назначения, для которой генерируется описание. Может быть пуст.
   * @return String непосредственно сгенерированное описание поля или null.
  */
  public static String getFieldSQL(FieldStructureModel field, DBType targetDBType)
   {
    StringBuilder sql = null;

    // Обрабатываем поле, только если оно не пусто
    if (field != null)
     {
      sql = new StringBuilder(field.getName()).append(" ");
      // Если у поля есть значение по умолчанию, то это значение обрабатывается или в блоке построения
      // описания поля (switch {}), или после этого блока. Чтобы два раза не обрабатывать значение по умолчанию и
      // нужен данный флажок.
      boolean isDefaultProcessed = false;
      
      switch (field.getJavaDataType())
       {
        // Дробный тип данных
        case Types.NUMERIC:
          // Если не указан тип СУБД для которой создается запрос - используем тип исходной СУБД
          if (DBType.INFORMIX.equals(targetDBType))      {sql.append("DECIMAL(13,2)");}
          else if (DBType.MYSQL.equals(targetDBType))    {sql.append("DECIMAL(13,2)");}
          else sql.append(field.getDbmsDataType());
         break;

        // Дробный тип данных
        case Types.DECIMAL:
          // Если не указан тип СУБД для которой создается запрос - используем тип исходной СУБД
          if (DBType.MYSQL.equals(targetDBType)) sql.append("DECIMAL(13,2)");
          else sql.append(field.getDbmsDataType());
         break;

        // Целочисленный тип
        case Types.INTEGER: sql.append(field.getDbmsDataType()); break;

        // Дробный тип данных
        case Types.DOUBLE: sql.append(field.getDbmsDataType()); break;

        // Тип данных дата
        case Types.DATE: sql.append(field.getDbmsDataType()); break;

        case Types.BIT: sql.append(field.getDbmsDataType()); break;

        // Символьный тип данных фиксированной длины
        case Types.CHAR:
          // Если указан тип целевой СУБД, то укажем специфический для нее символьный тип
          if (DBType.INFORMIX.equals(targetDBType)) {sql.append("NCHAR");}
          else if (DBType.DBF.equals(targetDBType)) {sql.append("CHAR");}
          else if (DBType.MYSQL.equals(targetDBType)) {sql.append("NVARCHAR");}
          else {sql.append(field.getDbmsDataType());}

          // Добавляем размерность поля.
          sql.append("(").append(field.getSize()).append(")");

          // Для символьного поля значение по умолчанию (если оно есть) должно быть в кавычках
          if (field.getDefaultValue() != null)
           {
            // Установим флажок обработки значения по умолчанию
            isDefaultProcessed = true;
            // Значение по умолчанию должно начинаться и оканчиваться на одинарную кавычку. Если это не так -
            // кавычки нужно добавить.
            String defaultValue = field.getDefaultValue();
            if ((!defaultValue.startsWith("'")) && (!defaultValue.endsWith("'")))
             {sql.append(" DEFAULT '").append(defaultValue).append("'");}
            else {sql.append(" DEFAULT ").append(defaultValue);}
           }
         
         break;

        // Символьный тип данных переменной длины
        case Types.VARCHAR:
          // Если указан тип целевой СУБД, то укажем специфический для нее символьный тип
          if (DBType.INFORMIX.equals(targetDBType)) {sql.append("NVARCHAR");}
          else if (DBType.DBF.equals(targetDBType)) {sql.append("VARCHAR");}
          else {sql.append(field.getDbmsDataType());}
          // Добавляем размерность поля.
          sql.append("(").append(field.getSize()).append(")");
          // Для символьного поля значение по умолчанию (если оно есть) должно быть в кавычках
          if (field.getDefaultValue() != null)
           {
            // Установим флажок обработки значения по умолчанию
            isDefaultProcessed = true;
            // Значение по умолчанию должно начинаться и оканчиваться на одинарную кавычку. Если это не так -
            // кавычки нужно добавить.
            String defaultValue = field.getDefaultValue();
            if ((!defaultValue.startsWith("'")) && (!defaultValue.endsWith("'")))
             {sql.append(" DEFAULT '").append(defaultValue).append("'");}
            else {sql.append(" DEFAULT ").append(defaultValue);}
           }
         break;

        // Длинный символьный тип данных фиксированной длины (в DBF - memo - поле)
        case Types.LONGVARCHAR:
         // Если указан тип целевой БД, то укажем специфический для нее символьный тип
         if (DBType.INFORMIX.equals(targetDBType))
          {sql.append("NCHAR(").append(DBModelConsts.LONGVARCHAR_SIZE).append(")");}
         else
          {sql.append(field.getDbmsDataType()).append("(").append(field.getSize()).append(")");}
         
         // Для символьного поля значение по умолчанию (если оно есть) должно быть в кавычках
         if (field.getDefaultValue() != null)
          {
           // Установим флажок обработки значения по умолчанию
           isDefaultProcessed = true;
           // Значение по умолчанию должно начинаться и оканчиваться на одинарную кавычку. Если это не так -
           // кавычки нужно добавить.
           String defaultValue = field.getDefaultValue();
           if ((!defaultValue.startsWith("'")) && (!defaultValue.endsWith("'")))
            {sql.append(" DEFAULT '").append(defaultValue).append("'");}
           else {sql.append(" DEFAULT ").append(defaultValue);}
          }
         break;

        // Тип данных таймштамп (время+дата)
        case Types.TIMESTAMP:
         // Непосредственно тип данных
         sql.append(field.getDbmsDataType());
         // Обработка значения по умолчанию
         if (field.getDefaultValue() != null)
          {
           switch (targetDBType)
            {
             // Если конечная СУБД - MySQL, то значение по умолчанию игнорируем для данного поля.
             case MYSQL:
              // Установим флажок - значение по умолчанию обработано
              isDefaultProcessed = true;
              // todo: обработка значения по умолчанию для MySQL!
              logger.warn("DEFAULT VALUE FOR FIELD [" + field.getName() + "] TYPE [TIMESTAMP] IGNORED!");
              break;
            }
          }
         break;

        // Если ни один из типов данных не подошел
        default:
          sql.append("[TYPE: ").append(field.getJavaDataType()).append("]");
          logger.fatal("UNKNOWN DATA TYPE: " + field.getJavaDataType());
         break;
       }
      
      // Если для поля указано значение по умолчанию - обработаем его (значение) - если оно еще не обработано!
      if ((field.getDefaultValue() != null) && (!isDefaultProcessed))
       {sql.append(" DEFAULT ").append(field.getDefaultValue());}

      // Если данное поле не может содержать значений NULL - указываем это
      if (!field.isNullable()) {sql.append(" NOT NULL");}
     }
    String result;
    if (sql == null) {result = null;} else {result = sql.toString();}
    return result;
   }

  /**
   * Метод формирует и возвращает sql-запрос только для создания таблицы БД. Метод для внутреннего использования.
   * Данный метод не генерирует sql-запрос для индексов и ограничений данной таблицы. Если указанная в качестве
   * параметра модель пуста - метод вернет значение null.
   * @param table TableStructureModel модель таблицы, по которой будет сгенерирован запрос.
   * @param targetDBType DBType тип СУБД, для которой готовится данный запрос.
   * @param usePrimaryKey boolean данный параметр указывает - генерировать (true) или нет (false) опции для
   * создания первичного ключа. Если указана опция true - опция "первичный ключ" будет указана в sql-операторе
   * [CREATE TABLE...], если же указана опция false, то вместо "первичного ключа" будет создан уникальный индекс по
   * тем же полям, которые входят в первичный ключ.
   * @param addSemi boolean добавлять или нет разделитель в конец каждого sql-запроса (разделитель -> ;)
   * @return String sql-запрос для создания ТОЛЬКО таблицы.
  */
  public static String getCreateTableFieldsSQL(TableStructureModel table, DBType targetDBType,
                                               boolean usePrimaryKey, boolean addSemi)
   {
    StringBuilder sql = null;
    // Если полученная таблица не пуста - работаем
    if (table != null)
     {
      sql = new StringBuilder("CREATE TABLE ").append(table.getTableName()).append(" \n(");
      // Обрабатываем список полей данной таблицы (если он не пуст)
      if ((table.getFields() != null) && (!table.getFields().isEmpty()))
       {
        Iterator iterator = table.getFields().iterator();
        while (iterator.hasNext())
         {
          // Получили поле
          FieldStructureModel field = (FieldStructureModel) iterator.next();
          // Сгенерировали его (поля) sql-описание
          sql.append("\n").append(SQLUtils.getFieldSQL(field, targetDBType));
          // Если надо - добавили запятую
          if (iterator.hasNext()) {sql.append(",");}
         }
        // Если указана опция usePrimaryKey=true и у таблицы есть первичные ключи - генерируем запись [PRIMARY KEY...]
        if (usePrimaryKey)
         {
          String keys = table.getCSVPKFieldsList();
          // Если есть первичный ключ - генерируем запись [PRIMARY KEY...]
          if ((keys != null) && (!keys.isEmpty())) {sql.append(", \n  PRIMARY KEY (").append(keys).append(")");}
         }
        sql.append("\n)");
        // Если нужна тока-с-запятой в конце запроса - добавим ее
        if (addSemi) {sql.append(";");}
        // Добавим символ перевода строки в конец запроса (для его удобочитаемости)
        sql.append("\n");
       }
     }
    String result;
    if (sql == null) {result = null;} else {result = sql.toString();}
    return result;
   }

  /**
   * Метод возвращает sql-запрос для создания индексов данной таблицы. Метод для внутреннего использования.
   * Данный метод не генерирует sql-запрос для полей таблицы. Если указанная в качестве параметра модель
   * таблицы пуста - метод вренет значение null.
   * @param table TableStructureModel модель таблицы, по которой будет сгенерирован запрос.
   * @param targetDBType DBType тип СУБД, для которой готовится данный запрос.
   * @param usePrimaryKey boolean данный параметр указывает - генерировать (true) или нет (false) опции для
   * создания первичного ключа. Если указана опция true - опция "первичный ключ" будет указана в sql-операторе
   * [CREATE TABLE...], если же указана опция false, то вместо "первичного ключа" будет создан уникальный индекс по
   * тем же полям, которые входят в первичный ключ.
   * @param addSemi boolean добавлять или нет разделитель в конец каждого sql-запроса (разделитель -> ;)
   * @return ArrayList[String] список sql-запросов для создания индексов таблицы.
  */
  public static ArrayList<String> getCreateTableIndexesSQL(TableStructureModel table, DBType targetDBType,
                                                           boolean usePrimaryKey, boolean addSemi)
   {
    ArrayList<String> sql = null;
    // Если таблица не пуста и у данной таблицы вообще есть индексы - тогда формируем sql-запрос
    if ((table != null) && (table.getIndexes() != null) && (!table.getIndexes().isEmpty()))
     {
      sql = new ArrayList<String>();
      // Обрабатываем индексы данной таблицы
      TreeSet<String> processed = new TreeSet<String>();
      for (IndexedField field : table.getIndexes())
       {
        // Если указана опция usePrimaryKey=true и данное поле входит в состав первичного ключа - пропускаем его
        // (при опции usePrimaryKey=true вместо индекса будет сгенерирована запись [PRIMARY KEY...])
        if (!(usePrimaryKey && field.isPrimaryKey()))
         {
          // Если индекс с данным именем еще не обрабатывался - обрабатываем
          if (!processed.contains(field.getIndexName()))
           {
            // Добавляем индекс в список обработанных
            processed.add(field.getIndexName());
            // Непосредственно формируем sql-запрос на создание индекса
            StringBuilder query = new StringBuilder("CREATE ");
            // Признак уникальности индекса
            if (field.isUnique()) {query.append("UNIQUE ");}
            query.append("INDEX ").append(field.getIndexName());
            query.append(" ON ").append(table.getTableName()).append(" (");
            // Проходим по всему списку индексируемый полей, собирая поля для данного индекса
            boolean flag = false;
            for (IndexedField iField : table.getIndexes())
             {
              if (iField.getIndexName().equals(field.getIndexName()))
               {if (!flag) {flag = true;} else {query.append(",");} query.append(iField.getFieldName());}
             }
            query.append(")");
            // Если в конце запроса нужна точка-с-запятой - добавляем
            if (addSemi) {query.append(";");}
            // Добавляем символ перевода строки в конец запроса - для удобочитаемости
            query.append("\n");
            
            // Созданный запрос добавляем к результирующему списку запросов
            sql.add(query.toString());
           }
         }
       }
     }
    return sql;
   }

  /**
   * Метод генерирует sql-запросы на сброс полей. При сбросе полей из таблицы теоретически не должно быть проблем -
   * если только поле участвует в каком-либо ограничении или индексе. Тогда, скорее всего, надо будет сбрасывать
   * и этот индекс.
   * @param current TableStructureModel таблица, которую с помощью sql-запроса нужно привести к нужному образцу.
   * @param foreign TableStructureModel таблица-образец для изменения таблицы current.
   * @return String sql-запрос(ы) для сброса полей в таблице current.
  */
  // todo: метод должен возвращать ArrayList<String>
  public static String getDropFieldsSQL(TableStructureModel current, TableStructureModel foreign)
   {
    StringBuilder sql = null;
    // Если обе таблицы не пусты и не эквивалетны - тогда генерируем sql-запрос для превращения таблицы
    // current в таблицу foreign (сброс полей из таблицы current, чтобы она стала похожа на foreign)
    if (current != null && foreign != null && !current.equals(foreign))
     {
      // Проходим по таблице current и определяем в ней те поля, которых нет в таблице foreign
      for (FieldStructureModel field : current.getFields())
       {
        boolean found = false;
        Iterator iterator = foreign.getFields().iterator();
        while (iterator.hasNext() && !found)
         {
          FieldStructureModel foreignField = (FieldStructureModel) iterator.next();
          // Если поле с таким же, как у поля текущей таблицы current именем найдено - все ок.
          if (field.getName().equals(foreignField.getName())) {found = true;}
         }
        // Если пройдя по всем полям таблицы foreign мы не нашли такого поля - поле в список на удаление
        if (!found)
         {
          if (sql == null) sql = new StringBuilder();
          sql.append("ALTER TABLE ").append(current.getTableName()).append(" DROP ").append(field.getName()).append(";\n");
         }
       }
     }
    String result;
    if (sql == null) {result = null;} else {result = sql.toString();}
    return result;
   }

  /**
   * Метод генерирует sql-запросы для добавления полей. При добавлении полей в таблицу не должно возникать проблем.
   * @param current TableStructureModel таблица, которую с помощью sql-запроса нужно привести к нужному образцу.
   * @param currentDBType DBType тип СУБД таблицы current.
   * @param foreign TableStructureModel таблица-образец для изменения таблицы current.
   * @return String sql-запрос(ы) для добавления полей в таблицу current.
  */
  // todo: метод должен возвращать ArrayList<String>
  public static String getAddFieldsSQL(TableStructureModel current, DBType currentDBType, TableStructureModel foreign)
   {
    StringBuilder sql = null;
    // Если обе таблицы не пусты и не эквивалетны - тогда генерируем sql-запрос для превращения таблицы
    // current в таблицу foreign (сброс полей из таблицы current, чтобы она стала похожа на foreign)
    if (current != null && foreign != null && !current.equals(foreign))
     {
      for (FieldStructureModel field : foreign.getFields())
       {
        boolean found = false;
        Iterator iterator = current.getFields().iterator();
        // Проходим по полям таблицы current - ищем поле, аналогичное текущему полю foreign
        while (iterator.hasNext() && !found)
         {
          FieldStructureModel currentField = (FieldStructureModel) iterator.next();
          // Если нашли аналогичное поле(по имени) - такое поле добавлять уже не надо. Его, может быть,
          // надо только изменить - но это уже работа другого метода.
          if (field.getName().equals(currentField.getName())) {found = true;}
         }
        // Если мы не нашли поле из таблицы foreign в таблице current - его надо добавить в таблицу current
        if (!found)
         {
          if (sql == null) sql = new StringBuilder();
          sql.append("ALTER TABLE ").append(current.getTableName()).append(" ADD ");
          sql.append(SQLUtils.getFieldSQL(field, currentDBType)).append(";\n");
         }
       }
     }
    String result;
    if (sql == null) {result = null;} else {result = sql.toString();}
    return result;
   }
  
  /**
   * Метод генерирует sql-запросы для модификации полей таблицы current. При изменении поля(-ей) возможно множество
   * разных ситуаций...
   * @param current TableStructureModel таблица, которую с помощью sql-запроса нужно привести к нужному образцу.
   * @param currentDBType DBType тип СУБД таблицы current.
   * @param foreign TableStructureModel таблица-образец для изменения таблицы current.
   * @return String sql-запрос(ы) для изменения полей в таблице current.
  */
  // todo: метод должен возвращать ArrayList<String>
  public static String getChangeFieldsSQL(TableStructureModel current, DBType currentDBType, TableStructureModel foreign)
   {
    StringBuilder sql = null;
    // Если обе таблицы не пусты и не эквивалетны - тогда генерируем sql-запрос для превращения таблицы
    // current в таблицу foreign (сброс полей из таблицы current, чтобы она стала похожа на foreign)
    if (current != null && foreign != null && !current.equals(foreign))
     {
      for (FieldStructureModel field : foreign.getFields())
       {
        boolean found = false;
        Iterator iterator = current.getFields().iterator();
        // Проходим по полям таблицы current - ищем поле, аналогичное текущему полю foreign
        while (iterator.hasNext() && !found)
         {
          FieldStructureModel currentField = (FieldStructureModel) iterator.next();
          // Если нашли аналогичное поле(по имени) - проверяем его в нашей таблице current.
          if (field.getName().equals(currentField.getName()))
           {
            // Поиск в любом случае прекращаем - совпадают поля или нет
            found = true;
            // Если же поля разнятся - текущее поле из таблицы foreign будет образцом для поля с таким же именем
            // из таблицы current
            if (!field.equals(currentField))
             {
              if (sql == null) sql = new StringBuilder();
              sql.append("ALTER TABLE ").append(current.getTableName()).append(" MODIFY ");
              sql.append(SQLUtils.getFieldSQL(field, currentDBType)).append(";\n");
             }
           }
         }
        // Если мы не нашли поле из таблицы foreign в таблице current - его надо добавить в
        // таблицу current - но это уже другой метод
       }
     }
    String result;
    if (sql == null) {result = null;} else {result = sql.toString();}
    return result;
   }

 }