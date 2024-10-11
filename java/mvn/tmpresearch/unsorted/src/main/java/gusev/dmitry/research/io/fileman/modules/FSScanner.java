package gusev.dmitry.research.io.fileman.modules;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Recursive file system scanner.
 * Some features:
 *  - exclude files filter is more priveledged, than include filter - if file exists in both filters,
 *    it will be excluded
 *
 * @author Gusev Dmitry (Дмитрий)
 * @version 1.0 (DATE: 08.10.12)
*/

public class FSScanner extends Observable {

    private static Log log = LogFactory.getLog(FSScanner.class);

    private String      scanStartPath;                     // path for start sacnning
    private Set<String> includeFilter = new TreeSet<>();   // include files filter
    private Set<String> excludeFilter = new TreeSet<>();   // exclude files filter (more priveledged, than include filter)
    private List<File>  foundFiles    = new ArrayList<>(); // found files list
    private long        fullSize      = 0;                 // found files fullSize

    /***/
    public FSScanner(String scanStartPath) throws IOException {
        if (StringUtils.isBlank(scanStartPath)) {
            throw new IOException("Scan start path is empty!");
        } else if (!new File(scanStartPath).exists() || !new File(scanStartPath).isDirectory()) {
            throw new IOException(String. format("Scan start path [%s] doesn't exists or not a directory!", scanStartPath));
        }
        this.scanStartPath = scanStartPath;
    }

    /***/
    public FSScanner(String scanStartPath, Collection<String> includeFilter, Collection<String> excludeFilter) throws IOException {
        this(scanStartPath);
        if (includeFilter != null && !includeFilter.isEmpty()) { // add include filter
            log.debug("Exclude filter added.");
            this.includeFilter.addAll(includeFilter);
        }
        if (excludeFilter != null && !excludeFilter.isEmpty()) { // add exclude filter
            log.debug("Include filter added.");
            this.excludeFilter.addAll(excludeFilter);
        }
    }

    /***/
    private void scanFileSystem(String startPath) {
        // create File object
        File pathName = new File(startPath);
        //log.debug("Processing path [" + startPath + "]"); // <- too much output!
        // check current path
        if (pathName.exists() && pathName.isDirectory()) {
            String[] fileNames = pathName.list(); // all files list (in current dir)
            if (fileNames != null) {
                // В цикле проходим по всему списку полученных файлов
                for (String fileName : fileNames) {
                    // Опять создаем объект "файл"
                    File file = new File(pathName.getPath(), fileName);
                    // Если полученный "файл" - снова является каталогом, рекурсивно вызываем данный метод
                    if (file.isDirectory()) {this.scanFileSystem(file.getPath());}
                    // Если же полученный "файл" - файл, то добавляем его в список
                    else if (file.isFile()) {
                        boolean isFileProcessed = false;
                        //log.debug("File -> " + file);
                        // check file in exclude filter
                        if (this.excludeFilter != null && !this.excludeFilter.isEmpty()) {
                            Iterator<String> iterator = this.excludeFilter.iterator();
                            while (iterator.hasNext() && !isFileProcessed) {
                                if (file.getName().trim().toUpperCase().endsWith(iterator.next().toUpperCase())) {
                                    isFileProcessed = true; // mark file as processed (and skip)
                                    //log.debug("Skipped -> " + file);
                                }
                            }
                        }
                        // file isn't processed by exclude filter
                        if (!isFileProcessed) {
                            // we have include filter - check it
                            if (includeFilter != null && !includeFilter.isEmpty()) {
                                Iterator<String> iterator = includeFilter.iterator();
                                while (iterator.hasNext() && !isFileProcessed) {
                                    if (file.getName().trim().toUpperCase().endsWith(iterator.next().toUpperCase())) {
                                        foundFiles.add(file);  // add found file
                                        //log.debug("Added -> " + file);
                                        fullSize += file.length(); // increase total fullSize of found files
                                        isFileProcessed = true; // every found file we will add only once!
                                    }
                                } // end of while (iterating over includeFilter)
                            } else { // no includeFilter - we will add every found file
                                foundFiles.add(file);
                                //log.debug("Added -> " + file);
                                fullSize += file.length();
                            }
                        }
                    } // end of section with found file
                }
            }
        }
    }

    /***/
    public List<File> searchFiles() {
        log.debug("FSScanner.searchFiles() working.");
        // call recursive method
        this.scanFileSystem(this.scanStartPath);
        // return result
        return Collections.unmodifiableList(this.foundFiles);
    }

    /***/
    public long getFullSize() {
        return fullSize;
    }

    /***/
    public static void main(String[] args) throws IOException, InterruptedException {
        Log log = LogFactory.getLog(FSScanner.class);

        //System.out.print("[ 10%]");
        //for (int i = 0; i < 60; i++) {
        //    System.out.print(String.format("[%3s%%]", i));
        //    Thread.sleep(2000);
        //    System.out.print("\r");
        //}
        //System.exit(1);

        FSScanner scanner = new FSScanner("//pst-fs/asu/users/new", Arrays.asList(".lck", ".dbf", ".db"), Arrays.asList(".dbf", ".db"));
        //Set<File> foundFiles = scanner.searchFiles("//pst-fs/asu/users/new/spz");
        List<File> foundFiles = scanner.searchFiles();

        System.out.println("Found files -> " + foundFiles.size() + ". Size: " + scanner.getFullSize()/1024 + " Kb");
        System.out.println(foundFiles);
        //System.exit(1);

    }

}