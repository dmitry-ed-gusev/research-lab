package jdb.model;

import jdb.model.dto.DBDTOModelTest;
import jdb.model.integrity.DBIntegrityModelTest;
import jdb.model.structure.DBStructureModelTest;
import jdb.model.time.DBTimedModelTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * �������� ����� (����� ������) ��� ������� ���������� ������� ��, ������, �����.
 * @author Gusev Dmitry (����� �������)
 * @version 1.0 (DATE: 17.03.11)
*/

@RunWith(value=Suite.class)
@Suite.SuiteClasses(value={DBTimedModelTest.class, DBStructureModelTest.class, DBIntegrityModelTest.class,
                           DBDTOModelTest.class})
public class JdbModelsTestSuite
 {
 }