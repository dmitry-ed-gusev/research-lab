package dg.bigdata.hw2;

import org.junit.Test;

import java.text.ParseException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Unit tests for IPinYou bidding count app.
 * Created by gusevdm on 5/17/2017.
 */

public class IPinYouTest {

    // some example data
    private static final String RECORD_EMPTY_IPINYOUID     = "65a705c8ef61b9acc04e88a4a1d56aab	20130606000708852	null	" +
            "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0; QQDownload 714),gzip(gfe),gzip(gfe)	" +
            "120.69.224.*	374	380	2	trqRTudNQquvFs1o5SqfNX	9345668d6f5a7e27e33482b453d3e2bc		1408060206	336	280	1	" +
            "0	5	77819d3e0b3467fe5c7b16d68ad923a1	300	1458	null";
    private static final String RECORD_NOT_EMPTY_IPINYOUID = "f91603151f2c41f988e7866bfb9538a2	20130606000708853	" +
            "VhTnPaf9OHb8MsR	Mozilla/5.0 (Windows NT 5.1) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/21.0.1180.89 Safari/537.1,gzip(gfe)	" +
            "114.86.187.*	79	79	2	trqRTuN-XIuc1mKYUV	b9b27f86cd5409b505b66e27470765af		1709226045	728	90	1	0	5	" +
            "48f2e9ba15708c0146bda5e1dd653caa	300	1458	null";
    private static final String RECORD_NO_IPINYOUID        = "zzzzzzzzzzzz xxxxxxxxx";

    @Test (expected = IllegalArgumentException.class)
    public void testGetIPinYouIdNullRecord() throws ParseException {
        IPinYou.getIPinYouId(null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testGetIPinYouIdEmptyRecord() throws ParseException {
        IPinYou.getIPinYouId("    ");
    }

    @Test (expected = ParseException.class)
    public void testGetIPinYouIdNoID() throws ParseException {
        IPinYou.getIPinYouId(RECORD_NO_IPINYOUID);
    }

    @Test
    public void testGetIPinYouId() throws ParseException {
        assertEquals("IPinYouIDs should be equal!", "VhTnPaf9OHb8MsR", IPinYou.getIPinYouId(RECORD_NOT_EMPTY_IPINYOUID));
    }

    @Test
    public void testGetIPinYouIdNullId() throws ParseException {
        assertNull("IPinYouIDs should be NULL!", IPinYou.getIPinYouId(RECORD_EMPTY_IPINYOUID));
    }

}
