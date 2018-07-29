package jdb.model.integrity;

import jdb.exceptions.DBModelException;
import org.junit.Test;

import java.util.ArrayList;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * ������ ����-������ ��� ������-������ �� (������ ����������� ��).
 * @author Gusev Dmitry (����� �������)
 * @version 1.0 (DATE: 21.03.11)
*/

public class DBIntegrityModelTest
 {

  @Test
  public void isEmptyTest() throws DBModelException
   {
    DBIntegrityModel model = new DBIntegrityModel("test_db");
    // ������ ������ ��
    assertTrue("On empty DB model must return TRUE.", model.isEmpty());
    // ������� ������ ������ ������ � ������ ��
    ArrayList<TableIntegrityModel> tables = new ArrayList<TableIntegrityModel>();
    model.setTables(tables);
    assertTrue("On DB model with empty tables list returns TRUE.", model.isEmpty());
    // ������� �������� ������� � ������
    model.addTable(new TableIntegrityModel("test_table"));
    assertFalse("Returns FALSE on not empty DB model.", model.isEmpty());
   }

 }