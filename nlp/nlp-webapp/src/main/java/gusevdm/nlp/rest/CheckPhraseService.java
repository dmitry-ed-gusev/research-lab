package gusevdm.nlp.rest;

import gusev.dmitry.jtils.nlp.NLPUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * RESTful service for checking phrases. Gets POST requests in UTF-8 and answers with JSON.
 */
@Path("/check")
@Stateless
public class CheckPhraseService {

    private static final Log LOG = LogFactory.getLog(CheckPhraseService.class);

    @Inject
    BadWordsDictionary dictionary;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public CheckPhraseAnswer checkPhrase(String phrase) {
        LOG.debug(String.format("Checking phrase [%s].", phrase));

        CheckPhraseAnswer answer = null;

        if (StringUtils.isBlank(phrase)) {
            answer = new CheckPhraseAnswer(-1, "Empty check phrase!");
        } else {
            for (String word: StringUtils.split(NLPUtils.cleanString(phrase))) {
                if (NLPUtils.in(NLPUtils.fixRussianWord(word), true, dictionary.getBadwords().toArray(new String[0]))) {
                    answer = new CheckPhraseAnswer(1, "Contains stop words!");
                    break;
                }
            } // end of for

        }

        // nothing found
        if (answer == null) {
            answer = new CheckPhraseAnswer(0, "OK");
        }

        // some debug
        LOG.debug(String.format("Check result: %s", answer.toString()));

        return answer;
    }

}
