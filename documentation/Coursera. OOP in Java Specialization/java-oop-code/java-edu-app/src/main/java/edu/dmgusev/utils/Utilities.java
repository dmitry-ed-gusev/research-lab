package edu.dmgusev.utils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.NoArgsConstructor;

// todo: see: https://www.baeldung.com/java-list-directory-files
// todo: see: https://www.logicbig.com/how-to/java/list-all-files-in-resouce-folder.html
// todo: see: https://mkyong.com/java/java-read-a-file-from-resources-folder/

@NoArgsConstructor // (access = AccessLevel.PRIVATE)
public class Utilities {

    /***/
    public static Set<String> listFilesUsingJavaIO(String dir) {
        return Stream.of(new File(dir).listFiles())
                .filter(file -> !file.isDirectory())
                .map(File::getName)
                .collect(Collectors.toSet());
    }

    /***/
    public static Set<String> listFilesUsingFilesList(String dir) throws IOException {
        try (Stream<Path> stream = Files.list(Paths.get(dir))) {
            return stream
                    .filter(file -> !Files.isDirectory(file))
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .collect(Collectors.toSet());
        }
    }

    /** This method should be qble to show [resources] folder content (list of files). */
    public static File[] getResourceFolderFiles(String folder) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        URL url = loader.getResource(folder);
        String path = url.getPath();
        return new File(path).listFiles();
    }

    /***/
    public List<File> getAllFilesFromResource(String folder) throws URISyntaxException, IOException {

        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource(folder);

        // dun walk the root path, we will walk all the classes
        return Files.walk(Paths.get(resource.toURI()))
                .filter(Files::isRegularFile)
                .map(Path::toFile)
                .collect(Collectors.toList());
    }

    // print a file
    public static void printFile(File file) {

        List<String> lines;
        try {
            lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
            lines.forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) throws IOException {

        var app = new Utilities();

        // read all files from a resources folder
        try {

            // files from src/main/resources/json
            List<File> result = app.getAllFilesFromResource("dna");
            for (File file : result) {
                System.out.println("file : " + file);
                // printFile(file);
            }

        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }

    }

}
