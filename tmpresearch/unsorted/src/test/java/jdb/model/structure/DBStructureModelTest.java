package jdb.model.structure;

import jdb.exceptions.DBModelException;
import org.junit.Test;

import java.util.TreeSet;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Модуль юнит-тестов для класса-модели БД (модель структуры БД).
 * @author Gusev Dmitry (Гусев Дмитрий)
 * @version 1.0 (DATE: 21.03.11)
*/

public class DBStructureModelTest
 {

  @Test
  public void isEmptyTest() throws DBModelException
   {
    DBStructureModel model = new DBStructureModel("test_db");
    // Пустая модель БД
    assertTrue("On empty DB model must return TRUE.", model.isEmpty());
    // Добавим пустой список таблиц в модель БД
    TreeSet<TableStructureModel> tables = new TreeSet<TableStructureModel>();
    model.setTables(tables);
    assertTrue("On DB model with empty tables list returns TRUE.", model.isEmpty());
    // Добавим непустую таблицу в модель
    model.addTable(new TableStructureModel("test_table"));
    assertFalse("Returns FALSE on not empty DB model.", model.isEmpty());
   }

 }