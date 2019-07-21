package dg.social.crawler.utilities;

import gusev.dmitry.jtils.spring.CustomStringProperty;
import gusev.dmitry.utils.CmdLine;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;

import static dg.social.crawler.utilities.CmdLineOption.OUTPUT_FORCE;

/**
 * Some specific useful utilities.
 * Created by gusevdm on 12/22/2016.
 */

public final class CommonUtilities {

    private static final Log LOG = LogFactory.getLog(CommonUtilities.class); // module logger

    private static final String CUSTOM_PROPERTY_NAME = "custom_%s";

    private CommonUtilities() {
    } // can't instantiate

    /***/
    public static List<CustomStringProperty> getCustomPropertiesList(CmdLine cmdLine) {
        LOG.debug("SocialCrawler.getCustomProperties() is working [PRIVATE].");

        if (cmdLine == null) {
            throw new IllegalArgumentException("Can't create custom properties from NULL cmd line!");
        }

        List<CustomStringProperty> result = new ArrayList<>();

        // iterate over options and create custom
        // todo: add cmd line option to logger (like: new value for option ....)
        for (CmdLineOption option : CmdLineOption.values()) {

            if (!StringUtils.isBlank(option.getOptionKey())) { // process only config options

                String optionValue = cmdLine.optionValue(option.getOptionName());
                String customPropertyName = String.format(CUSTOM_PROPERTY_NAME, option.getOptionKey());

                if (!StringUtils.isBlank(optionValue)) { // value isn't empty (present)
                    //LOG.info(String.format(""));
                    LOG.debug(String.format("Creating custom property [%s]: name [%s], value [%s].",
                            customPropertyName, option.getOptionKey(), optionValue));
                    result.add(new CustomStringProperty(customPropertyName, option.getOptionKey(), optionValue));

                } else if (OUTPUT_FORCE.equals(option)) { // one option is a flag

                    if (cmdLine.hasOption(option.getOptionName())) {
                        LOG.debug(String.format("Set value [TRUE] for flag [%s].", option));
                        result.add(new CustomStringProperty(customPropertyName, option.getOptionKey(), String.valueOf(true)));
                    } else {
                        LOG.debug(String.format("There is no new value for flag [%s].", option));
                    }

                } else { // no value and not a flag
                    LOG.debug(String.format("There is no new value for option [%s].", option));
                }

            }

        } // end of FOR

        return result;
    }

}
