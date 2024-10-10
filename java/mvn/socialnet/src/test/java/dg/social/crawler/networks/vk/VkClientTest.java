package dg.social.crawler.networks.vk;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for VK network client.
 * Created by vinnypuhh on 18.03.17.
 */

public class VkClientTest {

    @Test
    public void testIsTokenValidEmptyToken() {
        assertFalse("Should be false!", VkClient.isVKAccessTokenValid(null));
    }

    @Test
    public void testIsTokenValidEmptyTokenValue() {
        Pair<Date, String> token = new ImmutablePair<>(new Date(), null);
        assertFalse("Should be false!", VkClient.isVKAccessTokenValid(token));
    }

    @Test
    public void testIsTokenValidGoodToken() {
        Pair<Date, String> token = new ImmutablePair<>(new Date(), "abcd");
        assertTrue("Should be true!", VkClient.isVKAccessTokenValid(token));
    }

    @Test
    public void testIsTokenValidInvalidToken() {
        // create a date in past (24h)
        Date pastDate = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000);
        // token with past date
        Pair<Date, String> token = new ImmutablePair<>(pastDate, "abcd");
        assertFalse("Should be false!", VkClient.isVKAccessTokenValid(token));
    }

    // todo: test for Date in future inside a token!
}
