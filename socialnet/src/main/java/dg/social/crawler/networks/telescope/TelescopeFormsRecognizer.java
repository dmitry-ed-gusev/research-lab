package dg.social.crawler.networks.telescope;

import dg.social.crawler.SCrawlerDefaults;
import dg.social.crawler.networks.HtmlFormRecognizer;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

/**
 * Recognizer for Telescope html forms.
 * Created by gusevdm on 3/17/2017.
 */

@Service
public class TelescopeFormsRecognizer implements HtmlFormRecognizer {

    @Override
    public SCrawlerDefaults.HttpFormType getHtmlFormType(Document document) {
        // todo: method implementation!
        throw new IllegalStateException("Method not implemented yet!");
    }

}
