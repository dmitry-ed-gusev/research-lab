import gusev.dmitry.research.math.FactorialTest;
import gusev.dmitry.research.utils.rdictionary.ReverseDictionaryTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * @author Gusev Dmitry (gusevd)
 * @version 1.0 (DATE: 05.10.12)
 */

@RunWith(value=Suite.class)
@Suite.SuiteClasses(value={FactorialTest.class, ReverseDictionaryTest.class})
public class TestSuite {
}
