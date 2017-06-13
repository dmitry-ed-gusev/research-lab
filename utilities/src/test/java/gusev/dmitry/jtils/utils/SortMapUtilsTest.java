package gusev.dmitry.jtils.utils;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Unit tests for {@link SortMapUtils} module.
 * Created by gusevdm on 6/13/2017.
 */
public class SortMapUtilsTest {

    @Test
    public void testSortByValue2()
    {
        Random random = new Random(System.currentTimeMillis());
        Map<String, Integer> testMap = new HashMap<String, Integer>(1000);
        for(int i = 0 ; i < 1000 ; ++i) {
            testMap.put( "SomeString" + random.nextInt(), random.nextInt());
        }

        testMap = SortMapUtils.sortByValue2( testMap );
        Assert.assertEquals( 1000, testMap.size() );

        Integer previous = null;
        for(Map.Entry<String, Integer> entry : testMap.entrySet()) {
            Assert.assertNotNull( entry.getValue() );
            if (previous != null) {
                Assert.assertTrue( entry.getValue() >= previous );
            }
            previous = entry.getValue();
        }
    }

}
