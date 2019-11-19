package dgusev.spring;

import org.junit.Test;

/**
 * Tests for {@link CustomStringProperty} class.
 * Created by gusevdm on 3/3/2017.
 */
public class CustomStringPropertyTest {

    @Test (expected = IllegalArgumentException.class)
    public void testCreateNullPropertySourceName() {
        new CustomStringProperty(null, "name", "value");
    }

    @Test (expected = IllegalArgumentException.class)
    public void testCreateNullPropertyName() {
        new CustomStringProperty("property", null, "value");
    }

    @Test (expected = IllegalArgumentException.class)
    public void testCreateEmptyPropertyName() {
        new CustomStringProperty("property", "", "value");
    }

}
