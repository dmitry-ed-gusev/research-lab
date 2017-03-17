package dg.social.crawler.networks.telescope;

import dg.social.crawler.domain.PersonDto;
import dg.social.crawler.networks.ParserInterface;
import dg.social.crawler.utilities.CommonUtilities;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static dg.social.crawler.SCrawlerDefaults.DEFAULT_ENCODING;
import static dg.social.crawler.SCrawlerDefaults.SocialNetwork.TELESCOPE;

/**
 * Parses CSV files from Telescope (people info).
 * Created by gusevdm on 2/20/2017.
 */

public class TelescopeParser implements ParserInterface {

    private static Log LOG = LogFactory.getLog(TelescopeParser.class);

    //private static final JSONParser JSON_PARSER       = new JSONParser();
    private static final String TELESCOPE_CSV_ENCODING      = "Windows-1251";

    // header fields for Telescope output file
    private static final String TELESCOPE_ID                = "id";
    private static final String TELESCOPE_ANNIVERSARY_DATE  = "anniversaryDate";
    private static final String TELESCOPE_CITY              = "city";
    private static final String TELESCOPE_CITY_SUM          = "citySum";
    private static final String TELESCOPE_COUNTRY           = "country";
    private static final String TELESCOPE_COUNTRY_SUM       = "countrySum";
    private static final String TELESCOPE_DISPLAY_NAME      = "displayName";
    private static final String TELESCOPE_EDUCATION         = "education";
    private static final String TELESCOPE_EMAIL             = "email";
    private static final String TELESCOPE_EMPLOYMENT_STATUS = "employmentStatus";
    private static final String TELESCOPE_END_WORK_DATE     = "endWorkDate";
    private static final String TELESCOPE_FIRST_NAME        = "firstName";
    private static final String TELESCOPE_FULL_NAME         = "fullName";
    private static final String TELESCOPE_LAST_NAME         = "lastName";
    private static final String TELESCOPE_LEVEL             = "level";
    private static final String TELESCOPE_NATIVE_NAME       = "nativeName";
    private static final String TELESCOPE_OFFICE            = "office";
    private static final String TELESCOPE_PHONE             = "phone";
    private static final String TELESCOPE_PHONES            = "phones";
    private static final String TELESCOPE_SENIORITY         = "seniority";

    // constructing the header array for Telescope output file
    private static final String[] FILE_HEADER = {
            TELESCOPE_ID, TELESCOPE_ANNIVERSARY_DATE, TELESCOPE_CITY, TELESCOPE_CITY_SUM, TELESCOPE_COUNTRY,
            TELESCOPE_COUNTRY_SUM, TELESCOPE_DISPLAY_NAME, TELESCOPE_EDUCATION, TELESCOPE_EMAIL,
            TELESCOPE_EMPLOYMENT_STATUS, TELESCOPE_END_WORK_DATE, TELESCOPE_FIRST_NAME, TELESCOPE_FULL_NAME,
            TELESCOPE_LAST_NAME, TELESCOPE_LEVEL, TELESCOPE_NATIVE_NAME, TELESCOPE_OFFICE, TELESCOPE_PHONE,
            TELESCOPE_PHONES, TELESCOPE_SENIORITY
    };

    private TelescopeParser() {}

    /***/
    public static List<PersonDto> parseTelescopeCSV(String telescopeCsvFile, String fileEncoding) {
        LOG.debug(String.format("TelescopeParser.parseCSV() is working. File to parse [%s], encoding [%s].",
                telescopeCsvFile, fileEncoding));

        if (StringUtils.isBlank(telescopeCsvFile)) { // fail-fast
            throw new IllegalArgumentException("Can't parse file with empty/null name!");
        }

        // resulting list of parsed data
        List<PersonDto> telePeople = new ArrayList<>();

        Reader      fileReader = null;
        CSVParser   csvParser;

        // build CSV format
        CSVFormat csvFormat = CSVFormat.DEFAULT.withHeader(FILE_HEADER);

        try {
            // file reader with specified encoding for CSV parser
            fileReader = new InputStreamReader(new FileInputStream(telescopeCsvFile),
                    (StringUtils.isBlank(fileEncoding) ? DEFAULT_ENCODING : fileEncoding));
            // CSV parser instance
            csvParser = new CSVParser(fileReader, csvFormat);

            List<CSVRecord> csvRecords = csvParser.getRecords();
            PersonDto person;
            Set<String> names;

            for (int i = 1; i < csvRecords.size(); i++) {
                CSVRecord record = csvRecords.get(i); // get one record

                if (StringUtils.isBlank(record.get(TELESCOPE_END_WORK_DATE))) { // process only current working employees

                    // create new person object with Telescope ID and type
                    person = new PersonDto(0, Long.parseLong(record.get(TELESCOPE_ID)), TELESCOPE);
                    // add properties to person object
                    person.setFirstName(record.get(TELESCOPE_FIRST_NAME));
                    person.setLastName(record.get(TELESCOPE_LAST_NAME));
                    person.setDisplayName(record.get(TELESCOPE_DISPLAY_NAME));
                    person.setNativeName(record.get(TELESCOPE_NATIVE_NAME));


                    // process full name (list of possible names)
                    names = new HashSet<>();
                    // get <full name> value and remove [] symbols (at start and at the end)
                    String fullName = StringUtils.strip(record.get(TELESCOPE_FULL_NAME), "[]");
                    LOG.debug(String.format("Raw full name [%s].", fullName));
                    for (String name : StringUtils.split(fullName, ",")) { // add names to set
                        names.add(StringUtils.strip(StringUtils.trimToEmpty(name), "'"));
                    }
                    LOG.debug(String.format("Set of names: %s.\n", names));
                    // add names set to person object
                    person.setNamesList(names);

                }
            } // end of FOR statement

        } catch (IOException e) {
            LOG.error(e);
        } finally {
            if (fileReader != null) {
                try {
                    fileReader.close();
                } catch (IOException e) {
                    LOG.error(String.format("Can't close Reader object for file [%s]!", telescopeCsvFile));
                }
            }
        } // end of finally

        return telePeople;
    }

    /***/
    public static List<PersonDto> parseTelescopeCSV(String telescopeCsvFile) {
        return TelescopeParser.parseTelescopeCSV(telescopeCsvFile, TELESCOPE_CSV_ENCODING);
    }

    public static void main(String[] args) {
        LOG.info("Telescope parser is starting...");

        //CommonUtilities.unZipIt("people.zip", "");

        List<PersonDto> perons = TelescopeParser.parseTelescopeCSV("people.csv");
    }

}
