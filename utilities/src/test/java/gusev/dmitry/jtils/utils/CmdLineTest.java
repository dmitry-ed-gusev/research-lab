package gusev.dmitry.jtils.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for {@link CmdLine} class.
 * Created by gusevdm on 3/3/2017.
 */

// todo: add tests for help/description

public class CmdLineTest {

    private static String     FLAG              = "-flag";
    private static String     KEY               = "-name";
    private static String     VALUE             = "value";
    // cmd line with flags
    private static String[]   FLAGS_OPTIONS     = {"-flag", "   -flag", "-flag   ", "   -flag   "};
    // cmd line with key-values
    private static String[][] KEY_VALUE_OPTIONS = {
            {"-name", "value"},
            {"-name   ", "value    "},
            {"   -name", "   value"},
            {"   -name   ", "   value   "}
    };
    // cmd line options enum
    private enum Option implements CmdLineOption {
        FLAG_OPTION     ("-flag", "flag description"),
        KEY_VALUE_OPTION("-name", "name description");

        private String optionName;
        private String optionDesc;

        Option(String optionName, String optionDesc) {
            this.optionName = optionName;
            this.optionDesc = optionDesc;
        }

        @Override
        public String getName() {
            return this.optionName;
        }

        @Override
        public String getDescription() {
            return this.optionDesc;
        }

    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateWithNull() {
        new CmdLine(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidArgument1() {
        //thrown.expect(IllegalArgumentException.class); // <- may use it instead of (expected ...)
        new CmdLine(new String[]{"-   "});
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidArgument2() {
        new CmdLine(new String[]{"   -"});
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidArgument3() {
        new CmdLine(new String[]{"  -  "});
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidArgument4() {
        new CmdLine(new String[]{"-"});
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidArgument5() {
        new CmdLine(new String[]{""});
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidArgument6() {
        new CmdLine(new String[]{"     "});
    }

    @Test
    public void testCmdLineImmutability() {

        // prepare cmd line
        String[] args = {"-flag1", "-key", "value", "-flag2"};
        CmdLine cmdLine = new CmdLine(args);

        // change source array for cmd line
        String option = "-zzz";
        args[0] = option;

        // test
        assertFalse("Should be FALSE!", cmdLine.hasOption(option));
    }

    @Test
    public void testFlagOptionString() {
        CmdLine cmdLine;
        for (String option : FLAGS_OPTIONS) {
            cmdLine = new CmdLine(new String[]{option});
            assertTrue("Should be TRUE!", cmdLine.hasOption(FLAG));
        }
    }

    @Test
    public void testFlagOption() {
        CmdLine cmdLine;
        for (String option : FLAGS_OPTIONS) {
            cmdLine = new CmdLine(new String[]{option});
            assertTrue("Should be TRUE!", cmdLine.hasOption(Option.FLAG_OPTION));
        }
    }

    @Test
    public void testNullAndEmptyFlag() {
        CmdLine cmdLine = new CmdLine(FLAGS_OPTIONS);
        assertFalse("Should be FALSE!", cmdLine.hasOption((String) null));
        assertFalse("Should be FALSE!", cmdLine.hasOption((Option) null));
        assertFalse("Should be FALSE!", cmdLine.hasOption(""));
        assertFalse("Should be FALSE!", cmdLine.hasOption("    "));
    }

    @Test
    public void testKeyValueOptionString() {
        CmdLine cmdLine;
        for (String[] oneCmdLine : KEY_VALUE_OPTIONS) {
            cmdLine = new CmdLine(oneCmdLine);
            assertEquals("Should be equals!", VALUE, cmdLine.optionValue(KEY));
        }
    }

    @Test
    public void testKeyValueOption() {
        CmdLine cmdLine;
        for (String[] oneCmdLine : KEY_VALUE_OPTIONS) {
            cmdLine = new CmdLine(oneCmdLine);
            assertEquals("Should be equals!", VALUE, cmdLine.optionValue(Option.KEY_VALUE_OPTION));
        }
    }

    @Test
    public void testNullAndEmptyKeyKey() {
        CmdLine cmdLine = new CmdLine(FLAGS_OPTIONS);
        assertNull("Should be NULL!", cmdLine.optionValue((String) null));
        assertNull("Should be NULL!", cmdLine.optionValue((Option) null));
        assertNull("Should be NULL!", cmdLine.optionValue(""));
        assertNull("Should be NULL!", cmdLine.optionValue("    "));
    }

}
