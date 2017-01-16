package dg.social;

import org.jsoup.nodes.Document;

/**
 * Common interface for http form recognizer.
 * Created by gusevdm on 1/11/2017.
 */

public interface HtmlFormRecognizer {

    /***/
    HttpFormType getHtmlFormType(Document document);

}
