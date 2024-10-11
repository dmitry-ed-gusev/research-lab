package jdb.model.dto;

import jdb.exceptions.DBModelException;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Модуль юнит-тестов для класса-модели БД (модель БД с данными - DTO).
 * @author Gusev Dmitry (Гусев Дмитрий)
 * @version 1.0 (DATE: 21.03.11)
*/

public class DBDTOModelTest
 {
  @Test
  public void isEmptyTest() throws DBModelException
   {
    DBDTOModel model = new DBDTOModel("test_db");
    // Пустая модель БД
    assertTrue("On empty DB model must return TRUE.", model.isEmpty());
    // Добавим пустой список таблиц в модель БД
    ArrayList<TableDTOModel> tables = new ArrayList<TableDTOModel>();
    model.setTables(tables);
    assertTrue("On DB model with empty tables list returns TRUE.", model.isEmpty());
    // Добавим непустую таблицу в модель
    model.addTable(new TableDTOModel("test_table"));
    assertFalse("Returns FALSE on not empty DB model.", model.isEmpty());
   }
 }