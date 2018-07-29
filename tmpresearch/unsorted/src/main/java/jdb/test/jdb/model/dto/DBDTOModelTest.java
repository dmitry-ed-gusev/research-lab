package jdb.model.dto;

import jdb.exceptions.DBModelException;
import org.junit.Test;

import java.util.ArrayList;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * ������ ����-������ ��� ������-������ �� (������ �� � ������� - DTO).
 * @author Gusev Dmitry (����� �������)
 * @version 1.0 (DATE: 21.03.11)
*/

public class DBDTOModelTest
 {
  @Test
  public void isEmptyTest() throws DBModelException
   {
    DBDTOModel model = new DBDTOModel("test_db");
    // ������ ������ ��
    assertTrue("On empty DB model must return TRUE.", model.isEmpty());
    // ������� ������ ������ ������ � ������ ��
    ArrayList<TableDTOModel> tables = new ArrayList<TableDTOModel>();
    model.setTables(tables);
    assertTrue("On DB model with empty tables list returns TRUE.", model.isEmpty());
    // ������� �������� ������� � ������
    model.addTable(new TableDTOModel("test_table"));
    assertFalse("Returns FALSE on not empty DB model.", model.isEmpty());
   }
 }