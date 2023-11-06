package edu.dmgusev.utils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import edu.duke.FileResource;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor (access = AccessLevel.PRIVATE)
// we should have a public constructor as we use reference 'this' for getting current class (getClass())
public class Utils {

    /** Method lists files in a given absolute path directory. Can't find relative/resources dirs. */
    public static Set<File> getAllFilesFromDirectory(@NonNull String dir) 
        throws IOException {

        log.debug("Listing files for: {}", dir);

        try (Stream<Path> stream = Files.walk(Paths.get(dir))) { // try-with-resources
            return stream
                    .filter(Files::isRegularFile)
                    //.map(Path::getFileName)
                    .map(Path::toFile)
                    .collect(Collectors.toSet());
        } // end of TRY-WITH-RESOURCES
    }

    /** 
        Method is able to see files in the directory inside [resources] dir of the current ClassLoader and 
        return list of the files (true File objects). It always search for the directory inside the 
        [resources] folder.
    */
    public static Set<File> getAllFilesFromResources(@NonNull String folder) 
        throws URISyntaxException, IOException {

        log.debug("Listing files for: {}", folder);
        
        // get the current ClassLoader
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        // get the relative folder name as a resource folder for this ClassLoader
        URL resource = classLoader.getResource(folder);
        log.debug("Got path: {}", Paths.get(resource.toURI()));

        // do walk the root path, we will walk all the classes
        try (Stream<Path> stream = Files.walk(Paths.get(resource.toURI()))) {
            return stream
                .filter(Files::isRegularFile)
                .map(Path::toFile)
                .collect(Collectors.toSet());
        } // end of TRY-WITH-RESOURCES
    }

    /** Print content of a file to std output, all lines. */
    public static void printFile(@NonNull File file) {
        log.debug("Printing content of the file: [{}].", file.getName());
        try {
            var lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
            lines.forEach(System.out::println);
        } catch (IOException e) {
            log.error(String.format("Can't read file: [%s].", file), e);
        }
    }

    /** Get CSV Reader from string file path. */
    public static CSVParser getCSVParser(@NonNull String csvFile, boolean withHeader) {
        log.debug(String.format("Preparing CSV Parser for [%s].", csvFile));
        return new FileResource(csvFile).getCSVParser(withHeader);
    }

    /** */
    public static CSVParser getCSVParser(@NonNull String csvFile) {
        return Utils.getCSVParser(csvFile, true);
    }

    /** Get CSV Reader from File object. */
    public static CSVParser getCSVParser(@NonNull File file, boolean withHeader) {
        log.debug(String.format("Preparing CSV Parser for [%s].", file));
        return new FileResource(file).getCSVParser(withHeader);
    }

    /** */
    public static CSVParser getCSVParser(@NonNull File file) {
        return Utils.getCSVParser(file, true);
    }

    /** Return column value as list of string, from CSV Parser object. */
    private static List<String> getColumnValuesFromCSV(@NonNull CSVParser csvParser, 
        @NonNull String columnName) {

        log.debug(String.format("Getting value of the column [%s] from the CSV Parser.", columnName));

        List<String> result = new ArrayList<>();
        for (CSVRecord csvRecord: csvParser) {
            result.add(csvRecord.get(columnName));
        } // end of FOR

        return result;
    }

    /** */
    public static List<String> getColumnValuesFromCSVFile(@NonNull String csvFile, @NonNull String columnName) {
        return getColumnValuesFromCSV(getCSVParser(csvFile), columnName);
    }

    /** */
    public static List<String> getColumnValuesFromCSVFile(@NonNull File csvFile, @NonNull String columnName) {
        return getColumnValuesFromCSV(getCSVParser(csvFile), columnName);
    }

    /** */
    public static String getCollectionAsColumnFormatted(@NonNull Collection<?> collection) {
        log.debug("Formatting collection into a column string.");
        return collection.stream().map(Object::toString).collect(Collectors.joining("\n"));
    }

    /** */
    public static void main(String[] args) throws IOException, URISyntaxException {

        var files = Utils.getAllFilesFromResources("dna");
        var str = Utils.getCollectionAsColumnFormatted(files);

        System.out.println("\n" + str + "\n");

    }

}
