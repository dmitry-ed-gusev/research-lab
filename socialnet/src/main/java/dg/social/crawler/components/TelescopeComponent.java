package dg.social.crawler.components;

import dg.social.crawler.networks.telescope.TelescopeClient;
import dg.social.crawler.persistence.PeopleDao;
import dg.social.crawler.utilities.CommonUtilities;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Telescope social network/system component.
 * Created by gusevdm on 2/22/2017.
 */

@Component
@Transactional
public class TelescopeComponent {

    private static final Log LOG = LogFactory.getLog(TelescopeComponent.class);

    @Autowired private TelescopeClient telescopeClient;
    @Autowired private PeopleDao       peopleDao;

    /***/
    public void loadTelescopeData(String telescopeCsv) {
        LOG.debug(String.format("TelescopeComponent.loadTelescopeData() is working. File to load [%s].", telescopeCsv));

        if (StringUtils.isBlank(telescopeCsv)) { // fail-fast
            throw new IllegalArgumentException("Can't load data from empty file!");
        }

        //CommonUtilities.unZipIt("people.zip", "");
    }

}
