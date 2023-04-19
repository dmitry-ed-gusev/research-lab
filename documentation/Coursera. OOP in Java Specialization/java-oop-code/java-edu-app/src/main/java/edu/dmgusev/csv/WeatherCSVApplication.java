package edu.dmgusev.csv;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import edu.dmgusev.utils.Utilities;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WeatherCSVApplication {

    // some useful constants
    public static final String           BASE_CSV_FOLDER    = "weather";
    // format used for weather files
    public static final SimpleDateFormat DATE_FORMAT_US     = new SimpleDateFormat("yyyy-MM-dd");
    
    // DATEs: input dates formats (EU, but various formats)
    public static final String[]         DATE_FORMATS       =
        {"dd.MM.yyyy", "dd.MM.yy", "dd/MM/yyyy", "dd/MM/yy"};
    // MONTHs + YEARs: input formats
    public static final String[]         MONTH_YEAR_FORMATS = 
        {"MM.yyyy", "MM.yy", "MM/yyyy", "MM/yy"};
    // YEARs: input formats
    public static final String[]         YEAR_FORMATS       = 
        {"yyyy", "yy"};

    // years that are valid for dataset
    public static final Set<Integer>     VALID_YEARS       =
        Stream.of(2012, 2013, 2014, 2015).collect(Collectors.toSet());

    // dataset file name template
    public static final String           FILENAME_TEMPLATE = "weather-%s.csv";

    // todo: move code working with dates to a separate module...

    /** */
    private static Optional<Date> parseDateByFormats(@NonNull String strDate, @NonNull String... formats) {
        log.debug("parseDateByFormats(): parse {} using formats[{}].", 
            strDate, Arrays.toString(formats));

        Date parsedDate = null;
        for (String format: formats) { // iterate over formats and try to parse a date
            try {
                parsedDate = new SimpleDateFormat(format).parse(strDate);
                break; // if we parsed the date - get out from the cycle
            } catch (ParseException e) {
                log.debug("Format [{}] doesn't match the value [{}]!", format, strDate);
            }
        } // end of FOR

        return Optional.ofNullable(parsedDate);
    }

    /** Get list of all dates for the concrete month from the specified date. */
    private static List<Date> getMonthDates(@NonNull String strMonthYear, @NonNull String... formats) 
        throws ParseException {

        log.debug("getMonthDates(): parsing date [{}].", strMonthYear);

        // --- Part I. Parse month and year into a date.
        var parsedDate = parseDateByFormats(strMonthYear, formats);
        if (parsedDate.isEmpty()) { // if we can't find the matching format - throw an exception
            throw new ParseException(String.format(
            "Can't find the matching format for the given date [%s]!", strMonthYear), 0);
        }

        // --- Part II. Generate list of dates for the month (all days).
        Calendar cal = Calendar.getInstance();
        cal.setTime(parsedDate.get());
        cal.set(Calendar.DAY_OF_MONTH, 1); 
        int myMonth = cal.get(Calendar.MONTH);

        var list = new ArrayList<Date>();
        while (myMonth == cal.get(Calendar.MONTH)) { // until we are in a current month - iterate
            list.add(cal.getTime());
            cal.add(Calendar.DAY_OF_MONTH, 1);
        } // end of WHILE

        return list;
    }

    /** */
    private static List<Date> getYearDates(@NonNull String strYear, @NonNull String... formats) 
        throws ParseException {

        log.debug("getYearDates(): parsing date [{}].", strYear);

        // --- Part I. Parse month and year into a date.
        var parsedDate = parseDateByFormats(strYear, formats);
        if (parsedDate.isEmpty()) { // if we can't find the matching format - throw an exception
            throw new ParseException(String.format(
            "Can't find the matching format for the given date [%s]!", strYear), 0);
        }

        // --- Part II. Generate list of dates for the month (all days).
        Calendar cal = Calendar.getInstance();
        cal.setTime(parsedDate.get());
        cal.set(Calendar.MONTH, 0); // months numbers start from 0
        cal.set(Calendar.DAY_OF_MONTH, 1); 
        int myYear = cal.get(Calendar.YEAR);

        var list = new ArrayList<Date>();
        while (myYear == cal.get(Calendar.YEAR)) { // until we are in a current month - iterate
            list.add(cal.getTime());
            cal.add(Calendar.DAY_OF_MONTH, 1);
        } // end of WHILE

        return list;
    }

    /** Check the validity of the year in the provided Date object. */
    private static boolean isYearValid(@NonNull Date date) {
        log.debug(String.format("isYearValid(): checking validity of the year for date: [%s].", date));

        var calendar = Calendar.getInstance();
        calendar.setTime(date);
        return VALID_YEARS.contains(calendar.get(Calendar.YEAR));
    }

    /** Get set of files from the dataset by the provided dates. */
    private Set<File> getCSVFilesByDates(String... strDates) 
        throws ParseException, URISyntaxException, IOException {

        log.debug(String.format("getCSVFilesByDates(): processing dates [%s].",
            Arrays.toString(strDates)));
        
        // get all files in a dataset (no filtering now)
        Set<File> datasetFiles = new Utilities().getAllFilesFromResource(BASE_CSV_FOLDER);

        // if provided list of dates is null or empty - return all files in a dataset
        if (strDates == null || strDates.length <= 0) {
            return datasetFiles;
        }

        // provided list of dates isn't null or empty - processing further
        Set<File> resultFiles = new HashSet<>(); // resulting set of files from dataset

        // Part I: process provided list of dates and convert them to US format
        Set<String> strUsDates = new HashSet<>();
        for (String strDate: strDates) {
            var localDate = WeatherCSVApplication.parseDateByFormats(strDate, DATE_FORMATS);
            
            if (localDate.isEmpty()) {
                throw new ParseException("Can't parse!", 0);
            }

            // todo: uncomment and fix!
            // if (!WeatherCSVApplication.isYearValid(localDate)) { // check validity of the year
            //     throw new IllegalArgumentException(String.format(
            //         "Provided invalid year in the date [%s]!", strDate));
            // }

            strUsDates.add(DATE_FORMAT_US.format(localDate.get())); // add converted date to the set
        } // end of FOR

        // Part II: iterate over dataset files and pick up necessary files
        // todo: use stream and filtering + collector into set
        for (File file: datasetFiles) {
            for (String tmpStrDate: strUsDates) {
                if (file.getAbsolutePath().contains(tmpStrDate)) {
                    resultFiles.add(file);
                }
            }
        } // end of FOR processing of dataset files
    
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

    /** */
    private static double getTempFromCSVRecord(@NonNull CSVRecord csvRecord) {
        return Double.parseDouble(csvRecord.get("TemperatureF"));
    }

    /** */
    private static String getTimeFromCSVRecord(@NonNull CSVRecord csvRecord) {
        try {
            return csvRecord.get("TimeEST"); // try to get value from "TimeEST" column
        } catch (IllegalArgumentException e) {
            log.warn("Column \"TimeEST\" not found!", e);
            return csvRecord.get("TimeEDT"); // try to get value from "TimeEDT" column
        }
    }

    /** */
    private static OptionalInt getHumidityFromCSVRecord(@NonNull CSVRecord csvRecord) {
        var result = csvRecord.get("Humidity");
        if ("N/A".equalsIgnoreCase(result)) {
            return OptionalInt.empty();
        } else {
            return OptionalInt.of(Integer.parseInt(result));
        }
    }

    /** */
    private static String getUTCTimestampFromCSVRecord(@NonNull CSVRecord csvRecord) {
        return csvRecord.get("DateUTC");
    }

    /**
        Write a method named coldestHourInFile() that has one parameter, a CSVParser named parser. 
        This method returns the CSVRecord with the coldest temperature in the file and thus all the 
        information about the coldest temperature, such as the hour of the coldest temperature.

        NOTE: Sometimes there was not a valid reading at a specific hour, so the temperature field 
        says -9999. You should ignore these bogus temperature values when calculating the lowest temperature.
    */
    public Pair<File, CSVRecord> coldestHourInFile(@NonNull String strDate) 
        throws URISyntaxException, IOException, ParseException {

        log.debug(String.format("coldestHourInFile(): looking for date [%s].", strDate));

        // processing the found file (the first one/the only one)
        CSVRecord result = null;
        File file = this.getCSVFileByDate(strDate);
        try (var parser = Utilities.getCSVParser(file)) {

            for (CSVRecord csvRecord: parser) {
                double temperature = getTempFromCSVRecord(csvRecord);

                // update the existing resulting CSV Record in case case matched
                if (result == null || 
                    (temperature > -9999 && temperature < getTempFromCSVRecord(result))) {

                    // updating the current result record
                    result = csvRecord;
                    log.debug(String.format("The current record [%s] updated with [%s].",
                        result, csvRecord));
                } // end of IF

            } // end of FOR

        } // end of TRY-WITH-RESOURCES

        return new ImmutablePair<>(file, result);
    }

    /** 
        Write a void method named testColdestHourInFile() to test this method and print out information 
        about that coldest temperature, such as the time of its occurrence.
    */
    public void testColdestHourInFile() throws URISyntaxException, IOException, ParseException {

        log.debug("testColdestHourInFile() is working.");

        //var result = this.coldestHourInFile("04.01.2012"); // get result from the method under test
        var result = this.coldestHourInFile("01.05.2014"); // get result from the method under test


        // extract data from the result
        var file = result.getLeft();
        var temp = getTempFromCSVRecord(result.getRight());
        var hour = getTimeFromCSVRecord(result.getRight());

        // print output
        System.out.println(
            String.format("%nThe coldest hour in file [%s] is [%s] with the temperature [%s].",
                file, hour, temp));
    }

    /** 
        Write the method fileWithColdestTemperature() that has no parameters. This method should return a 
        string that is the name of the file from selected files that has the coldest temperature.

        Note: my own implementation will perform action based on specified dates, not using file chooser.
    */
    public Pair<File, CSVRecord> fileWithColdestTemperature(@NonNull String... strDates)
        throws URISyntaxException, IOException, ParseException {

        log.debug(String.format("fileWithColdestTemperature(): looking for dates [%s].", 
            Arrays.toString(strDates)));

        Pair<File, CSVRecord> resultFileTempData = null;
        for (String strDate: strDates) {
            var currentFileTempData = coldestHourInFile(strDate);
            var currentTemperature = getTempFromCSVRecord(currentFileTempData.getRight());

            if (resultFileTempData == null ||
                (currentTemperature > -999 &&
                    currentTemperature < getTempFromCSVRecord(resultFileTempData.getRight()))) {
                resultFileTempData = currentFileTempData;
            } // end of IF

        } // end of FOR

        return resultFileTempData; // todo: may return null - fix?
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
    public void testFileWithColdestTemperature() throws URISyntaxException, IOException, ParseException {
        log.debug("testFileWithColdestTemperature() is working.");
        
        var listDates = getYearDates("2014", YEAR_FORMATS);
        var listStrDates = new String[listDates.size()];
        var sdf = new SimpleDateFormat("dd.MM.yyyy");
        var counter = 0;
        for (Date date: listDates) {
            var strDate = sdf.format(date);
            listStrDates[counter] = strDate;
            counter++;
        }

        // var result = fileWithColdestTemperature("01.01.2014", "02.01.2014", "03.01.2014");
        var result = fileWithColdestTemperature(listStrDates);

        System.out.println(String.format("%nColdest day was in file [%s]%n" +
            "Coldest temperature on that day was [%s]%n" +
            "All the Temperatures on the coldest day were:%n\t%s%n",
            result.getLeft().getName(), getTempFromCSVRecord(result.getRight()),
                Utilities.getColumnValuesFromCSVFile(result.getLeft(), "TemperatureF")));
    }

    /** 
        Write a method named lowestHumidityInFile() that has one parameter, a CSVParser named parser. 
        This method returns the CSVRecord that has the lowest humidity. If there is a tie, then return 
        the first such record that was found.

        Note that sometimes there is not a number in the Humidity column but instead there is the 
        string “N/A”. This only happens very rarely. You should check to make sure the value you get is 
        not “N/A” before converting it to a number.

        Also note that the header for the time is either TimeEST or TimeEDT, depending on the time of year. 
        You will instead use the DateUTC field at the right end of the data file to  get both the date and 
        time of a temperature reading.
    */
    public Pair<File, CSVRecord> lowestHumidityInFile(@NonNull String strDate) 
        throws ParseException, URISyntaxException, IOException {

        log.debug(String.format("lowestHumidityInFile(): looking for date: [%s].", strDate));

        // processing the found file (the first one/the only one)
        CSVRecord result = null;
        File file = this.getCSVFileByDate(strDate);
        try (var parser = Utilities.getCSVParser(file)) {

            for (CSVRecord csvRecord: parser) {
                var humidity = getHumidityFromCSVRecord(csvRecord);

                // update the existing resulting CSV Record in case case matched
                if (humidity.isPresent() && // process only if humidity is not "N/A"
                    (result == null || 
                        (getHumidityFromCSVRecord(result).getAsInt() > 
                            getHumidityFromCSVRecord(csvRecord).getAsInt()))) {

                    // updating the current result record
                    result = csvRecord;
                    log.debug(String.format("The current record [%s] updated with [%s].",
                        result, csvRecord));

                } // end of IF

            } // end of FOR

        } // end of TRY-WITH-RESOURCES

        return new ImmutablePair<>(file, result);
    }

    /** 
        You should also write a void method named testLowestHumidityInFile() to test the method 
        lowestHumidityInFile() that starts with these lines:
            FileResource fr = new FileResource();
            CSVParser parser = fr.getCSVParser();
            CSVRecord csv = lowestHumidityInFile(parser);
        and then prints the lowest humidity AND the time the lowest humidity occurred.

        For example, for the file weather-2014-01-20.csv, the output should be:
            Lowest Humidity was 24 at 2014-01-20 19:51:00

        NOTE: If you look at the data for January 20, 2014, you will note that the Humidity was 
            also 24 at 3:51pm, but you are supposed to return the first such record that was found.
    */
    public void testLowestHumidityInFile() throws ParseException, URISyntaxException, IOException {
        log.debug("testLowestHumidityInFile() is working.");
        
        // var result = lowestHumidityInFile("20.01.2014");
        var result = lowestHumidityInFile("01.04.2014");

        System.out.println(String.format("%nThe lowest humidity in the file [%s] was [%s] at [%s].",
            result.getLeft().getName(), getHumidityFromCSVRecord(result.getRight()).getAsInt(),
                getUTCTimestampFromCSVRecord(result.getRight())));
    }

    /** 
        Write the method lowestHumidityInManyFiles() that has no parameters. This method returns a 
        CSVRecord that has the lowest humidity over all the files. If there is a tie, then return the first 
        such record that was found.
    */
    public Pair<File, CSVRecord> lowestHumidityInManyFiles(@NonNull String... strDates) 
        throws ParseException, URISyntaxException, IOException {
        
        log.debug(String.format("lowestHumidityInManyFiles(): looking for dates [%s].", 
            Arrays.toString(strDates)));

        Pair<File, CSVRecord> resultFileHumidityData = null;
        for (String strDate: strDates) {
            var currentFileHumidityData = lowestHumidityInFile(strDate); // Pair<File, CSVRecord>
            var currentHumidity = getHumidityFromCSVRecord(currentFileHumidityData.getRight())
                .getAsInt(); // int

            if (resultFileHumidityData == null ||
                (currentHumidity < getHumidityFromCSVRecord(resultFileHumidityData.getRight()).getAsInt())) {
                resultFileHumidityData = currentFileHumidityData;
            } // end of IF

        } // end of FOR

        return resultFileHumidityData; // todo: may return null - fix?
    }

    /** 
        You should also write a void method named testLowestHumidityInManyFiles() to test the method 
        lowestHumidityInManyFiles() and to print the lowest humidity AND the time the lowest humidity 
        occurred. Be sure to test this method on two files so you can check if it is working correctly. 

        If you run this program and select the files for January 19, 2014 and January 20, 2014, 
        you should get:
            Lowest Humidity was 24 at 2014-01-20 19:51:00
    */
    public void testLowestHumidityInManyFiles() throws ParseException, URISyntaxException, IOException {
        log.debug("testLowestHumidityInManyFiles() is working.");

        var listDates = getYearDates("2014", YEAR_FORMATS);
        var listStrDates = new String[listDates.size()];
        var sdf = new SimpleDateFormat("dd.MM.yyyy");
        var counter = 0;
        for (Date date: listDates) {
            var strDate = sdf.format(date);
            listStrDates[counter] = strDate;
            counter++;
        }

        // var result = lowestHumidityInManyFiles("19.01.2014", "20.01.2014");
        var result = lowestHumidityInManyFiles(listStrDates);

        System.out.println(String.format("%nThe lowest humidity was in the " + 
            "file [%s],it was [%s] at [%s].",
                result.getLeft().getName(), getHumidityFromCSVRecord(result.getRight()).getAsInt(),
                    getUTCTimestampFromCSVRecord(result.getRight())));
    }

    /** 
        Write the method averageTemperatureInFile() that has one parameter, a CSVParser named parser. 
        This method returns a double that represents the average temperature in the file. 
    */
    public double averageTemperatureInFile(@NonNull String strDate) 
        throws ParseException, URISyntaxException, IOException {

        log.debug(String.format("averageTemperatureInFile(): looking for avg temp in date: [%s].",
            strDate));

        // processing the found file (the first one/the only one)
        double result = 0.0;
        File file = this.getCSVFileByDate(strDate);
        try (var parser = Utilities.getCSVParser(file)) {

            var counter = 0;
            for (CSVRecord csvRecord: parser) {
                result = result + getTempFromCSVRecord(csvRecord);
                counter++;
            } // end of FOR

            if (counter == 0) { // no CSV records in CSV file
                return 0.0;
            } else {
                return result / counter;
            }

        } // end of TRY-WITH-RESOURCES
    }

    /** 
        You should also write a void method named testAverageTemperatureInFile() to test the method
        averageTemperatureInFile(). When this method runs and selects the file for January 20, 2014,
        the method should print out:
            Average temperature in file is 44.93333333333334
    */
    public void testAverageTemperatureInFile() throws ParseException, URISyntaxException, IOException {
        log.debug("testAverageTemperatureInFile() is working.");

        System.out.println(String.format("Average temperature in file is [%s].",
            averageTemperatureInFile("01.06.2014")));
    }

    /**
        Write the method averageTemperatureWithHighHumidityInFile() that has two parameters, a CSVParser 
        named parser and an integer named value. This method returns a double that represents the average 
        temperature of only those temperatures when the humidity was greater than or equal to value. 
    */
    public double averageTemperatureWithHighHumidityInFile(@NonNull String strDate, int humidityThreshold) 
        throws ParseException, URISyntaxException, IOException {

        log.debug(String.format("averageTemperatureWithHighHumidityInFile(): " + 
            "looking for avg temp in date: [%s] with humidity threshold: [%s].",
                strDate, humidityThreshold));

        // processing the found file (the first one/the only one)
        var result = 0.0;
        var counter = 0;

        File file = this.getCSVFileByDate(strDate);
        try (var parser = Utilities.getCSVParser(file)) {

            for (CSVRecord csvRecord: parser) {
                var humidity = getHumidityFromCSVRecord(csvRecord).orElse(0);

                if (humidityThreshold > 0 && humidity >= humidityThreshold) {
                    result = result + getTempFromCSVRecord(csvRecord);
                    counter++;
                }

            } // end of FOR

            if (counter == 0) { // no CSV records with humidity > threshold in CSV file
                return 0.0;
            } else {
                return result / counter;
            }
        } // end of TRY-WITH-RESOURCES

    }

    /** 
        You should also write a void method named testAverageTemperatureWithHighHumidityInFile() to test 
        the method averageTemperatureWithHighHumidityInFile(). When this method runs checking for humidity 
        greater than or equal to 80 and selects the file for January 20, 2014, the method should print out:
            No temperatures with that humidity
        
        If you run the method checking for humidity greater than or equal to 80 and select the file 
        March 20, 2014, a wetter day, the method should print out:
            Average Temp when high Humidity is 41.78666666666667
    */
    public void testAverageTemperatureWithHighHumidityInFile() 
        throws ParseException, URISyntaxException, IOException {

        log.debug("testAverageTemperatureWithHighHumidityInFile() is working.");
        
        // var result1 = averageTemperatureWithHighHumidityInFile("20.01.2014", 80);
        // if (result1 > 0) {
        //     System.out.println(String.format("Average temperature when high Humidity is [%s].", 
        //         result1));
        // } else {
        //     System.out.println("No temperatures with that humidity!");
        // }

        var result2 = averageTemperatureWithHighHumidityInFile("30.03.2014", 80);
        if (result2 > 0) {
            System.out.println(String.format("Average temperature when high Humidity is [%s].", 
                result2));
        } else {
            System.out.println("No temperatures with that humidity!");
        }

    }

    /** */
    public static void main(String[] args) throws URISyntaxException, IOException, ParseException {
        log.info("Weather Analisys CSV Application is starting...");

        // create instance of this class
        var application = new WeatherCSVApplication();

        // application.testColdestHourInFile();                        // test method #1
        // application.testFileWithColdestTemperature();               // test method #2
        // application.testLowestHumidityInFile();                     // test method #3
        // application.testLowestHumidityInManyFiles();                // test method #4
        // application.testAverageTemperatureInFile();                 // test method #5
        application.testAverageTemperatureWithHighHumidityInFile(); // test method #6

        // System.out.println("-> " + WeatherCSVApplication.parseDate("02/10/2022"));
        //System.out.println("\n" + WeatherCSVApplication.parseMonth("10/2022"));

    }

}
