package gusevdm.rest;

import org.junit.Test;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;

/**
 * Unit tests for RestClient.
 * @author gusevdm, 1/24/2017.
 */

public class RestClientTest {

    class RestClientChild extends RestClient {

        @Override
        protected String getPath() {
            return "http://localhost:3000";
        }

    }

    private RestClientChild restClientChild = new RestClientChild();

    @Test (expected = IllegalArgumentException.class)
    public void testBuildClient() {
        this.restClientChild.buildClient("/path1\n/path2", new MediaType(), new Cookie("name", "value"));
    }

}
