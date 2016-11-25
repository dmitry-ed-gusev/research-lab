package gusev.dmitry.research.io.fileman.modules;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

/**
 * @author Gusev Dmitry (gusevd)
 * @version 1.0 (DATE: 13.01.14)
*/

public class FSCopier extends Observable {

    private Log log = LogFactory.getLog(FSCopier.class);

    // internal object state
    private String             sourceDirectory;
    private String             destDirectory;
    private Collection<String> includeFilter;
    private Collection<String> excludeFilter;


    /***/
    public FSCopier(String sourceDirectory, String destDirectory,
                    Collection<String> includeFilter, Collection<String> excludeFilter) throws IOException {
        // both source and directory should be not empty strings
        if (StringUtils.isBlank(sourceDirectory) || StringUtils.isBlank(destDirectory)) {
            throw new IOException(String.format("Source [%s] or destination [%s] is empty!", sourceDirectory, destDirectory));
        }
        this.sourceDirectory = StringUtils.trimToEmpty(sourceDirectory);
        this.destDirectory   = StringUtils.trimToEmpty(destDirectory);
        this.includeFilter   = (includeFilter == null ? Collections.<String>emptyList() : Collections.unmodifiableCollection(includeFilter));
        this.excludeFilter   = (excludeFilter == null ? Collections.<String>emptyList() : Collections.unmodifiableCollection(excludeFilter));
    }

    /***/
    public FSCopier(String sourceDirectory, String destDirectory) throws IOException {
        this(sourceDirectory, destDirectory, null, null);
    }

    /***/
    public void copy(boolean preserveDirTree) throws IOException {
        log.debug("FSCopier.copy() working");

        log.debug("Observers count -> " + this.countObservers());

        // file objects for both source/destination
        File destFile   = new File(this.destDirectory);
        File sourceFile = new File(this.sourceDirectory);
        // destination should be directory (if exists) - check
        if (destFile.exists() && !destFile.isDirectory()) {
            throw new IOException(String.format("Destination [%s] exists but is not a directory!", this.destDirectory));
        } else if (!sourceFile.exists()) { // check source - does it exist?
            throw new IOException(String.format("Source [%s] doesn't exist!", this.sourceDirectory));
        }

        String middlePathStr; // destination catalog for copying
        if (preserveDirTree) { // we will preserve whole source dir tree (path)
            log.debug("Preserve dir tree [TRUE].");
            // preserve all directories in source path
            //Path sourcePath = Paths.get(this.sourceDirectory);
            //Path destPath   = Paths.get(this.destDirectory);
            if (this.sourceDirectory.contains(":")) { // source is local path
                middlePathStr = "";
                /*
                middlePathStr = destPath.toString() + (destPath.toString().endsWith("\\") ? "" : "\\") +
                        sourcePath.subpath(0, (sourceFile.isFile() ? sourcePath.getNameCount() - 1 : sourcePath.getNameCount()));
                */
            } else { // source is network path
                middlePathStr = StringUtils.strip(StringUtils.strip(Paths.get(this.sourceDirectory).getRoot().toString(), "\\"), "/");
                /*
                middlePathStr = destPath.toString() + (destPath.toString().endsWith("\\") ? "" : "\\") +
                        StringUtils.strip(StringUtils.strip(sourcePath.toString(), "\\"), "/");
                if (sourceFile.isFile()) { // cut trailing file name from destination path
                    Path tmpPath = Paths.get(middlePathStr);
                    middlePathStr = tmpPath.getRoot().toString() + tmpPath.subpath(0, tmpPath.getNameCount() - 1).toString();
                }
                */
            }
        } else { // we won't preserve source dir tree (simple copy)
            log.debug("Preserve dir tree [FALSE].");
            middlePathStr = "";
            //middlePathStr = this.destDirectory;
        }

        // processed destination catalog
        log.debug(String.format("Middle path -> [%s]", middlePathStr));
        String destinationRoot = this.destDirectory + (this.destDirectory.endsWith("\\") ? "" : "\\") + middlePathStr +
                (middlePathStr.endsWith("\\") ? "" : "\\");
        log.debug(String.format("Destination path -> [%s].", destinationRoot));

        // if destination catalog doesn't exists - we will create it
        //if (!new File(middlePathStr).exists()) {
        //     boolean result = new File(middlePathStr).mkdirs();
        //    log.info(String.format("Destination [%s] doesn't exist, but created [%s].", middlePathStr, result));
        //}

        // if we copy directory - we have to scan it for files
        if (sourceFile.isDirectory()) {
            FSScanner scanner = new FSScanner(this.sourceDirectory, this.includeFilter, this.excludeFilter);
            List<File> foundFiles = scanner.searchFiles();
            log.debug("Found file(s) for copying " + foundFiles.size());
            Iterator<File> iterator = foundFiles.iterator();

            int count             = Paths.get(this.sourceDirectory).getNameCount();
            int counter           = 0;                 // processed files counter
            int allFilesCounter   = foundFiles.size(); // all files number
            int percentsCompleted         = 0;
            int previousPercentsCompleted = 0;
            while (iterator.hasNext()) { // cycle for files copying
                File file = iterator.next();
                Path path = Paths.get(file.getParent());

                String currentDestDir = destinationRoot +
                        (preserveDirTree ? path.subpath(0, path.getNameCount()).toString() :
                                path.subpath(count - 1, path.getNameCount()));

                //log.debug("processing -> [" + file + "] -> " + file.getParent());
                //Path path = Paths.get(file.getParent());
                //log.debug("-> " + path.getRoot() + " | " + path.subpath(0, path.getNameCount()));

                //log.info("-> " + file.getName() + " | " + file.getAbsolutePath());
                //Path source = Paths.get(file.getAbsolutePath());
                //Path dest = Paths.get(BACKUP_DIR + source);
                //new File(dest.getParent().toString()).mkdirs();
                //Path source = Paths.get(file.getAbsolutePath());
                //Path dest = Paths.get(BACKUP_DIR + source);
                //new File(dest.getParent().toString()).mkdirs();
                //Files.copy(source, dest, StandardCopyOption.REPLACE_EXISTING);

                try {
                    FileUtils.copyFileToDirectory(file, new File(currentDestDir));
                    //Files.copy(Paths.get(file.getAbsolutePath()), Paths.get(realDestinationStr), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    log.error(String.format("Can't copy [%s] to [%s]!", file, middlePathStr), e);
                }
                        //log.info("file -> " + path.getFileName() + " parent -> " + path.getParent());
                        //System.out.println(path.getNameCount());
                        //System.out.println(path.subpath(-1, 5));
                        //System.out.println(BACKUP_DIR + path.getRoot());
                        //log.info(new File(BACKUP_DIR + path.getRoot()).mkdirs());

                counter++;                                       // update processed files counter
                percentsCompleted = counter*100/allFilesCounter; // completed percents
                // Update observers -> send info about processing %. Update every 10%
                if (percentsCompleted > 0 && percentsCompleted != previousPercentsCompleted && percentsCompleted%10 == 0) {
                    //log.debug("Notifying Observers -> " + percentsCompleted);
                    previousPercentsCompleted = percentsCompleted;
                    this.setChanged();                        // mark this object as 'changed'
                    this.notifyObservers(percentsCompleted); // notify registered observers
                }

            } // end of while - copying files

        } else { // we will copy just file
            throw new UnsupportedOperationException("File copy is unsupported now!");
        }

    }

