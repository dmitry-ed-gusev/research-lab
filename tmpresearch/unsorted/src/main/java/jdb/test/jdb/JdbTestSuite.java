package jdb;

/**
 * Главный модуль юнит-тестирования библиотеки JDB. Модуль представляет из себя сюиту, объединяющую другие, более
 * простые тестовые сюиты и юнит-тесты.
 * @author Gusev Dmitry (Гусев Дмитрий)
 * @version 1.0 (DATE: 03.03.11)
*/

import jdb.model.JdbModelsTestSuite;
import jdb.utils.DBUtilsTest;
import jdb.utils.helpers.JdbcUrlHelperTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(value=Suite.class)
@SuiteClasses(value={DBUtilsTest.class, JdbcUrlHelperTest.class, JdbModelsTestSuite.class})
public class JdbTestSuite
 {
 }