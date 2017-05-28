package dg.social.crawler.networks.vk;

import dg.social.crawler.networks.HtmlFormRecognizer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import static dg.social.crawler.SCrawlerDefaults.HttpFormType;
import static dg.social.crawler.SCrawlerDefaults.HttpFormType.ACCESS_TOKEN_FORM;
import static dg.social.crawler.SCrawlerDefaults.HttpFormType.ADD_MISSED_DIGITS_FORM;
import static dg.social.crawler.SCrawlerDefaults.HttpFormType.APPROVE_ACCESS_RIGHTS_FORM;
import static dg.social.crawler.SCrawlerDefaults.HttpFormType.LOGIN_FORM;
import static dg.social.crawler.SCrawlerDefaults.HttpFormType.UNKNOWN_FORM;
import static gusev.dmitry.jtils.utils.HttpUtilities.HTTP_FORM_TAG;

/**
 * Recognizer for VK html forms.
 * Created by gusevdm on 1/11/2017.
 */

@Service
public class VkFormsRecognizer implements HtmlFormRecognizer {

    private static final Log LOG = LogFactory.getLog(VkFormsRecognizer.class);

    // Html info elements classes (usually - <div/>)
    private static final String OP_INFO_CLASS_NAME                 = "op_info";
    private static final String FI_ROW_CLASS_NAME                  = "fi_row";
    // login form params
    private static final String LOGIN_FORM_TITLE                   = "Получение доступа к ВКонтакте";
    private static final String LOGIN_FORM_OP_INFO_TEXT            = "Для продолжения Вам необходимо войти ВКонтакте.";
    // app access rights approve form params
    private static final String ACCESS_RIGHTS_FORM_TITLE           = "Получение доступа к ВКонтакте";
    private static final String ACCESS_RIGHTS_FORM_OP_INFO_TEXT    = "запрашивает доступ к Вашему аккаунту";
    // add missed phone number digits form
    private static final String ADD_MISSED_DIGITS_FORM_TITLE       = "Вход | ВКонтакте";
    private static final String ADD_MISSED_DIGITS_FORM_H4_TEXT     = "Проверка безопасности";
    private static final String ADD_MISSED_DIGITS_FORM_FI_ROW_TEXT =
            "Чтобы подтвердить, что Вы действительно являетесь владельцем страницы, пожалуйста, укажите все недостающие цифры номера телефона, к которому привязана страница.";
    // receiving access token form params
    private static final String ACCESS_TOKEN_FORM_TITLE            = "OAuth Blank";

    @Override
    public HttpFormType getHtmlFormType(Document document) {
        LOG.debug("VkFormsRecognizer.getHtmlFormType() working.");

        if (document == null) { // quick check
            LOG.warn("Received document is null!");
            return UNKNOWN_FORM;
        }

        // get form page <title> value
        String formTitle = document.title();
        LOG.debug(String.format("Form title: [%s].", formTitle));

        // get text from first element with op_info class
        Element firstOpInfo = document.body().getElementsByClass(OP_INFO_CLASS_NAME).first();
        String opInfoText = (firstOpInfo == null ? "" : firstOpInfo.text());
        LOG.debug(String.format("DIV by class [%s] text: [%s].", OP_INFO_CLASS_NAME, opInfoText));

        // #1 -> VK login form
        if (LOGIN_FORM_TITLE.equalsIgnoreCase(formTitle) && LOGIN_FORM_OP_INFO_TEXT.equalsIgnoreCase(opInfoText) &&
                !document.getElementsByTag(HTTP_FORM_TAG).isEmpty()) {
            return LOGIN_FORM;
        }

        // #2 -> VK approve rights form (adding new/changing right(s) for application)
        if (ACCESS_RIGHTS_FORM_TITLE.equalsIgnoreCase(formTitle) && ACCESS_RIGHTS_FORM_OP_INFO_TEXT.equalsIgnoreCase(opInfoText) &&
                !document.getElementsByTag(HTTP_FORM_TAG).isEmpty()) {
            return APPROVE_ACCESS_RIGHTS_FORM;
        }

        // #3 -> add missed phone number digits form
        if (ADD_MISSED_DIGITS_FORM_TITLE.equalsIgnoreCase(formTitle)) { // check title
            Elements elementsH4 = document.getElementsByTag("h4");
            if (!elementsH4.isEmpty() && ADD_MISSED_DIGITS_FORM_H4_TEXT.equalsIgnoreCase(elementsH4.first().text())) { // check h4 tag
                // check <div class="fi_row"/> text
                Elements elementsFiRow = document.getElementsByClass(FI_ROW_CLASS_NAME);
                for (Element element : elementsFiRow) {
                    if (ADD_MISSED_DIGITS_FORM_FI_ROW_TEXT.equalsIgnoreCase(element.text())) {
                        return ADD_MISSED_DIGITS_FORM;
                    }
                } // end of FOR
            }
        }

        // #4 -> page/form with access token
        if (ACCESS_TOKEN_FORM_TITLE.equalsIgnoreCase(formTitle) && opInfoText.isEmpty() && document.getElementsByTag(HTTP_FORM_TAG).isEmpty()) {
            return ACCESS_TOKEN_FORM;
        }

        return UNKNOWN_FORM; // can't determine form type
    }

}
