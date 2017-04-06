package dg.social.crawler.components;

import dg.social.crawler.domain.PersonDto;
import dg.social.crawler.networks.telescope.TelescopeClient;
import dg.social.crawler.networks.telescope.TelescopeParser;
import dg.social.crawler.persistence.PeopleDao;
import dg.social.crawler.utilities.CommonUtilities;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Paths;
import java.util.List;

/**
 * Telescope social network/system component.
 * Created by gusevdm on 2/22/2017.
 */

@Component
@Transactional
public class TelescopeComponent {

    private static final Log LOG = LogFactory.getLog(TelescopeComponent.class);

    private static final String ZIP_EXTENSION = ".zip";
    private static final String CSV_EXTENSION = ".csv";

    @Autowired private TelescopeClient telescopeClient;
    @Autowired private PeopleDao       peopleDao;

    /***/
    public void loadTelescopeData(String telescopeCsv) {
        LOG.debug(String.format("TelescopeComponent.loadTelescopeData() is working. File to load [%s].", telescopeCsv));

        if (StringUtils.isBlank(telescopeCsv)) { // fail-fast
            throw new IllegalArgumentException("Can't load data from empty file!");
        }

        String csvFileName;
        // unzip Telescope CSV, if necessary, and change file extension
        if (telescopeCsv.trim().toLowerCase().endsWith(ZIP_EXTENSION)) {
            LOG.debug(String.format("Unzipping file [%s] to current folder.", telescopeCsv));
            CommonUtilities.unZipIt(telescopeCsv, ""); // output to current folder
            // get filename from path and change extension zip -> csv
            csvFileName = Paths.get(telescopeCsv).getFileName().toString();
            csvFileName = csvFileName.substring(0, csvFileName.lastIndexOf('.')) + CSV_EXTENSION;
        } else {
            csvFileName = telescopeCsv;
        }

        if (!csvFileName.trim().toLowerCase().endsWith(CSV_EXTENSION)) { // fail-fast check of file extension
            throw new IllegalArgumentException(String.format("Invalid extension for input file [%s]!", csvFileName));
        }

        // parse and load data from CSV file
        List<PersonDto> people = TelescopeParser.parseTelescopeCSV(csvFileName);
        LOG.debug(String.format("Got list of size [%s] from Telescope.", people.size()));

        // load/update data in Crawler's DB (if there are data)
        if (!people.isEmpty()) {
            LOG.debug("Persisting people list from Telescope.");
            this.peopleDao.loadPeople(people);
        } else {
            LOG.warn("People list from Telescope is empty! Nothing to persist!");
        }

    }

}
