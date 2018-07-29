package jdb.nextGen.serialization;

import jdb.DBConsts;
import jdb.nextGen.exceptions.JdbException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Класс реализует сериализуемый набор записей БД (ResultSet). Конструктору класса передается незакрытый экземпляр класса
 * ResultSet и на его основе класс SerializableResultSet заполняет данными свои поля.
 * <br>Особенности данного класса:
 * <ul>
 *  <li> класс является immutable - не меняет свое состояние после создания экземпляра. Т.е методы "аксессоры" для доступа к
 *       полям класса возвращают так называемые "защитные" (defensive) копии полей и списков.
 *  <li> имя таблицы задается при создании экземпляра класса - обязательно должно быть непустым!
 *  <li> имена полей (столбцов) таблицы и имя таблицы хранятся в ВЕРХНЕМ регистре символов - для удобства поиска
 *       (поиск не зависит от регистра символов).
 *  <li> наименование ключевого поля задается значением по умолчанию - DBConsts.FIELD_NAME_KEY (="ID"). Не предусмотрено
 *       методов для изменения этого значения - у каждой таблицы должно быть простое (не составное) ключевое поле ID
 *       (для работы с данным классом).
 * </ul>
 *
 * @author Gusev Dmitry (Гусев Дмитрий)
 * @version 6.0 (DATE: 09.06.2011)
*/

