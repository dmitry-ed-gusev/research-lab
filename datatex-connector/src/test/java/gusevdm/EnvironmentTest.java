package gusevdm;

import org.junit.After;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for {@link Environment} class.
 * @author Serhii Hapii
 */

public class EnvironmentTest {

    private Environment environment = new Environment();

    @After
    public void tearDown() throws Exception {
        //System.clearProperty(Environment.RIVER_TIMEOUT_SECONDS_PROPERTY);
    }

    /*@Test
    public void getRiverTimeoutSeconds() throws Exception {
        System.setProperty(Environment.RIVER_TIMEOUT_SECONDS_PROPERTY, "6");

        long riverTimeoutSeconds = environment.getRiverTimeout(TimeUnit.SECONDS);

        assertEquals("River timeout should be taken from the system property", 6L, riverTimeoutSeconds);
    }
*/
    /*@Test
    public void getRiverTimeoutMilliseconds() throws Exception {
        System.setProperty(Environment.RIVER_TIMEOUT_SECONDS_PROPERTY, "6");

        long riverTimeoutMilliseconds = environment.getRiverTimeout(TimeUnit.MILLISECONDS);

        assertEquals("River timeout should be converted from the system property", 6000L, riverTimeoutMilliseconds);
    }

    @Test
    public void getRiverTimeoutDefault() throws Exception {
        long riverTimeoutSeconds = environment.getRiverTimeout(TimeUnit.SECONDS);

        assertEquals("Default River timeout should be used",
                Environment.DEFAULT_RIVER_TIMEOUT_SECONDS, riverTimeoutSeconds);
    }

    @Test
    public void getRiverTimeoutInvalid() throws Exception {
        System.setProperty(Environment.RIVER_TIMEOUT_SECONDS_PROPERTY, "A");

        long riverTimeoutSeconds = environment.getRiverTimeout(TimeUnit.SECONDS);

        assertEquals("Default River timeout should be used",
                Environment.DEFAULT_RIVER_TIMEOUT_SECONDS, riverTimeoutSeconds);
    }

    @Test
    public void getRiverTimeoutAttempts() throws Exception {
        System.setProperty(Environment.RIVER_TIMEOUT_ATTEMPTS_PROPERTY, "11");

        int riverTimeoutAttempts = environment.getRiverTimeoutAttempts();

        assertEquals("River timeout attempts should be taken from system property", 11, riverTimeoutAttempts);
    }

    @Test
    public void getRiverTimeoutAttemptsDefault() throws Exception {
        int riverTimeoutAttempts = environment.getRiverTimeoutAttempts();

        assertEquals("Default River timeout attempts should be used",
                Environment.DEFAULT_RIVER_TIMEOUT_ATTEMPTS, riverTimeoutAttempts);
    }

    @Test
    public void getRiverTimeoutAttemptsInvalid() throws Exception {
        System.setProperty(Environment.RIVER_TIMEOUT_ATTEMPTS_PROPERTY, "A");

        int riverTimeoutAttempts = environment.getRiverTimeoutAttempts();

        assertEquals("Default River timeout attempts should be used",
                Environment.DEFAULT_RIVER_TIMEOUT_ATTEMPTS, riverTimeoutAttempts);
    }*/

    @Test (expected = IllegalArgumentException.class)
    public void testCheckUrlCR() {
        Environment.checkUrl("http://aaa.\rbb/ccc/ddd?n=v");
    }

    @Test (expected = IllegalArgumentException.class)
    public void testCheckUrlLF() {
        Environment.checkUrl("http://aaa.bb/ccc\n/ddd?n=v");
    }

}
