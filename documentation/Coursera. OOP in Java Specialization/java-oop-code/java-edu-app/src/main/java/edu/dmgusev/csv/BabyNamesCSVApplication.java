package edu.dmgusev.csv;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Set;

import org.apache.commons.csv.CSVParser;

import edu.dmgusev.utils.Utils;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BabyNamesCSVApplication {
    
    private static final String BASE_CSV_FOLDER = "babynames";

    private Set<File> fileset = null;

    /** */
    public BabyNamesCSVApplication() throws URISyntaxException, IOException {
        log.debug("Initializing app with base folder [{}].", BASE_CSV_FOLDER);
        this.fileset = Utils.getAllFilesFromResources(BASE_CSV_FOLDER);
    }

    /** */
    private CSVParser getCSVParserFromFile(@NonNull String fileName) {
        log.debug("Returning CSV Parser for file [{}].", fileName);
        var file = this.fileset.stream().filter(item -> item.getName().contains(fileName)).findFirst();

        if (file.isEmpty()) { // fail-fast - no such file found, can't continue
            throw new IllegalStateException(String.format("No such file: [%s]!", fileName));
        }

        return Utils.getCSVParser(file.get(), false); // no CSV header
    }

    /** */
    public void totalBirth(@NonNull String csvFile) {
        log.debug("totalBirth(): processing file [{}].", csvFile);


    }

    /** */
    public static void main(String[] args) throws URISyntaxException, IOException {

        var app = new BabyNamesCSVApplication();
        app.totalBirth("aaa");

    }

}
