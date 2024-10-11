package jdb.model.time;

import jdb.exceptions.DBModelException;
import jdb.model.TableModel;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Модель таблицы с указанием времени последнего обновления (максимальное значение поля timestamp данной таблицы).
 * Имя таблицы хранится только в верхнем регистре - для обеспечения универсальности поиска по имени таблицы.
 * @author Gusev Dmitry (019gus)
 * @version 3.0 (DATE: 27.07.2010)
 *
 * @deprecated вместо данного класса рекомендуется использовать класс
 * {@link jdb.nextGen.models.SimpleDBIntegrityModel SimpleDBIntegrityModel}
*/

public class TableTimedModel extends TableModel implements Serializable
 {
  static final long serialVersionUID = 3235351342579063883L;

  /** Максимальные дата/время (таймштамп - timestamp) для всех записей таблицы. */
  private Timestamp timeStamp;

  /**
   * Конструктор. Обязательно инициализирует наименование таблицы.
   * @param tableName String имя создаваемой модели таблицы.
   * @param timeStamp Timestamp таймштамп данной таблицы (время+дата).
   * @throws DBModelException ИС возникает при инициализации таблицы с пустым именем.
  */
  public TableTimedModel(String tableName, Timestamp timeStamp) throws DBModelException
   {super(tableName); this.timeStamp = timeStamp;}

  public Timestamp getTimeStamp() {return timeStamp;}
  public void setTimeStamp(Timestamp timeStamp) {this.timeStamp = timeStamp;}

  /** Строковое представление данного объекта (модели таблицы). */
  @Override
  public String toString()
   {
    StringBuilder tableString = new StringBuilder();
    tableString.append(" TABLE: ");
    // Если есть схема - укажем ее
    if (!StringUtils.isBlank(this.getTableSchema())) {tableString.append(this.getTableSchema()).append(".");}
    tableString.append(this.getTableName()).append(" (TYPE: ").append(this.getTableType()).append("); ");
    tableString.append("TIMESTAMP: ").append(this.timeStamp);
    // Возвращаем результат
    return tableString.toString();
   }

 }