package dg.social.crawler.utilities;

import org.junit.Test;

/**
 * Tests for {@link CustomStringProperty} class.
 * Created by gusevdm on 3/3/2017.
 */
public class CustomStringPropertyTest {

    @Test (expected = IllegalArgumentException.class)
    public void testCreateNullPropertyName() {
        new CustomStringProperty(null, "name", "value");
    }

    @Test (expected = IllegalArgumentException.class)
    public void testCreateNullName() {
        new CustomStringProperty("property", "", "value");
    }

}
