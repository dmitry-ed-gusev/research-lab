package gusev.dmitry.jtils.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for {@link CmdLine} class.
 * Created by gusevdm on 3/3/2017.
 */

// todo: immutability test!

public class CmdLineTest {

    //@Rule
    //public ExpectedException thrown = ExpectedException.none();

    @Test (expected = IllegalArgumentException.class)
    public void testCreateWithNull() {
        new CmdLine(null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testInvalidArgument1() {
        //thrown.expect(IllegalArgumentException.class); // <- may use it instead of (expected ...)
        new CmdLine(new String[] {"-   "});
    }

    @Test (expected = IllegalArgumentException.class)
    public void testInvalidArgument2() {
        new CmdLine(new String[] {"   -"});
    }

    @Test (expected = IllegalArgumentException.class)
    public void testInvalidArgument3() {
        new CmdLine(new String[] {"  -  "});
    }

    @Test (expected = IllegalArgumentException.class)
    public void testInvalidArgument4() {
        new CmdLine(new String[] {"-"});
    }

    @Test (expected = IllegalArgumentException.class)
    public void testInvalidArgument5() {
        new CmdLine(new String[] {""});
    }

    @Test (expected = IllegalArgumentException.class)
    public void testInvalidArgument6() {
        new CmdLine(new String[] {"     "});
    }

    @Test
    public void testFlagOption() {
        String[] options = {"-flag", "   -flag", "-flag   ", "   -flag   "};
        CmdLine cmdLine;
        for (String option : options) {
            cmdLine = new CmdLine(new String[] {option});
            assertTrue("Should be TRUE!", cmdLine.hasOption("-flag"));
        }
    }

    @Test
    public void testOptionWithValue() {
        String[][] options = {
                {"-name",       "value"},
                {"-name   ",    "value    "},
                {"   -name",    "   value"},
                {"   -name   ", "   value   "}
        };

        CmdLine cmdLine;
        for (String[] oneCmdLine : options) {
            cmdLine = new CmdLine(oneCmdLine);
            assertEquals("Should be equals!", "value", cmdLine.optionValue("-name"));
        }

    }

}
