package jdb.model.time;

import jdb.exceptions.DBModelException;
import jdb.model.TableModel;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * ������ ������� � ��������� ������� ���������� ���������� (������������ �������� ���� timestamp ������ �������).
 * ��� ������� �������� ������ � ������� �������� - ��� ����������� ��������������� ������ �� ����� �������.
 * @author Gusev Dmitry (019gus)
 * @version 3.0 (DATE: 27.07.2010)
 *
 * @deprecated ������ ������� ������ ������������� ������������ �����
 * {@link jdb.nextGen.models.SimpleDBIntegrityModel SimpleDBIntegrityModel}
*/

public class TableTimedModel extends TableModel implements Serializable
 {
  static final long serialVersionUID = 3235351342579063883L;

  /** ������������ ����/����� (��������� - timestamp) ��� ���� ������� �������. */
  private Timestamp timeStamp;

  /**
   * �����������. ����������� �������������� ������������ �������.
   * @param tableName String ��� ����������� ������ �������.
   * @param timeStamp Timestamp ��������� ������ ������� (�����+����).
   * @throws DBModelException �� ��������� ��� ������������� ������� � ������ ������.
  */
  public TableTimedModel(String tableName, Timestamp timeStamp) throws DBModelException
   {super(tableName); this.timeStamp = timeStamp;}

  public Timestamp getTimeStamp() {return timeStamp;}
  public void setTimeStamp(Timestamp timeStamp) {this.timeStamp = timeStamp;}

  /** ��������� ������������� ������� ������� (������ �������). */
  @Override
  public String toString()
   {
    StringBuilder tableString = new StringBuilder();
    tableString.append(" TABLE: ");
    // ���� ���� ����� - ������ ��
    if (!StringUtils.isBlank(this.getTableSchema())) {tableString.append(this.getTableSchema()).append(".");}
    tableString.append(this.getTableName()).append(" (TYPE: ").append(this.getTableType()).append("); ");
    tableString.append("TIMESTAMP: ").append(this.timeStamp);
    // ���������� ���������
    return tableString.toString();
   }

 }