package edu.dmgusev.csv;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.csv.CSVRecord;

import edu.dmgusev.utils.Utilities;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WeatherCSVApplication {

    // some useful constants
    public static final String           BASE_CSV_FOLDER = "weather";
    // format used for weather files
    public static final SimpleDateFormat DATE_FORMAT_US  = new SimpleDateFormat("yyyy-MM-dd");
    // input dates formats (EU, but various formats)
    public static final String[]         DATE_FORMATS    = 
        {"dd.MM.yyyy", "dd.MM.yy", "dd/MM/yyyy", "dd/MM/yy"};
    // years that are valid for dataset
    public static final Set<Integer>     VALID_YEARS     = 
        Stream.of(2012, 2013, 2014, 2015).collect(Collectors.toSet());

    /** Parse provided string into Date object applying several formats - until match or throws exception. */
    private static Date parseDate(@NonNull String strDate) throws ParseException {
        log.debug(String.format("parseDate(): parsing date [%s].", strDate));

        for (String format: DATE_FORMATS) {
            try {
                return new SimpleDateFormat(format).parse(strDate);
            } catch (ParseException e) {
                log.warn(String.format("Format [%s] doesn't match the value [%s]!",
                    format, strDate));
            }
        } // end of FOR

        // no matching format for the date
        throw new ParseException(String.format(
            "Can't find the matching format for the given date [%s]!", strDate), 0);
    }

    /** Check the validity of the year in the provided Date object. */
    private static boolean isYearValid(@NonNull Date date) {
        log.debug(String.format("isYearValid(): checking validity of the year for date: [%s].", date));

        var calendar = Calendar.getInstance();
        calendar.setTime(date);
        return VALID_YEARS.contains(calendar.get(Calendar.YEAR));

    }

    /** Get set of files from the dataset by the provided dates. */
    private Set<File> getCSVFilesByDates(@NonNull String... strDates) 
        throws ParseException, URISyntaxException, IOException {

        log.debug(String.format("getCSVFilesByDates(): processing dates [%s].",
            Arrays.toString(strDates)));

        Set<File> resultFiles = new HashSet<>(); // resulting set of files from dataset

        // Part I: process provided list of dates and convert them to US format
        Set<String> strUsDates = new HashSet<>();
        for (String strDate: strDates) {
            var localDate = WeatherCSVApplication.parseDate(strDate);

            if (!WeatherCSVApplication.isYearValid(localDate)) { // check validity of the year
                throw new IllegalArgumentException(String.format(
                    "Provided invalid year in the date [%s]!", strDate));
            }

            strUsDates.add(DATE_FORMAT_US.format(localDate)); // add converted date to the set
        } // end of FOR

        // Part II: iterate over dataset files and pick up necessary files
        for (File file: new Utilities().getAllFilesFromResource(BASE_CSV_FOLDER)) {
            for (String tmpStrDate: strUsDates) {
                if (file.getAbsolutePath().contains(tmpStrDate)) {
                    resultFiles.add(file);
                }
            }
        }
    
        return resultFiles;
    }

    /** */
    private File getCSVFileByDate(@NonNull String strDate)
        throws ParseException, URISyntaxException, IOException {

        log.debug(String.format("getCSVFileByDate(): processing date [%s].", strDate));

        // get one CSV file by provided date
        Set<File> files = this.getCSVFilesByDates(strDate);

        if (files.isEmpty() || files.size() > 1) { // integrity check/fail-fast - should be one file
            throw new IllegalStateException(String.format(
                "Should be one file for date [%s], but found [%s]!", strDate, files.size()));
        }

        return files.iterator().next(); // return one file from dataset
    }

    /**
        Write a method named coldestHourInFile() that has one parameter, a CSVParser named parser. 
        This method returns the CSVRecord with the coldest temperature in the file and thus all the 
        information about the coldest temperature, such as the hour of the coldest temperature.

        NOTE: Sometimes there was not a valid reading at a specific hour, so the temperature field 
        says -9999. You should ignore these bogus temperature values when calculating the lowest temperature.
    */
    public CSVRecord coldestHourInFile(@NonNull String strDate) 
        throws URISyntaxException, IOException, ParseException {

        log.debug(String.format("coldestHourInFile(): looking for date [%s].", strDate));

        // processing the found file (the first one/the only one)
        CSVRecord result = null;
        File file = this.getCSVFileByDate(strDate);
        try (var parser = Utilities.getCSVParser(file)) {

            for (CSVRecord csvRecord: parser) {
                double temperature = Double.parseDouble(csvRecord.get("TemperatureF"));

                // update the existing resulting CSV Record in case case matched
                if (result == null || 
                    (temperature > -9999 && 
                        temperature < Double.parseDouble(result.get("TemperatureF")))) {

                    // updating the current result record
                    result = csvRecord;
                    log.debug(String.format("The current record [%s] updated with [%s].",
                        result, csvRecord));

                }

            } // end of FOR

        } // end of TRY-WITH-RESOURCES

        return result;
    }

    /** 
        Write a void method named testColdestHourInFile() to test this method and print out information 
        about that coldest temperature, such as the time of its occurrence.
    */
    public void testColdestHourInFile(@NonNull String strDate) 
        throws URISyntaxException, IOException, ParseException {

        log.debug("testColdestHourInFile() is working.");
        System.out.println("-> " + this.coldestHourInFile(strDate));

    }

    /** 
        Write the method fileWithColdestTemperature that has no parameters. This method should return a 
        string that is the name of the file from selected files that has the coldest temperature.

        Note: my own implementation will perform action based on specified dates, not using file chooser.
    */
    public void fileWithColdestTemperature(@NonNull String... strDates) {

    }

    /** 
        Write a void method named testFileWithColdestTemperature() to test the method 
        fileWithColdestTemperature(). Note that after determining the filename, you could call the method 
        coldestHourInFile() to determine the coldest temperature on that day. 
        
        Sample result: when fileWithColdestTemperature() runs and selects the files for January 1–3 in 2014, 
        the method should print out:
            Coldest day was in file weather-2014-01-03.csv
            Coldest temperature on that day was 21.9
            All the Temperatures on the coldest day were:
                <list of the all temperatures on the found date>
    */
    public void testFileWithColdestTemperature() {

    }

    /** 
        Write a method named lowestHumidityInFile that has one parameter, a CSVParser named parser. 
        This method returns the CSVRecord that has the lowest humidity. If there is a tie, then return 
        the first such record that was found.

        Note that sometimes there is not a number in the Humidity column but instead there is the 
        string “N/A”. This only happens very rarely. You should check to make sure the value you get is 
        not “N/A” before converting it to a number.

        Also note that the header for the time is either TimeEST or TimeEDT, depending on the time of year. 
        You will instead use the DateUTC field at the right end of the data file to  get both the date and 
        time of a temperature reading.
    */
    public void lowestHumidityInFile() {

    }

    /** 
        You should also write a void method named testLowestHumidityInFile() to test this method that starts with these lines:

123
FileResource fr = new FileResource();
CSVParser parser = fr.getCSVParser();
CSVRecord csv = lowestHumidityInFile(parser);
and then prints the lowest humidity AND the time the lowest humidity occurred. For example, for the file weather-2014-01-20.csv, the output should be:

1
Lowest Humidity was 24 at 2014-01-20 19:51:00
NOTE: If you look at the data for January 20, 2014, you will note that the Humidity was also 24 at 3:51pm, but you are supposed to return the first such record that was found.
    */
    public void testLowestHumidityInFile() {

    }

    /** 
        Write the method lowestHumidityInManyFiles that has no parameters. This method returns a CSVRecord that has the lowest humidity over all the files. If there is a tie, then return the first such record that was found. You should also write a void method named testLowestHumidityInManyFiles() to test this method and to print the lowest humidity AND the time the lowest humidity occurred. Be sure to test this method on two files so you can check if it is working correctly. If you run this program and select the files for January 19, 2014 and January 20, 2014, you should get

        Lowest Humidity was 24 at 2014-01-20 19:51:00
    */
    public void lowestHumidityInManyFiles() {

    }

    /** 
        Write the method averageTemperatureInFile that has one parameter, a CSVParser named parser. This method returns a double that represents the average temperature in the file. You should also write a void method named testAverageTemperatureInFile() to test this method. When this method runs and selects the file for January 20, 2014, the method should print out

1
    Average temperature in file is 44.93333333333334
    */
    public void averageTemperatureInFile() {

    }

    /**
        Write the method averageTemperatureWithHighHumidityInFile that has two parameters, a CSVParser named parser and an integer named value. This method returns a double that represents the average temperature of only those temperatures when the humidity was greater than or equal to value. You should also write a void method named testAverageTemperatureWithHighHumidityInFile() to test this method. When this method runs checking for humidity greater than or equal to 80 and selects the file for January 20, 2014, the method should print out

1   No temperatures with that humidity
    If you run the method checking for humidity greater than or equal to 80 and select the file March 20, 2014, a wetter day, the method should print out

    Average Temp when high Humidity is 41.78666666666667
     * @throws IOException
     * @throws URISyntaxException
     */
    public void averageTemperatureWithHighHumidityInFile() {

    }

    public static void main(String[] args) throws URISyntaxException, IOException, ParseException {
        log.info("Weather Analisys CSV Application is starting...");

        var application = new WeatherCSVApplication();
        application.testColdestHourInFile("04.01.2012");
    }

}
