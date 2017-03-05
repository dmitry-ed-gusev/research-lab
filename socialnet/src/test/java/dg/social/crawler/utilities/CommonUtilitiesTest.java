package dg.social.crawler.utilities;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;

import static org.junit.Assert.assertEquals;
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
        assertEquals("Invalid list size!", 5, list.size());
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

}