    /***/
    public static void main(String[] args) throws IOException {
        Log log = LogFactory.getLog(FSCopier.class);
        log.info("Files copier started.");

        FSCopier copier = new FSCopier("//pst-fs/asu/users/new/asu", "c:/temp/backup1");
        //FSCopier copier = new FSCopier("d:/mydocs/projects/javaresearch", "c:/temp/backup2");
        copier.copy(false);
        //FSZipper zip = new FSZipper();
        //zip.zipFiles("c:/temp/backup1", "c:/temp/backup.zip");
        log.info("finished!");
        System.exit(1);

        //File ff = new File("c:/temp/PMesV2/pom.xml");
        File ff = new File("//pst-fs/asu/USERS/new/spz");
        log.info(ff.getAbsolutePath());
        log.info(ff.getCanonicalPath());
        log.info(ff.getPath());

        String str = ff.getParent();
        log.info(str);


        Path path = ff.toPath();
        log.info(path.getParent());

        log.info("root -> " + path.getRoot());
        String splitString = "//";
        log.info("split string -> " + splitString);
        String[] zzz = path.getRoot().toString().split(splitString);
        if (zzz.length > 1) {
            //log.info(zzz[zzz.length - 2]);
            //log.info(zzz[zzz.length - 1]);
            //log.info(zzz[0] + "|" + zzz[1] + "|" + zzz[2] + "|" + zzz[3]);
            log.info("network");

            for (String s : zzz) {
                log.info("-> |" + s + "|");
            }
        } else {
            log.info("local");
        }
        //log.info("-> " + path.getName(0));
        //log.info(path.getNameCount());

        System.exit(1);

        FSScanner scanner = new FSScanner("//pst-fs/asu/users/new/asu");
        List<File> foundFiles = scanner.searchFiles();
        //Set<File> foundFiles = scanner.searchFiles("//pst-fs/asu/users/new");

        Iterator<File> iterator = foundFiles.iterator();
        while (iterator.hasNext()) {
            File file = iterator.next();

            log.info("-> " + file.getName() + " | " + file.getAbsolutePath());
            Path source = Paths.get(file.getAbsolutePath());
            Path dest = Paths.get("c:/temp/backup/fdfdfdfdf");
            //log.debug("----> " + new File(dest.getParent().toString()).mkdirs());
            Path parentDir = dest.getParent();
            if (!Files.exists(parentDir))
                Files.createDirectories(parentDir);
            Files.copy(source, dest, StandardCopyOption.REPLACE_EXISTING);

            //log.info("file -> " + path.getFileName() + " parent -> " + path.getParent());
            //System.out.println(path.getNameCount());
            //System.out.println(path.subpath(-1, 5));
            //System.out.println(BACKUP_DIR + path.getRoot());
            //log.info(new File(BACKUP_DIR + path.getRoot()).mkdirs());

        }

        //FSZipper zip = new FSZipper();
        //zip.zipFiles(BACKUP_DIR, "d:/zzz.zip");
        //log.info("finished!");

        //Files.copy()
        //System.out.println(foundFiles);
        //for (File file : foundFiles) {
        //    System.out.println(file);
        //}
    }

}