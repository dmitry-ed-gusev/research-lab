package dg.social.crawler.utilities;

import dg.social.crawler.domain.TelescopePersonDto;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Parses CSV files from Telescope (people info).
 * Created by gusevdm on 2/20/2017.
 */

public class TelescopeCSVParser {

    private static Log LOG = LogFactory.getLog(TelescopeCSVParser.class);

    //id,anniversaryDate,city,citySum,country,countrySum,displayName,education,email,employmentStatus,endWorkDate,firstName,fullName,lastName,level,nativeName,office,phone,phones,seniority

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

    private static final String[] FILE_HEADER = {
            TELESCOPE_ID, TELESCOPE_ANNIVERSARY_DATE, TELESCOPE_CITY, TELESCOPE_CITY_SUM, TELESCOPE_COUNTRY,
            TELESCOPE_COUNTRY_SUM, TELESCOPE_DISPLAY_NAME, TELESCOPE_EDUCATION, TELESCOPE_EMAIL,
            TELESCOPE_EMPLOYMENT_STATUS, TELESCOPE_END_WORK_DATE, TELESCOPE_FIRST_NAME, TELESCOPE_FULL_NAME,
            TELESCOPE_LAST_NAME, TELESCOPE_LEVEL, TELESCOPE_NATIVE_NAME, TELESCOPE_OFFICE, TELESCOPE_PHONE,
            TELESCOPE_PHONES, TELESCOPE_SENIORITY
    };

    public static void parseCSV() {
        LOG.debug("TelescopeCSVParser.parseCSV() is working.");

        FileReader fileReader = null;
        CSVParser  csvParser = null;

        CSVFormat csvFormat = CSVFormat.DEFAULT.withHeader(FILE_HEADER);

        try {
            List<TelescopePersonDto> telePeople = new ArrayList<>();

            fileReader = new FileReader("people.csv");
            csvParser = new CSVParser(fileReader, csvFormat);

            List<CSVRecord> csvRecords = csvParser.getRecords();

            for (int i = 1; i < csvRecords.size(); i++) {
                CSVRecord record = csvRecords.get(i);

                String endDate = record.get(TELESCOPE_END_WORK_DATE);

                if (StringUtils.isBlank(endDate)) { // process only current working employees
                    TelescopePersonDto telePerson = new TelescopePersonDto();
                    telePerson.setId(Long.parseLong(record.get(TELESCOPE_ID)));
                    telePerson.setFullName(record.get(TELESCOPE_FULL_NAME));
                    //telePerson.setEndWorkDate(endDate);
                    telePeople.add(telePerson);
                    System.out.println("-> " + telePerson);
                }
            }

        } catch (Exception e) {
            // todo: !!!
        }

    }

}
