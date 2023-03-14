package edu.dmgusev.utils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

// todo: see: https://www.baeldung.com/java-list-directory-files
// todo: see: https://www.logicbig.com/how-to/java/list-all-files-in-resouce-folder.html
// todo: see: https://mkyong.com/java/java-read-a-file-from-resources-folder/

// todo: replace System.out with the logger!

@Slf4j
@NoArgsConstructor // (access = AccessLevel.PRIVATE)
// we should have a public constructor as we use reference 'this' for getting current class (getClass())
public class Utilities {

    /** (-) Method I. List files in a given directory. Can't find relative/resources dirs. */
    public static Set<File> listFilesUsingJavaIO(@NonNull String dir) {
        log.info(String.format("[Java IO] Listing files for dir [%s].", dir));

        return Stream.of(new File(dir).listFiles())
                .filter(file -> !file.isDirectory())
                //.map(File::getName) // <- result is Set<String> (extract files names)
                .collect(Collectors.toSet());

    }

    /** (-) Method II. List files in a given directory. Can't find relative/resources dirs. */
    public static Set<File> listFilesUsingFilesList(@NonNull String dir) throws IOException {
        log.info(String.format("[Files List] Listing files for dir [%s].", dir));

        try (Stream<Path> stream = Files.list(Paths.get(dir))) { // try-with-resources
            return stream
                    .filter(file -> !Files.isDirectory(file))
                    .map(Path::getFileName)
                    //.map(Path::toString) // <- result is Set<String> (extract files names)
                    .map(Path::toFile) // <- convert Path instance to File instance
                    .collect(Collectors.toSet());
        }

    }

    /** Method III: this method should be able to show [resources] folder content (list of files). */
    public static File[] getResourceFolderFiles(String folder) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        URL         url    = loader.getResource(folder);
        String      path   = url.getPath();
        return new File(path).listFiles();
    }

    /** (+) Method IV: this method is able to see files in the directory inside [resources] dir.  */
    public Set<File> getAllFilesFromResource(String folder) throws URISyntaxException, IOException {

        ClassLoader classLoader = this.getClass().getClassLoader();
        URL resource = classLoader.getResource(folder);

        // do walk the root path, we will walk all the classes
        try (Stream<Path> stream = Files.walk(Paths.get(resource.toURI()))) {
            return stream
                .filter(Files::isRegularFile)
                .map(Path::toFile)
                .collect(Collectors.toSet());
        }

    }

    // print a file
    public static void printFile(@NonNull File file) {

        try {
            var lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
            lines.forEach(System.out::println);
        } catch (IOException e) {
            log.error(String.format("Can't read file: [%s].", file), e);
        }

    }

    public static void main(String[] args) throws IOException {

        // todo: implement tests for all methods

        var app = new Utilities();

        // read all files from a resources folder
        try {

            // methods references to static methods
            //Function<String, Set<File>> staticMethod1 = Utilities::listFilesUsingJavaIO;

            // files from src/main/resources/json
            Set<File> result = app.getAllFilesFromResource("dna");
            for (File file : result) {
                log.info(String.format("Found file: [%s]", file)); // print file name
                // printFile(file); // print file content
            }

        } catch (URISyntaxException | IOException e) {
            log.error("Processing error!", e);
        }

    }

}
