package jdb.model.integrity;

import jdb.exceptions.DBModelException;
import org.junit.Test;

import java.util.ArrayList;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * Модуль юнит-тестов для класса-модели БД (модель целостности БД).
 * @author Gusev Dmitry (Гусев Дмитрий)
 * @version 1.0 (DATE: 21.03.11)
*/

public class DBIntegrityModelTest
 {

  @Test
  public void isEmptyTest() throws DBModelException
   {
    DBIntegrityModel model = new DBIntegrityModel("test_db");
    // Пустая модель БД
    assertTrue("On empty DB model must return TRUE.", model.isEmpty());
    // Добавим пустой список таблиц в модель БД
    ArrayList<TableIntegrityModel> tables = new ArrayList<TableIntegrityModel>();
    model.setTables(tables);
    assertTrue("On DB model with empty tables list returns TRUE.", model.isEmpty());
    // Добавим непустую таблицу в модель
    model.addTable(new TableIntegrityModel("test_table"));
    assertFalse("Returns FALSE on not empty DB model.", model.isEmpty());
   }

 }