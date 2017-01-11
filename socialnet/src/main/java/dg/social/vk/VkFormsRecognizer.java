package dg.social.vk;

import dg.social.HttpFormRecognizer;
import dg.social.HttpFormType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import static dg.social.HttpFormType.*;
import static dg.social.utilities.HttpUtilities.HTTP_FORM_TAG;

/**
 * Recognizer for VK http forms.
 * Created by gusevdm on 1/11/2017.
 */

// todo: implement "add missed digits" form recognizing

public class VkFormsRecognizer implements HttpFormRecognizer {

    private static final Log LOG = LogFactory.getLog(VkFormsRecognizer.class);

    // Html info elements class (usually - <div/>)
    private static final String OP_INFO_CLASS_NAME              = "op_info";
    // login form params
    private static final String LOGIN_FORM_TITLE                = "Получение доступа к ВКонтакте";
    private static final String LOGIN_FORM_OP_INFO_TEXT         = "Для продолжения Вам необходимо войти ВКонтакте.";
    // app access rights approve form params
    private static final String ACCESS_RIGHTS_FORM_TITLE        = "Получение доступа к ВКонтакте";
    private static final String ACCESS_RIGHTS_FORM_OP_INFO_TEXT = "запрашивает доступ к Вашему аккаунту";
    // receiving access token form params
    private static final String ACCESS_TOKEN_FORM_TITLE         = "OAuth Blank";
    //private static final String ACCESS_TOKEN_FORM_OP_INFO_TEXT  = "";

    @Override
    public HttpFormType getHttpFormType(Document document) {
        LOG.debug("VkFormsRecognizer.getHttpFormType() working.");

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

        // if title match and there is div with specified class - we've found
        if (LOGIN_FORM_TITLE.equalsIgnoreCase(formTitle) && LOGIN_FORM_OP_INFO_TEXT.equalsIgnoreCase(opInfoText) &&
                !document.getElementsByTag(HTTP_FORM_TAG).isEmpty()) {
            return LOGIN_FORM;
        }

        // approve rights form (adding new right for application)
        if (ACCESS_RIGHTS_FORM_TITLE.equalsIgnoreCase(formTitle) && ACCESS_RIGHTS_FORM_OP_INFO_TEXT.equalsIgnoreCase(opInfoText) &&
                !document.getElementsByTag(HTTP_FORM_TAG).isEmpty()) {
            return APPROVE_ACCESS_RIGHTS_FORM;
        }

        // page with access token
        if (ACCESS_TOKEN_FORM_TITLE.equalsIgnoreCase(formTitle) && opInfoText.isEmpty() && document.getElementsByTag(HTTP_FORM_TAG).isEmpty()) {
            return ACCESS_TOKEN_FORM;
        }

        return UNKNOWN_FORM; // can't determine form type
    }

}
