package dg.social.crawler.ok;

import dg.social.crawler.HtmlFormRecognizer;
import dg.social.crawler.HttpFormType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.Map;

import static dg.social.crawler.HttpFormType.ACCESS_TOKEN_FORM;
import static dg.social.crawler.HttpFormType.LOGIN_FORM;
import static dg.social.crawler.HttpFormType.UNKNOWN_FORM;

/**
 * Recognizer for OK http forms.
 * Created by gusevdm on 1/11/2017.
 */

public class OkFormsRecognizer implements HtmlFormRecognizer {

    private static final Log LOG = LogFactory.getLog(OkFormsRecognizer.class);

    // OK LOGIN FORM (mode w - standard)
    private static final String              FORM_LABEL_TAG_NAME     = "label";
    private static final String              FORM_LABEL_TAG_ATTR     = "for";
    // format -> [tag attribute; tag text]
    private static final Map<String, String> FORM_LABEL_VALUES       = new HashMap<String, String> () {{
        put("field_email", "логин, адрес почты или телефон");
        put("field_password", "пароль");
        put("field_remember", "запомнить меня");
    }};

    // OK LOGIN FORM (mode m - mobile)
    private static final String              FORM_PLACEHOLDER_ATTR   = "placeholder";
    private static final String              FORM_ID_ATTR            = "id";
    // format -> [id attr value; placeholder attr value]
    private static final Map<String, String> FORM_PLACEHOLDER_VALUES = new HashMap<String, String> () {{
        put("field_email", "логин, адрес эл. почты или телефон");
        put("field_password", "пароль");
    }};

    // OK ACCESS TOKEN PAGE
    private static final String              ACCESS_TOKEN_PAGE_TITLE = "OAuth Blank";
    private static final String              ACCESS_TOKEN_PAGE_BODY  =
            "Пожалуйста, не копируйте данные из адресной строки для сторонних сайтов. Таким образом Вы можете потерять доступ к Вашему аккаунту.";

    @Override
    public HttpFormType getHtmlFormType(Document document) {
        LOG.debug("OkFormsRecognizer.getHtmlFormType() working.");

        if (document == null) { // quick check
            LOG.warn("Received document is null!");
            return UNKNOWN_FORM;
        }

        // get <label> elements from document
        Elements labelTags = document.getElementsByTag(FORM_LABEL_TAG_NAME);
        LOG.debug(String.format("Form <%s> tags:%n%s.", FORM_LABEL_TAG_NAME, labelTags));
        // get all elements with "placeholder" attributes
        Elements placeholderAttrTags = document.getElementsByAttribute(FORM_PLACEHOLDER_ATTR);
        LOG.debug(String.format("Form tags with attr [%s]:%n%s.", FORM_PLACEHOLDER_ATTR, placeholderAttrTags));
        // get page title
        String pageTitle = document.title();
        LOG.debug(String.format("Got page title [%s].", pageTitle));

        // check for LOGIN_FORM (for standard login window - mode/layout "w")
        if (labelTags.size() == 3) {
            LOG.debug(String.format("Found 3 <%s> tags. Checking for LOGIN FORM (mode w (standard)).", FORM_LABEL_TAG_NAME));
            boolean result = true;
            for (Element element : labelTags) {
                if (!FORM_LABEL_VALUES.get(element.attr(FORM_LABEL_TAG_ATTR)).equalsIgnoreCase(element.text())) {
                    result = false;
                    break;
                }
            }
            if (result) { // we've found login form
                return LOGIN_FORM;
            }
        }

        // check for LOGIN_FORM (for mobile login window - mode/layout "m")
        if (placeholderAttrTags.size() == 2) {
            LOG.debug(String.format("Found 2 tags with [%s] attribute. Checking LOGIN FORM (mode m (mobile)).", FORM_PLACEHOLDER_ATTR));
            boolean result = true;
            for (Element element : placeholderAttrTags) {
                if (!FORM_PLACEHOLDER_VALUES.get(element.attr(FORM_ID_ATTR)).equalsIgnoreCase(element.attr(FORM_PLACEHOLDER_ATTR))) {
                    result = false;
                    break;
                }
            }
            if (result) { // we've found login form
                return LOGIN_FORM;
            }
        }

        // check for ACCESS TOKEN PAGE
        if (ACCESS_TOKEN_PAGE_TITLE.equalsIgnoreCase(pageTitle) && ACCESS_TOKEN_PAGE_BODY.equalsIgnoreCase(document.body().text())) {
            return ACCESS_TOKEN_FORM;
        }

        return UNKNOWN_FORM; // can't determine form type

    }

}
