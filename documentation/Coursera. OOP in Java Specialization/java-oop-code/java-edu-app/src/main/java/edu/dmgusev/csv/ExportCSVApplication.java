package edu.dmgusev.csv;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Currency;
import java.util.Locale;

import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import edu.duke.FileResource;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExportCSVApplication {

    // sample CSV files
    public static final String CSV_EXPORTDATA       = "csv/exportdata.csv";
    public static final String CSV_EXPORTS_SAMPLE   = "csv/exports_sample.csv";
    public static final String CSV_EXPORTS_SMALL_MS = "csv/exports_small_ms.csv";
    public static final String CSV_EXPORTS_SMALL    = "csv/exports_small.csv";

    /** */
    private static CSVParser getCSVParser(@NonNull String csvFile) {
        log.debug(String.format("Preparing CSV Parser for [%s].", csvFile));
        return new FileResource(csvFile).getCSVParser();
    }

    /** Print content of the CSV file (record by record). */
    public void tester(@NonNull String csvFile) throws IOException {
        log.debug("tester() method is working.");

        try (var parser = getCSVParser(csvFile)) {
            for (CSVRecord csvRecord: parser) {
                System.out.println(csvRecord);
            }
        }

    }

    /** 
        Write a method named countryInfo that has two parameters, parser is a CSVParser and country 
        is a String. This method returns a string of information about the country or returns “NOT FOUND” 
        if there is no information about the country. The format of the string returned is the country, 
        followed by “: “, followed by a list of the countries’ exports, followed by “: “, followed by the 
        countries export value.

        For example, using the file exports_small.csv and the country Germany, the program returns the 
        string: [Germany: motor vehicles, machinery, chemicals: $1,547,000,000,000]
    */
    public void countryInfo(@NonNull String csvFile, @NonNull String country) throws IOException {
        log.debug(String.format("countryInfo(): CSV file [%s], country [%s].", 
            csvFile, country));

        try (var parser = getCSVParser(csvFile)) {
            for (CSVRecord csvRecord: parser) { // iterate over CSV file records and process

                String csvCountry = csvRecord.get("Country"); // get country value
                if (csvCountry.equalsIgnoreCase(country)) {

                    // get other values from CSV
                    String csvExports = csvRecord.get("Exports");
                    String csvValue   = csvRecord.get("Value (dollars)");

                    // print the result
                    System.out.println(String.format("%s: %s: %s", csvCountry, csvExports, csvValue));

                    return; // when we've found a country - immediate exit
                }
            } // end of FOR
        } // end of try-with-resources

        System.out.println(String.format("Country [%s] NOT FOUND!", country));

    }

    /**
        Write a void method named listExportersTwoProducts that has three parameters, parser is a 
        CSVParser, exportItem1 is a String and exportItem2 is a String. This method prints the 
        names of all the countries that have both exportItem1 and exportItem2 as export items. 

        For example, using the file exports_small.csv, this method called with the items “gold” 
        and “diamonds” would print the countries: Namibia, South Africa.
    */
    public void listExportersTwoProducts(@NonNull String csvFile,
        @NonNull String exportItem1, @NonNull String exportItem2) throws IOException {

        log.debug("listExportersTwoProducts() is working.");

        boolean found = false;
        try (var parser = getCSVParser(csvFile)) {

            for (CSVRecord csvRecord: parser) {
                String exports = csvRecord.get("Exports").toUpperCase();

                if (exports.contains(exportItem1.toUpperCase()) &&
                        exports.contains(exportItem2.toUpperCase())) {

                    found = true; // set flag
                    String country = csvRecord.get("Country");
                    System.out.println(String.format("found: %s -> exports %s and %s",
                        country, exportItem1, exportItem2));

                }

            }

        }

        if (!found) { // nothing found - just inform about it
            System.out.println(String.format("Country for exports [%s, %s] NOT FOUND!",
                exportItem1, exportItem2));
        }

    }

    /** 
        Write a method named numberOfExporters, which has two parameters, parser is a CSVParser, and 
        exportItem is a String. This method returns the number of countries that export exportItem.

        For example, using the file exports_small.csv, this method called with the item “gold” 
        would return 3.
    */
    public void numberOfExporters(@NonNull String csvFile, @NonNull String exportItem) throws IOException {
        log.debug("numberOfExporters() is working.");

        var counter = 0;
        try (var parser = getCSVParser(csvFile)) {

            for (CSVRecord csvRecord: parser) {
                String exports = csvRecord.get("Exports").toUpperCase();
                if (exports.contains(exportItem.toUpperCase())) {
                    counter++;
                }
            }

        }

        System.out.println(String.format("Found exporters of [%s]: %s", exportItem, counter));

    }

    /** 
        Write a void method named bigExporters that has two parameters, parser is a CSVParser, and amount 
        is a String in the format of a dollar sign, followed by an integer number with a comma separator 
        every three digits from the right. An example of such a string might be “$400,000,000”. This method 
        prints the names of countries and their Value amount for all countries whose Value (dollars) 
        string is longer than the amount string. You do not need to parse either string value as an 
        integer, just compare the lengths of the strings.

        For example, if bigExporters is called with the file exports_small.csv and amount with the 
        string $999,999,999, then this method would print eight countries and their export values shown here:
            Germany $1,547,000,000,000
            Macedonia $3,421,000,000
            Malawi $1,332,000,000
            Malaysia $231,300,000,000
            Namibia $4,597,000,000
            Peru $36,430,000,000
            South Africa $97,900,000,000
            United States $1,610,000,000,000
    */
    public void bigExporters(@NonNull String csvFile, @NonNull String usDollarsAmount) 
        throws IOException, ParseException {

        log.debug("bigExporters() is working.");

        // create US dollars amount formatter (num <-> str) and currency instance
        Locale usa = new Locale("en", "US"); // new Locale
        Currency dollars = Currency.getInstance(usa); // new Currency instance for the Locale
        NumberFormat dollarFormat = NumberFormat.getCurrencyInstance(usa); // new formatter given the Locale

        // todo: just a simple sample... (feel free to remove)
        // Number num = dollarFormat.parse("$400,000,000.21");
        // String strNum = dollarFormat.format(num);
        // System.out.println(dollars.getDisplayName() + ": " + num);
        // System.out.println(dollars.getDisplayName() + ": " + strNum);

        // convert provided amount to number 
        Number providedAmount = dollarFormat.parse(usDollarsAmount);

        boolean found = false;
        try (var parser = getCSVParser(csvFile)) {

            for (CSVRecord csvRecord: parser) {
                String amount = csvRecord.get("Value (dollars)");
                Number numAmount = dollarFormat.parse(amount);

                if (numAmount.doubleValue() > providedAmount.doubleValue()) {
                    found = true;
                    System.out.println(String.format("%s %s", csvRecord.get("Country"), amount));
                }

            }

        }

        if (!found) { // nothing found - just inform about it
            System.out.println(
                String.format("Countries with export value greater than [%s] NOT FOUND!",
                    usDollarsAmount));
        }

    }

    /***/
    public static void main(String[] args) throws IOException, ParseException {
        log.info("Export Analisys CSV Application is starting...");

        var application = new ExportCSVApplication();

        // printing content of the provided CSV file
        //log.info(String.format("Printing content of the file: [%s]%n", CSV_EXPORTS_SAMPLE));
        //application.tester(CSV_EXPORTS_SAMPLE);

        // testing method countryInfo()
        // application.countryInfo(CSV_EXPORTS_SMALL, "Germany");
        //application.countryInfo(CSV_EXPORTDATA, "Nauru");

        // testing method listExportersTwoProducts()
        // application.listExportersTwoProducts(CSV_EXPORTS_SMALL, "gold", "diamonds");
        // application.listExportersTwoProducts(CSV_EXPORTDATA, "gold", "diamonds");

        // testing method numberOfExporters()
        //application.numberOfExporters(CSV_EXPORTS_SMALL, "gold");
        //application.numberOfExporters(CSV_EXPORTDATA, "sugar");

        // testing method bigExporters()
        // application.bigExporters(CSV_EXPORTS_SMALL, "$999,999,999");
        application.bigExporters(CSV_EXPORTDATA, "$999,999,999,999");

    }

}
