package dg.social.crawler.components;

import dg.social.crawler.networks.telescope.TelescopeClient;
import dg.social.crawler.persistence.PeopleDao;
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
    public void loadTelescopeData() {
        LOG.debug("TelescopeComponent.loadTelescopeData() is working.");
    }

}
