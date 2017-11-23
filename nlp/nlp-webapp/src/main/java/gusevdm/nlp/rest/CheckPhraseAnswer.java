package gusevdm.nlp.rest;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Answer call for rest service. Will be marshalled into JSON for sending to client.
 */
public class CheckPhraseAnswer {

    private int    code;
    private String message;

    /***/
    public CheckPhraseAnswer(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("code", code)
                .append("message", message)
                .toString();
    }

}
