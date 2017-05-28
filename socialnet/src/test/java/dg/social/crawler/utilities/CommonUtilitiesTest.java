package dg.social.crawler.utilities;

import gusev.dmitry.jtils.utils.CmdLine;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Unit tests for {@link CommonUtilities} class.
 * Created by gusevdm on 3/3/2017.
 */

public class CommonUtilitiesTest {

    @Mock
    CmdLine cmdLine;

    @Before
    public void beforeTest() {
        initMocks(this);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testPropertiesListNullCmdLine() {
        CommonUtilities.getCustomPropertiesList(null);
    }

    @Test
    public void testPropertiesListSize() {
        when(this.cmdLine.optionValue(anyString())).thenReturn("value");
        List<CustomStringProperty> list = CommonUtilities.getCustomPropertiesList(this.cmdLine);
        assertEquals("Invalid list size!", 6, list.size());
    }

    @Test
    public void testPropertiesListOneOption() {
        when(this.cmdLine.optionValue("-config")).thenReturn("config.cfg");

        // #1 - check list size
        List<CustomStringProperty> list = CommonUtilities.getCustomPropertiesList(this.cmdLine);
        assertEquals("Invalid list size!", 1, list.size());

        // #2 - check property
        CustomStringProperty property = list.get(0);
        assertEquals("Invalid property name!", "crawler.config", property.getPropertyName());
        assertEquals("Invalid property value!", "config.cfg", property.getProperty("crawler.config"));
    }

    @Test
    public void testPropertiesListOneFlag() {
        // todo: implement like testPropertiesListOneOption() test!
    }

    @Test
    public void testParseStringArrayEmptyArray() {
        Set<String> emptySet = new HashSet<>();

        // sample data
        List<String> samples = new ArrayList<String>() {{
            add(null);
            add("");
            add("    ");
        }};
        // tests
        samples.forEach(array -> {
            Set<String> result = CommonUtilities.parseStringArray(array);
            assertNotNull("Shouldn't be NULL!", result);
            assertEquals("Size should be 0!", 0, result.size());
            assertEquals("Should be empty set!", emptySet, result);
        });
    }

    @Test
    public void testParseStringArrayValidValue() {
        // sample source data
        List<String> samples = new ArrayList<String>() {{
            // spaces
            add("['value']");
            add("[    'value']");
            add("['value'    ]");
            add("[   'value'    ]");
            add("   ['value']");
            add("['value']   ");
            add("   ['value']   ");
            add("  ['   value']  ");
            add("  ['value   ']  ");
            add("  ['   value   ']  ");
            add("  [    '   value   '    ]  ");
            // duplicates
            add("   [ '    value'    ,     'value    '  ] ");
            add("['value','value']");
            // empty values
            add("['value', '']");
            add("  [  '      ' ,   ' value   ', '', '  ']");
        }};

        // expected result
        Set<String> expected = new HashSet<String>() {{
            add("value");
        }};

        // tests
        samples.forEach(array -> {
            Set<String> actual = CommonUtilities.parseStringArray(array);
            assertEquals("Size should be 1!", 1, actual.size());
            assertEquals("Should be equals!", expected, actual);
        });
    }

    @Test
    public void testParseStringArrayValidValues() {
        // sample source data
        List<String> samples = new ArrayList<String>() {{
            add("['value1', 'value2', 'd\"value3']");
            add("[ '   value1', 'value2   ', '  d\"value3', '  ']");
            add("['', '   ', ' value1', 'value2', 'd\"value3', '   ']");
        }};

        // expected result
        Set<String> expected = new HashSet<String>() {{
            add("value1");
            add("value2");
            add("d\"value3");
        }};

        // tests
        samples.forEach(array -> {
            Set<String> actual = CommonUtilities.parseStringArray(array);
            assertEquals("Size should be 3!", 3, actual.size());
            assertEquals("Should be equals!", expected, actual);
        });
    }

}
