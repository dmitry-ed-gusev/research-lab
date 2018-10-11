package jdb.model;

import jdb.model.dto.DBDTOModelTest;
import jdb.model.integrity.DBIntegrityModelTest;
import jdb.model.structure.DBStructureModelTest;
import jdb.model.time.DBTimedModelTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Тестовая сюита (набор тестов) для классов логических моделей БД, таблиц, полей.
 * @author Gusev Dmitry (Гусев Дмитрий)
 * @version 1.0 (DATE: 17.03.11)
*/

@RunWith(value=Suite.class)
@Suite.SuiteClasses(value={DBTimedModelTest.class, DBStructureModelTest.class, DBIntegrityModelTest.class,
                           DBDTOModelTest.class})
public class JdbModelsTestSuite
 {
 }