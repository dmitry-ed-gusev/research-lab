package dg.social;

import org.jsoup.nodes.Document;

/**
 * Base abstract class for social networks clients.
 * Created by gusevdm on 1/10/2017.
 */

public class AbstractClient {

    private AbstractClientConfig config;
    private HttpFormRecognizer   formRecognizer;

    /***/
    public AbstractClient(AbstractClientConfig config, HttpFormRecognizer formRecognizer) {

        if (config == null || formRecognizer == null) { // fail-fast
            throw new IllegalArgumentException(
                    String.format("Empty mandatory parameter: config [%s], recognizer [%s]!", config, formRecognizer));
        }

        this.config         = config;
        this.formRecognizer = formRecognizer;
    }

    public AbstractClientConfig getConfig() {
        return config;
    }

    public HttpFormType getHttpFormType(Document document) {
        return this.formRecognizer.getHttpFormType(document);
    }

}
