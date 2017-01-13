package dg.social.ok;

import dg.social.HttpFormRecognizer;
import dg.social.HttpFormType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.Map;

import static dg.social.HttpFormType.LOGIN_FORM;
import static dg.social.HttpFormType.UNKNOWN_FORM;

/**
 * Recognizer for OK http forms.
 * Created by gusevdm on 1/11/2017.
 */

public class OkFormsRecognizer implements HttpFormRecognizer {

    private static final Log LOG = LogFactory.getLog(OkFormsRecognizer.class);

    private static final String              FORM_LABEL_TAG_NAME      = "label";
    private static final String              FORM_LABEL_TAG_ATTRIBUTE = "for";
    private static final Map<String, String> FORM_LABEL_VALUES        = new HashMap<String, String> () {{
        put("field_email", "логин, адрес почты или телефон");
        put("field_password", "пароль");
        put("field_remember", "запомнить меня");
    }};

    @Override
    public HttpFormType getHttpFormType(Document document) {
        LOG.debug("OkFormsRecognizer.getHttpFormType() working.");

        if (document == null) { // quick check
            LOG.warn("Received document is null!");
            return UNKNOWN_FORM;
        }

        // get <label> elements from document
        Elements elements = document.getElementsByTag(FORM_LABEL_TAG_NAME);
        LOG.debug(String.format("Form <%s> tags:%n%s.", FORM_LABEL_TAG_NAME, elements));

        // check for LOGIN_FORM (for standard login window - mode/layout "w")
        if (elements.size() == 3) {
            LOG.debug(String.format("Found 3 <%s> tags. Checking for LOGIN FORM.", FORM_LABEL_TAG_NAME));
            boolean result = true;
            for (Element element : elements) {
                if (!FORM_LABEL_VALUES.get(element.attr(FORM_LABEL_TAG_ATTRIBUTE)).equalsIgnoreCase(element.text())) {
                    result = false;
                    break;
                }
            }
            if (result) { // we've found login form
                return LOGIN_FORM;
            }
        }

        // check for LOGIN_FORM (for mobile login window - mode/layout "m")
        if (true) {

        }

        return UNKNOWN_FORM; // can't determine form type

    }

}
