package jdb.model.structure;

import jdb.exceptions.DBModelException;
import org.junit.Test;

import java.util.TreeSet;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * ������ ����-������ ��� ������-������ �� (������ ��������� ��).
 * @author Gusev Dmitry (����� �������)
 * @version 1.0 (DATE: 21.03.11)
*/

public class DBStructureModelTest
 {

  @Test
  public void isEmptyTest() throws DBModelException
   {
    DBStructureModel model = new DBStructureModel("test_db");
    // ������ ������ ��
    assertTrue("On empty DB model must return TRUE.", model.isEmpty());
    // ������� ������ ������ ������ � ������ ��
    TreeSet<TableStructureModel> tables = new TreeSet<TableStructureModel>();
    model.setTables(tables);
    assertTrue("On DB model with empty tables list returns TRUE.", model.isEmpty());
    // ������� �������� ������� � ������
    model.addTable(new TableStructureModel("test_table"));
    assertFalse("Returns FALSE on not empty DB model.", model.isEmpty());
   }

 }