package dg.social.crawler.utilities;

import org.junit.Test;

import static org.junit.Assert.assertFalse;

/**
 * Unit tests for {@link CmdLine} class.
 * Created by gusevdm on 3/3/2017.
 */

public class CmdLineTest {

    @Test (expected = IllegalArgumentException.class)
    public void testCreateWithNull() {
        new CmdLine(null);
    }

    @Test
    public void testHasEmptyOptions() {
        CmdLine cmdLine = new CmdLine(new String[] {"-   "});
        assertFalse(cmdLine.hasOption("-   "));
    }

}