@SuppressWarnings({"NullableProblems"})
public final class SerializableResultSet implements Serializable
 {
  /** Поле для совместимости с последующими версиями класса. */
  static final            long               serialVersionUID = 8540809276925800602L;
  // Логгер класса.
  transient private final Logger             logger           = Logger.getLogger(this.getClass().getName());

  // Имя таблицы. Задается параметром конструктора.
  private final String                       tableName;
  // Индекс ключевого поля в списке полей курсора.
  private final int                          keyFieldIndex;
  // Список всех полей курсора.
  private final ArrayList<String>            fieldsNames;
  // Полный набор данных курсора.
  private final ArrayList<ArrayList<String>> data;

  /**
   * Конструктор.
   * @param rs ResultSet
   * @param tableName String
   * @throws SQLException ошибки при работе с курсором данных (ResultSet)
   * @throws JdbException ошибки в параметрах метода.
  */
  public SerializableResultSet(ResultSet rs, String tableName) throws SQLException, JdbException
   {
    // Проверяем указанное имя таблицы
    if (!StringUtils.isBlank(tableName))
     {
      // Имя таблицы
       this.tableName = tableName.toUpperCase();
      // Проверяем курсор данных и переводим указатель на первую (или следующую) запись
      if ((rs != null) && rs.next())
       {
        // Метаинформация
        ResultSetMetaData meta = rs.getMetaData();
        int columnCount = meta.getColumnCount();
        // Инициализация полей (имена полей, данные)
        fieldsNames  = new ArrayList<String>(columnCount);
        data         = new ArrayList<ArrayList<String>>();
        // Заполняем поля метаинфой
        for (int i = 1; i <= columnCount; i++)
         {fieldsNames.add(i - 1, meta.getColumnName(i).toUpperCase());}
        // Наименование ключевого поля - по умолчанию значение константы. Автоматом высчитываем
        // индекс (номер в массиве(списке) полей) для данного ключевого поля.
        this.keyFieldIndex = fieldsNames.indexOf(DBConsts.FIELD_NAME_KEY);
        // Если в курсоре нет ключевого поля - ошибка!
        if (keyFieldIndex < 0) {throw new SQLException("No such index field [" + DBConsts.FIELD_NAME_KEY + "]!");}
        // Заполняем поле с данными (ресурсоемкая операция)
        do
         {
          ArrayList<String> row = new ArrayList<String>(columnCount);
          for (int i = 1; i <= columnCount; i++) {row.add(rs.getString(i));}
          data.add(row);
         }
        while (rs.next());
       }
      // Если получен пустой ResultSet - сообщим об этом в лог - это потенциальная ошибка!
      else
       {
        logger.warn("Empty ResultSet object! Empty class instance was created!");
        this.keyFieldIndex = -1;   // поле всегда необходимо инициализировать!
        this.fieldsNames   = null; // поле всегда необходимо инициализировать!
        this.data          = null; // поле всегда необходимо инициализировать!
       }
     }
    // Если указано пустое имя таблицы - возбуждаем ИС
    else {throw new JdbException("Empty table name!");}
   }

  /**
   * Конструктор.
   * @param rs ResultSet
   * @param tableName String
   * @param count int
   * @throws SQLException ошибки при работе с курсором данных (ResultSet)
   * @throws JdbException ошибки в параметрах метода.
  */
  public SerializableResultSet(ResultSet rs, String tableName, int count) throws SQLException, JdbException
   {
    // Проверяем указанное имя таблицы
    if (!StringUtils.isBlank(tableName))
     {
      // Имя таблицы
      this.tableName = tableName.toUpperCase();
      if ((count > 0) && (rs != null))
       {
        //logger.debug("Table name [" + tableName + "] ok, count [" + count + "] ok, ResultSet not null. Processing."); // <- DEBUG!
        // Локальный (для конструктора) курсор данных
        ArrayList<ArrayList<String>> localData = new ArrayList<ArrayList<String>>();
        // Метаинформация
        ResultSetMetaData meta = rs.getMetaData();
        int columnCount = meta.getColumnCount();
        // Заполняем поле с данными
        int     counter       = 0;    // кол-во обработанных записей (счетчик)
        boolean iterationFlag = true; // флаг итераций, если = true -> итерации продолжаются
        // Непосредственно обработка данных (ресурсоемкая операция)
        while ((counter < count) && iterationFlag)
         {
          if (rs.next())
           {
            ArrayList<String> row = new ArrayList<String>(columnCount);
            for (int i = 1; i <= columnCount; i++) {row.add(rs.getString(i));}
            localData.add(row);
            counter++;
           }
          else {iterationFlag = false;}
         }
        // Если из курсора были прочитаны данные - инициализируем поля с метаинформацией (имена и типы полей)
        if (!localData.isEmpty())
         {
          // Полю класса "данные" присвоим ссылку на локальный курсор данных
          this.data = localData;
          // Если в курсоре есть данные - инициализируем поля и заполняем метаинфу
          fieldsNames  = new ArrayList<String>(columnCount);
          // Заполняем поля метаинфой
          for (int i = 1; i <= columnCount; i++)
           {fieldsNames.add(i - 1, meta.getColumnName(i).toUpperCase());}
          // Наименование ключевого поля - по умолчанию значение константы. Автоматом высчитываем
          // индекс (номер в массиве(списке) полей) для данного ключевого поля.
          this.keyFieldIndex = fieldsNames.indexOf(DBConsts.FIELD_NAME_KEY);
          // Если в курсоре нет ключевого поля - ошибка!
          if (keyFieldIndex < 0) {throw new SQLException("No such index field [" + DBConsts.FIELD_NAME_KEY + "]!");}
         }
        // Если же данных нет - обнулим проинициализированное поле с данными. Метаинфу не читаем из курсора.
        // Также проинициализируем пустыми значениями остальные поля курсора
        else
         {
          logger.info("Empty SerializableResultSet class instance was created!");
          this.keyFieldIndex = -1;   // поле всегда необходимо инициализировать!
          this.fieldsNames   = null; // поле всегда необходимо инициализировать!
          this.data          = null; // поле всегда необходимо инициализировать!
         }
       }
      // Если крусор == NULL или указано неверное кол-во записей (<= 0), то экземпляр класса все-таки будет
      // инициализирован, но поля данных останутся пустыми (пустой курсор)
      else
       {
        logger.warn("Wrong count value [" + count + "] or ResultSet is NULL! Empty SerializableResultSet class instance was created!");
        this.keyFieldIndex = -1;   // поле всегда необходимо инициализировать!
        this.fieldsNames   = null; // поле всегда необходимо инициализировать!
        this.data          = null; // поле всегда необходимо инициализировать!
       }
     }
    // Если указано пустое имя таблицы - возбуждаем ИС
    else {throw new JdbException("Empty table name!");}
   }


  /**
   * Метод возвращает данные курсора (ссылка на внутренний список не возвращается - возвращается его
   * "защитная" (defensive) копия).
   * @return ArrayList[ArrayList[String]]
  */
  public ArrayList<ArrayList<String>> getData() {return new ArrayList<ArrayList<String>>(data);}

  /**
   * Индекс (положение) ключевого поля в списке полей.
   * @return int индекс ключевого поля. Если нет ключевого поля или индекс не найден - метод вернет значение -1.
  */
  public int getKeyFieldIndex() {return keyFieldIndex;}

  /**
   * Возвращает имя таблицы, полученное из ResultSet'a, на основе которого построен данный экземпляр класса. Если
   * экземпляр пуст (построен на основе пустого ResultSet'a), то метод вернет значение NULL.
   * @return String имя таблицы или NULL.
  */
  public String getTableName() {return tableName;}

  /**
   * Метод возвращает количество строк данных класса. Если класс построен на пустом ResultSet'e - метод вернет
   * значение 0.
   * @return int количество строк данных.
  */
  public int getRowsCount()
   {
    int result = 0;
    if ((data != null) && (!data.isEmpty())) {result = data.size();}
    return result;
   }

  /**
   * Метод возвращает значение ИСТИНА/ЛОЖЬ в зависимости от того, является ли данный экземпляр класса пустым.
   * Экземпляр данного класса считается пустым, если пусто одно из полей: поле "наименование таблицы" (tableName)
   * или поле "данные" (data).
   * @return boolean ИСТИНА/ЛОЖЬ в зависимости от того, пуст или нет экземпляр данного класса.
  */
  public boolean isEmpty()
   {
    boolean result = true;
    if (!StringUtils.isBlank(tableName) && (data != null) && (!data.isEmpty())) {result = false;}
    return result;
   }

  /**
   * Метод генерирует и возвращает преподготовленный (prepared) sql-запрос INSERT, который может использоваться для
   * вставки данных, содержащихся в данном сериализуемом курсоре, в таблицу БД. Если в курсоре есть данные и заполнен
   * список полей - запрос будет сгенерирован, в противном случае метод вернет значение NULL. Если указан параметр
   * tableName, то в сгенерированном запросе имя таблицы (куда вставляются данные), заданное при создании экземпляра
   * класса, будет заменено на указанное (если оно не пусто).
   * @param tableName String имя таблицы для генерации запроса INSERT.
   * @return String сгенерированный INSERT-запрос или NULL.
  */
  public String getPreparedInsertSql(String tableName)
   {
    String result = null;
    if (!this.isEmpty() && (fieldsNames != null) && (!fieldsNames.isEmpty()))
     {
      StringBuilder sql = new StringBuilder("insert into ");
      // Если указан параметр - другое имя таблицы, то используем его значение
      if (!StringUtils.isBlank(tableName)) {sql.append(tableName);}
      else                                 {sql.append(this.tableName);}
      sql.append("(");

      // Формируем список полей для запроса
      for (int i = 0; i < fieldsNames.size(); i++)
       {
        sql.append(fieldsNames.get(i));
        if (i < (fieldsNames.size() - 1)) {sql.append(", ");}
       }
      sql.append(") values (");
      // Формируем необходимое количество знаков ? (вопрос)
      for (int i = 0; i < fieldsNames.size(); i++)
       {
        sql.append("?");
        if (i < (fieldsNames.size() - 1)) {sql.append(", ");}
       }
      // Окончание формирования insert-запроса
      sql.append(")");
      result = sql.toString();
     }
    return result;
   }

  /**
   * Метод генерирует и возвращает преподготовленный (prepared) sql-запрос INSERT, который может использоваться для
   * вставки данных, содержащихся в данном сериализуемом курсоре, в таблицу БД. Если в курсоре есть данные и заполнен
   * список полей - запрос будет сгенерирован, в противном случае метод вернет значение NULL. При создании запроса
   * используется имя таблицы, заданное в конструкторе класса.
   * @return String сгенерированный INSERT-запрос или NULL.
  */
  public String getPreparedInsertSql() {return this.getPreparedInsertSql(null);}

  /**
   * Метод генерирует и возвращает преподготовленный (prepared) sql-запрос UPDATE, который может использоваться для
   * обновления данных в таблице БД данными, содержащимися в курсоре. Если в курсоре есть данные, заполнен список полей
   * и список полей содержит ключевое поле - запрос будет сгенерирован, в противном случае метод вернет значение NULL.
   * Если указан параметр tableName, то в сгенерированном запросе имя таблицы, заданное при создании экземпляра класса,
   * будет заменено на указанное (если оно не пусто).
   * @param tableName String имя таблицы для генерации запроса UPDATE.
   * @return String сгенерированный UPDATE-запрос или NULL.
  */
  public String getPreparedUpdateSql(String tableName)
   {
    String result = null;
    // Для верного формирования запроса необходимо выполнение следующих условий: курсор должен содержать данные,
    // список наименований полей не должен быть пуст, должно быть указано непустое имя таблицы, список полей таблицы
    // должен ОБЯЗАТЕЛЬНО содержать ключевое поле.
    if (!this.isEmpty() && (fieldsNames != null) && (!fieldsNames.isEmpty()) && fieldsNames.contains(DBConsts.FIELD_NAME_KEY))
     {
      StringBuilder sql = new StringBuilder("update ");
      // Если указан параметр - другое имя таблицы, то используем его значение
      if (!StringUtils.isBlank(tableName)) {sql.append(tableName);}
      else                                 {sql.append(this.tableName);}
      sql.append(" set ");
      // Формируем список полей для запроса
      for (int i = 0; i < fieldsNames.size(); i++)
       {
        // Значение ключевого поля мы не обновляем - мы обновляем всю запись на
        // основании значения ключевого поля.
        if (!fieldsNames.get(i).equals(DBConsts.FIELD_NAME_KEY))
         {
          sql.append(fieldsNames.get(i)).append(" = ?");
          // Запятую добавляем только если одработанное поле не ключевое и не последнее в списке (или последнее,
          // или за данным полем только ключевое - текущее предпоследнее, ключевое - последнее)
          if ((i < (fieldsNames.size() - 1)) && !((i == (fieldsNames.size() - 2)) && (keyFieldIndex == fieldsNames.size() - 1)))
           {sql.append(", ");}
         }
       }
      sql.append(" where ").append(DBConsts.FIELD_NAME_KEY).append(" = ?");
      result = sql.toString();
     }
    return result;
   }

  /**
   * Метод генерирует и возвращает преподготовленный (prepared) sql-запрос UPDATE, который может использоваться для
   * обновления данных в таблице БД данными, содержащимися в курсоре. Если в курсоре есть данные, заполнен список полей
   * и список полей содержит ключевое поле - запрос будет сгенерирован, в противном случае метод вернет значение NULL.
   * При создании запроса используется имя таблицы, заданное в конструкторе класса.
   * @return String сгенерированный UPDATE-запрос или NULL.
  */
  public String getPreparedUpdateSql() {return this.getPreparedUpdateSql(null);}

 }