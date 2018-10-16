package gusev.dmitry.jtils.utils;

import org.junit.Test;

/**
 * Unit tests for HttpUtils module.
 * Created by Dmitrii_Gusev on 7/24/2017.
 */

// todo: implement test cases for all methods!

public class HttpUtilsTest {

    @Test(expected = IllegalArgumentException.class)
    public void testCheckUrlCR() {
        HttpUtils.checkUrl("http://aaa.\rbb/ccc/ddd?n=v");
    }

    @Test (expected = IllegalArgumentException.class)
    public void testCheckUrlLF() {
        HttpUtils.checkUrl("http://aaa.bb/ccc\n/ddd?n=v");
    }

}
