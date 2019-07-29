package dgusev.utils;

import dgusev.utils.MyHttpUtils;
import org.junit.Test;

/**
 * Unit tests for MyHttpUtils module.
 * Created by Dmitrii_Gusev on 7/24/2017.
 */

// todo: implement test cases for all methods!

public class MyHttpUtilsTest {

    @Test(expected = IllegalArgumentException.class)
    public void testCheckUrlCR() {
        MyHttpUtils.checkUrl("http://aaa.\rbb/ccc/ddd?n=v");
    }

    @Test (expected = IllegalArgumentException.class)
    public void testCheckUrlLF() {
        MyHttpUtils.checkUrl("http://aaa.bb/ccc\n/ddd?n=v");
    }

}
