package jdb;

/**
 * ������� ������ ����-������������ ���������� JDB. ������ ������������ �� ���� �����, ������������ ������, �����
 * ������� �������� ����� � ����-�����.
 * @author Gusev Dmitry (����� �������)
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