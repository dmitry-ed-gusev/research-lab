package dg.social.vk;

import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Sample client for VK API.
 * Created by gusevdm on 12/5/2016.
 */

public class VkClient {

    public static void main(String[] args) {

        Log log = LogFactory.getLog(VkClient.class);
        log.info("VkClient starting.");

        // creating vk api client object
        TransportClient transportClient = HttpTransportClient.getInstance();
        VkApiClient vk = new VkApiClient(transportClient);


    }

}
