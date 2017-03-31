package dg.social.crawler.networks.telescope;

import dg.social.crawler.domain.EducationProfileValue;
import dg.social.crawler.domain.PersonDto;
import dg.social.crawler.networks.ParserInterface;
import dg.social.crawler.utilities.CommonUtilities;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
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

    private static final String TELESCOPE_CSV_ENCODING      = "Windows-1251";
    // percents of data size for reporting (LOG to console)
    private static final int    REPORT_STEP_PERCENT         = 10;

    // header fields for Telescope output file
    private static final String TELESCOPE_ID                = "id";                // +
    private static final String TELESCOPE_ANNIVERSARY_DATE  = "anniversaryDate";   // possibly don't need
    private static final String TELESCOPE_CITY              = "city";              // +
    private static final String TELESCOPE_CITY_SUM          = "citySum";           // +
    private static final String TELESCOPE_COUNTRY           = "country";           // +
    private static final String TELESCOPE_COUNTRY_SUM       = "countrySum";        // +
    private static final String TELESCOPE_DISPLAY_NAME      = "displayName";       // +
    private static final String TELESCOPE_EDUCATION         = "education";
    private static final String TELESCOPE_EMAIL             = "email";             // +
    private static final String TELESCOPE_EMPLOYMENT_STATUS = "employmentStatus";  // possibly don't need
    private static final String TELESCOPE_END_WORK_DATE     = "endWorkDate";       // +
    private static final String TELESCOPE_FIRST_NAME        = "firstName";         // +
    private static final String TELESCOPE_FULL_NAME         = "fullName";          // +
    private static final String TELESCOPE_LAST_NAME         = "lastName";          // +
    private static final String TELESCOPE_LEVEL             = "level";             // possibly don't need
    private static final String TELESCOPE_NATIVE_NAME       = "nativeName";        // +
    private static final String TELESCOPE_OFFICE            = "office";            // +
    private static final String TELESCOPE_PHONE             = "phone";             // +
    private static final String TELESCOPE_PHONES            = "phones";            // +
    private static final String TELESCOPE_SENIORITY         = "seniority";         // possibly don't need

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
            LOG.debug("CSV Parser created.");

            List<CSVRecord> csvRecords = csvParser.getRecords();
            LOG.info(String.format("Get records from CSV file [%s]. Records count [%s].",
                    telescopeCsvFile, csvRecords.size()));

            PersonDto person;
            Set<String> tmpSet;
            final int reportCounter = csvRecords.size() / REPORT_STEP_PERCENT;

            for (int i = 1; i < csvRecords.size(); i++) {
                CSVRecord record = csvRecords.get(i); // get one record

                if (StringUtils.isBlank(record.get(TELESCOPE_END_WORK_DATE))) { // process only current working employees

                    // parse one record create new person object with Telescope ID and type
                    try {
                        person = new PersonDto(0, Long.parseLong(record.get(TELESCOPE_ID)), TELESCOPE);
                        // add properties to person object
                        person.setFirstName(record.get(TELESCOPE_FIRST_NAME));
                        person.setLastName(record.get(TELESCOPE_LAST_NAME));
                        person.setDisplayName(record.get(TELESCOPE_DISPLAY_NAME));
                        person.setNativeName(record.get(TELESCOPE_NATIVE_NAME));

                        // add names/countries/cities sets to person object
                        person.setNamesList(CommonUtilities.parseStringArray(record.get(TELESCOPE_FULL_NAME)));
                        person.setCitiesList(CommonUtilities.parseStringArray(record.get(TELESCOPE_CITY)));
                        person.setCity(record.get(TELESCOPE_CITY_SUM));
                        person.setCountriesList(CommonUtilities.parseStringArray(record.get(TELESCOPE_COUNTRY)));
                        person.setCountry(record.get(TELESCOPE_COUNTRY_SUM));

                        person.setEmailsList(CommonUtilities.parseStringArray(record.get(TELESCOPE_EMAIL)));
                        person.setOfficeAddress(record.get(TELESCOPE_OFFICE));

                        // join two fields: phone and phones
                        tmpSet = CommonUtilities.parseStringArray(record.get(TELESCOPE_PHONE));
                        tmpSet.addAll(CommonUtilities.parseStringArray(record.get(TELESCOPE_PHONES)));
                        if (tmpSet.size() > 0) {
                            person.setPhonesList(tmpSet);
                        }

                        //Set<EducationProfileValue> education = TelescopeParser.parseEducationString(record.get(TELESCOPE_EDUCATION));
                        System.out.println("-> " + record.get(TELESCOPE_EDUCATION));

                        // add resulting person to people list
                        telePeople.add(person);
                    } catch (NumberFormatException /*| ParseException*/ e) {
                        LOG.error(String.format("Can't parse ID/record with Telescope ID [%s]!", record.get(TELESCOPE_ID)), e);
                    }
                } // end IF for actual employees

                // reporting
                if (i % reportCounter == 0) {
                    LOG.info(String.format("Processed records [%s].", i));
                }

            } // end of FOR statement

            LOG.info(String.format("Processed records [%s].", csvRecords.size()));

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

    /***/
    protected static Set<EducationProfileValue> parseEducationString(String education) {

        if (StringUtils.isBlank(education)) { // fast check
            return null;
        }

        Set<EducationProfileValue> educations = new HashSet<>();

        // prepare source string
        String[] educationArray = StringUtils.splitByWholeSeparator(StringUtils.strip(education, "[]"), "}, {");
        // iterate over and build objects
        EducationProfileValue educationProfileValue;
        for (String edu : educationArray) {
            String[] eduElements = StringUtils.split(StringUtils.strip(edu, "[{}]"), ",");

            // iterate over elements and init instance
            educationProfileValue = new EducationProfileValue();
            for (String eduElement : eduElements) {
                String[] tmpStr = StringUtils.split(eduElement, ":");
                if (tmpStr.length == 2) {
                    String name  = StringUtils.strip(tmpStr[0], "' ");
                    String value = StringUtils.strip(tmpStr[1], "' ");
                    switch (name) {
                        case "faculty":
                            educationProfileValue.setFaculty(value);
                            break;
                        case "institution":
                            educationProfileValue.setUniversity(value);
                            break;
                        case "graduationYear":
                            educationProfileValue.setGraduationYear(value);
                            break;
                        case "department":
                            educationProfileValue.setDepartment(value);
                            break;
                        case "degree":
                            educationProfileValue.setDegree(value);
                            break;
                        case "startYear":
                            educationProfileValue.setStartYear(value);
                            break;
                        case "institutionUrl":
                            educationProfileValue.setUniversityUrl(value);
                            break;
                    } // end of SWITCH
                }
            } // end of FOR cycle
            educations.add(educationProfileValue);
        } // end of MAIN FOR cycle

        return educations;
    }

    public static void main(String[] args) {
        TelescopeParser.parseTelescopeCSV("people.csv");
    }

}
