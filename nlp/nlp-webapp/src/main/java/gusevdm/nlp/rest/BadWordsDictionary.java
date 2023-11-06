package gusevdm.nlp.rest;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;

import dgusev.io.MyCsvUtils;
import lombok.extern.apachecommons.CommonsLog;

/** Dictionary with bad words. Initialized on application start. */

@CommonsLog
public class BadWordsDictionary {

    private static final String DEFAULT_ENCODING         = "UTF-8";
    private static final String BADWORDS_DICTIONARY_FILE = "badwords.txt";

    private static List<String> badwords;

    /**
     * Initialize bad words dictionary (on application start) with predefined file.
     */
    public static void initDictionary(@Observes @Initialized(ApplicationScoped.class) Object o) {
        log.debug("Starting dictionary initialization.");

        try {
            InputStream input = Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream(BADWORDS_DICTIONARY_FILE);
            // read bad words and put it in unmodifiable collection
            badwords = Collections.unmodifiableList(MyCsvUtils.readCSVFile(input, DEFAULT_ENCODING));

            // some debug
            log.debug(String.format("Initialized bad words list:\n%s", badwords));

        } catch (IOException e) {
            log.error(e);
        }

        log.debug("Dictionary initialized!");
    }

    public List<String> getBadwords() {
        return badwords;
    }

}
