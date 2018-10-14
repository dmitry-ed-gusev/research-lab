package jdb.config;

import jdb.config.connection.BaseDBConfigTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Тестовая сюита (набор тестов) для тестирования классов конфигурирования (пакет jdb.config).
 * @author Gusev Dmitry (Гусев Дмитрий)
 * @version 1.0 (DATE: 10.03.11)
*/

@RunWith(value= Suite.class)
@Suite.SuiteClasses(value={BaseDBConfigTest.class})
public class JdbConfigTestSuite
 {
 }
