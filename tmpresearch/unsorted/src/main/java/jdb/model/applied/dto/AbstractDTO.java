package jdb.model.applied.dto;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * ����� ��������� ����������� DTO-��������� ��� ���������� �����. ��� DTO ���������� ��� ������ � �� ������ ����
 * ������������ �� ������� (��� ��� ������ ����� ��� ������ - isEmpty() � toString()). ����� toString() ����������
 * ��� ������ (��������� ������ ����������� ���������) ����� ToStringBuilder ���������� org.apache.commons.lang �
 * ����� ���� �� ���������� ToStringStyle.MULTI_LINE_STYLE, �������������� ��� ������-������� ����� ������ ������������
 * ��� ������ (ToStringBuilder � ToStringStyle). ����� ��� ���������� ������ toString() � ������ ������� ����������
 * �������� � ��� ����� ���������� �� ������� ������ ������-��������, �������� ��� � ������� �����������
 * appendSuper(super.toString()).
 * @author Gusev Dmitry (019gus)
 * @version 1.0 (DATE: 11.02.2010)
 * @deprecated ����� ����� �� ����� - ��������� ������!
*/

public abstract class AbstractDTO
 {
  private int id      = -1;
  private int deleted = 0;

  public int getId() {
   return id;
  }

  public void setId(int id) {
   this.id = id;
  }

  public int getDeleted() {
   return deleted;
  }

  public void setDeleted(int deleted) {
   this.deleted = deleted;
  }

  /**
   * ����� ��� ����������� ���� ��� ��� ��������� ������� ������. ���� - ������ �������� ���� �� ��������� �������.
   * ������ ������� ������� ������ ������ ������������� ���� ����� isEmpty().
   * @return boolean ������/���� � ����������� �� ����, ���� ��� ��� ��������� ������.
  */
  public abstract boolean isEmpty();

  /**
   * ����� ��� ������������� ����������� ��������� ���������� ������ - ���������� �������� ����� ������. ������ �����
   * ������������ ��� ������� ����������. ������ ������� ������� ������ ������ ������������� ���� ����� toString().
   * � ����� ������ ������ toString() ������ ������� ������� ������ ������ �������� ����� ������� ������ ������-��������.
   * @return String ��������� ������������� ��������� ���������� ������.
   */
  @Override
  public String toString()
   {
    return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).
     append("id", id).
     append("deleted", deleted).
     toString();
   }

 }