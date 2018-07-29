package jdb.utils;

import jdb.exceptions.DBModelException;
import jdb.model.dto.DBDTOModel;
import jdb.model.dto.TableDTOModel;
import jdb.model.integrity.DBIntegrityModel;
import jdb.model.integrity.TableIntegrityModel;
import jdb.model.structure.DBStructureModel;
import jdb.model.structure.TableStructureModel;
import jdb.model.time.DBTimedModel;
import jdb.model.time.TableTimedModel;
import junit.framework.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.TreeSet;

import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * ������ ����-������ ��� ������ ������ - DBUtils.
 * @author Gusev Dmitry (����� �������)
 * @version 1.0 (DATE: 03.03.11)
*/

public class DBUtilsTest
 {
  @Test
  public void testIsDBModelEmpty_NULLModel()
   {
    // ������ ������ ��
    assertTrue("On null db model must return TRUE.", DBUtils.isDBModelEmpty(null));
   }

  @Test
  public void testIsDBModelEmpty_TimedModel() throws DBModelException
   {
    // ������ �� ������ � ������
    DBTimedModel model = new DBTimedModel("test");
    assertTrue("On db model without tables must return TRUE.", DBUtils.isDBModelEmpty(model));
    // ������� ������ ������ ������ � ������ ��
    ArrayList<TableTimedModel> tables = new ArrayList<TableTimedModel>();
    model.setTables(tables);
    Assert.assertTrue("On DB model with empty tables list returns TRUE.", model.isEmpty());
    // ������� �������� ������� � ������
    model.addTable(new TableTimedModel("test_table", null));
    assertFalse("Returns FALSE on not empty DB model.", model.isEmpty());
   }

  @Test
  public void testIsDBModelEmpty_StructureModel() throws DBModelException
   {
    // ������ �� ������ � ������
    DBStructureModel model = new DBStructureModel("test");
    assertTrue("On db model without tables must return TRUE.", DBUtils.isDBModelEmpty(model));
    // ������� ������ ������ ������ � ������ ��
    TreeSet<TableStructureModel> tables = new TreeSet<TableStructureModel>();
    model.setTables(tables);
    Assert.assertTrue("On DB model with empty tables list returns TRUE.", model.isEmpty());
    // ������� �������� ������� � ������
    model.addTable(new TableStructureModel("test_table"));
    assertFalse("Returns FALSE on not empty DB model.", model.isEmpty());
   }

  @Test
  public void testIsDBModelEmpty_IntegrityModel() throws DBModelException
   {
    // ������ �� ������ � ������
    DBIntegrityModel model = new DBIntegrityModel("test");
    assertTrue("On db model without tables must return TRUE.", DBUtils.isDBModelEmpty(model));
    // ������� ������ ������ ������ � ������ ��
    ArrayList<TableIntegrityModel> tables = new ArrayList<TableIntegrityModel>();
    model.setTables(tables);
    Assert.assertTrue("On DB model with empty tables list returns TRUE.", model.isEmpty());
    // ������� �������� ������� � ������
    model.addTable(new TableIntegrityModel("test_table"));
    assertFalse("Returns FALSE on not empty DB model.", model.isEmpty());
   }

  @Test
  public void testIsDBModelEmpty_DTOModel() throws DBModelException
   {
    // ������ �� ������ � ������
    DBDTOModel model = new DBDTOModel("test");
    assertTrue("On db model without tables must return TRUE.", DBUtils.isDBModelEmpty(model));
    // ������� ������ ������ ������ � ������ ��
    ArrayList<TableDTOModel> tables = new ArrayList<TableDTOModel>();
    model.setTables(tables);
    Assert.assertTrue("On DB model with empty tables list returns TRUE.", model.isEmpty());
    // ������� �������� ������� � ������
    model.addTable(new TableDTOModel("test_table"));
    assertFalse("Returns FALSE on not empty DB model.", model.isEmpty());
   }

 }