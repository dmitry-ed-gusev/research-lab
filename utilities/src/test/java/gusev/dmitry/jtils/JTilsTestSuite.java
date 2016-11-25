package gusev.dmitry.jtils;

import gusev.dmitry.jtils.graph.typeIII.WeightedDigraphTest;
import gusev.dmitry.jtils.graph.typeIII.WeightedEdgeTest;
import gusev.dmitry.jtils.net.IPAddressValidatorTest;
import gusev.dmitry.jtils.utils.CommonUtilsTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Test suite for JUnit tests for [jtils] module.
 * @author Gusev Dmitry (gusevd)
 * @version 1.0 (DATE: 21.08.2014)
*/

@RunWith(value=Suite.class)
@Suite.SuiteClasses(value={CommonUtilsTest.class, WeightedEdgeTest.class, WeightedDigraphTest.class,
        IPAddressValidatorTest.class})
public class JTilsTestSuite {}
