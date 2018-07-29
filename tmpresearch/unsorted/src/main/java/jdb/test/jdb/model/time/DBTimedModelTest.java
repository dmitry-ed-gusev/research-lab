package jdb.model.time;

import jdb.exceptions.DBModelException;
import org.junit.Test;

import java.util.ArrayList;

import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.assertFalse;

/**
 * Модуль юнит-тестов для класса-модели БД (модель с указанием времени).
 * @author Gusev Dmitry (Гусев Дмитрий)
 * @version 1.0 (DATE: 17.03.11)
*/

public class DBTimedModelTest
 {
  @Test
  public void isEmptyTest() throws DBModelException
   {
    DBTimedModel model = new DBTimedModel("test_db");
    // Пустая модель БД
    assertTrue("On empty DB model must return TRUE.", model.isEmpty());
    // Добавим пустой список таблиц в модель БД
    ArrayList<TableTimedModel> tables = new ArrayList<TableTimedModel>();
    model.setTables(tables);
    assertTrue("On DB model with empty tables list returns TRUE.", model.isEmpty());
    // Добавим непустую таблицу в модель
    model.addTable(new TableTimedModel("test_table", null));
    assertFalse("Returns FALSE on not empty DB model.", model.isEmpty());
   }

 }