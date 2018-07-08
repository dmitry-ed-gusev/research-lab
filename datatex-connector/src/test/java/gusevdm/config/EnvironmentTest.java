package gusevdm.config;

import gusevdm.config.Environment;
import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for {@link Environment} class.
 * @author Serhii Hapii
 */

public class EnvironmentTest {

    //private Environment environment = new Environment();

    @After
    public void tearDown() throws Exception {
        //System.clearProperty(Environment.RIVER_TIMEOUT_SECONDS_PROPERTY);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testCheckUrlCR() {
        Environment.checkUrl("http://aaa.\rbb/ccc/ddd?n=v");
    }

    @Test (expected = IllegalArgumentException.class)
    public void testCheckUrlLF() {
        Environment.checkUrl("http://aaa.bb/ccc\n/ddd?n=v");
    }

}
