package dgusev.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.zip.CRC32;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import lombok.NonNull;
import lombok.extern.apachecommons.CommonsLog;

/** Some useful IO utilities. */

@CommonsLog
@NotThreadSafe
public final class MyIOUtils {

    private static final JSONParser JSON_PARSER                   = new JSONParser();
    private static final String     SELECT_SQL                    = "SELECT * FROM %s"; // SQL query: get data from source DB
    private static final char[]     DEPRECATED_DELIMITERS         = {'\\'};
    private static final char       DEFAULT_DIR_DELIMITER         = '/';
    private static final String     SERIALIZED_OBJECT_EXTENSION_1 = "_1_";
    private static final String     SERIALIZED_OBJECT_EXTENSION_2 = "_2_";
    private static final String     ZIPPED_OBJECT_EXTENSION       = ".zip";
    private static final int        FILE_BUFFER                   = 32768;

    private MyIOUtils() {}

    /**
     * Method is trying to delete specified file.
     * @param fileName String file for deletion
     * @param failOnDelete boolean if true - throws IOException if file can't be deleted
     */
    public static void deleteFileIfExists(@NonNull String fileName, boolean failOnDelete) throws IOException {
        log.debug(String.format("MyIOUtils.deleteFileIfExist() is working. File [%s].", fileName));

        File file = new File(fileName);
        if (file.exists()) {
            boolean isDeleteOK = file.delete();
            log.info(String.format("File [%s] exists. Remove it -> [%s].", fileName, isDeleteOK ? "OK" : "Fail"));
            if (!isDeleteOK) { // if can't delete - throw an exception or write a log message
                if (failOnDelete) {
                    throw new IOException(String.format("Cant't delete file [%s] by unknown reason!", fileName));
                } else {
                    log.error(String.format("Cant't delete file [%s] by unknown reason!", fileName));
                }
            } // end of IF - fail on deletion
        } else {
            log.debug(String.format("File [%s] doesn't exist.", fileName));
        } // end of main IF statement
    }

    /** Read simple long value from file (file can be edited with with any editor). */
    public static long readLongFromFile(@NonNull String filePath) throws IOException {
        log.info(String.format("MyIOUtils.readLongFromFile() is working. Read long from [%s].", filePath));
        // reading from file
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            return Long.parseLong(br.readLine());
        }
    }

    /** Write simple long value to file (file can be edited with with any editor). */
    public static void writeLongToFile(long value, @NonNull String fileName, boolean overwrite) throws IOException {
        log.info(String.format("MyIOUtils.writeLongToFile() is working. Write long [%s] to file [%s].", value, fileName));

        // overwrite file (if specified) - and fail on deletion error
        if (overwrite) {
            MyIOUtils.deleteFileIfExists(fileName, true);
        }

        // write value to file
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(fileName)))) {
            out.println(value);
        }
    }

    /** Reads access token and its date from specified file. If file doesn't exist throw exception. */
    public static Pair<Date, String> readDatePairFromFile(@NonNull String tokenFile, @NonNull SimpleDateFormat format)
            throws IOException, ParseException {
        log.debug(String.format("MyIOUtils.readDatePairFromFile() working. Read from [%s].", tokenFile));

        // todo: remove this unnecessary code??? check!
        if (StringUtils.isBlank(tokenFile)) { // fail-fast
            throw new IllegalArgumentException("File name is null!");
        }

        // reading token from file
        try (BufferedReader br = new BufferedReader(new FileReader(tokenFile))) {
            Date tokenDate = format.parse(br.readLine()); // first line of file
            String token = br.readLine();                 // second line of file
            return new ImmutablePair<>(tokenDate, token);
        }
    }

    /**
     * Writes access token and its date from specified file.
     * If file already exist - throw exception or overwrite it (if overwrite = true).
     */
    public static void writeDatePairToFile(@NonNull Pair<Date, String> token, @NonNull SimpleDateFormat format,
                                    @NonNull String fileName, boolean overwrite) throws IOException {
        log.debug(String.format("MyIOUtils.writeDatePairToFile() is working. " +
                "Pair: [%s], file: [%s], overwrite: [%s].", token, fileName, overwrite));

        // check input parameters - fail-fast
        if (token.getLeft() == null || StringUtils.isBlank(token.getRight()) || StringUtils.isBlank(fileName)) {
            throw new IllegalArgumentException(
                    String.format("Empty pair (or its part): [%s] or pair file name: [%s]!", token, fileName));
        }

        if (overwrite) { // overwrite file
            MyIOUtils.deleteFileIfExists(fileName, true);
        }

        // write token to file
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(fileName)))) {
            out.println(format.format(token.getLeft()));
            out.println(token.getRight());
        }
    }

    /**
     * Saves string to file with specified or auto-generated file name (based on time).
     * Returns file name.
     * If received string is empty throws run-time exception.
     */
    public static void writeStringToFile(@NonNull String string, @NonNull String fileName, boolean overwrite) throws IOException {
        log.debug(String.format("MyIOUtils.writeStringToFile() is working. Write to [%s].", fileName));

        if (StringUtils.isBlank(string) || StringUtils.isBlank(fileName)) {
            throw new IllegalArgumentException(
                    String.format("String to save [%s] and/or file name [%s] is empty!", string, fileName));
        }

        if (overwrite) {
            MyIOUtils.deleteFileIfExists(fileName, true);
        }

        // write data to file
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(fileName)))) {
            out.print(string); // write data to file
        }
    }

    /***/
    // todo: https://howtodoinjava.com/java/io/java-read-file-to-string-examples/
    public static String readStringFromFile(@NonNull String filePath) throws IOException {
        log.debug(String.format("MyIOUtils.readStringFromFile() is working. Read from [%s].", filePath));

        StringBuilder strBuilder = new StringBuilder();
        try (BufferedReader strReader = new BufferedReader(new FileReader(filePath))) {
            String tmpStr;
            while ((tmpStr = strReader.readLine()) != null) {
                strBuilder.append(tmpStr).append("\n");
            }
        }
        return strBuilder.toString();
    }


    /** Dump sql ResultSet to CSV. Reworked original implementation with intermediate progress output. */
    // todo: move to some dbpilot module
    public static void dumpResultSetToCSV(String csvFile, int reportStep, ResultSet rs, String tableName) throws IOException, SQLException {
        log.debug(String.format("MyIOUtils.dumpResultSetToCSV() is working. CSV file [%s].", csvFile));

        // with commons-csv. write CSV from ResultSet with header from ResultSet
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(csvFile));
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(rs))) {

            // init
            long counter     = 0;
            int  columnCount = rs.getMetaData().getColumnCount();
            // iterate over result set and dump it to CSV
            while (rs.next()) {

                for (int i = 1; i <= columnCount; i++) { // write one record to CSV file
                    csvPrinter.print(rs.getObject(i));
                }
                csvPrinter.println();

                counter++; // records counter
                if (counter % reportStep == 0) { // log progress
                    log.info(String.format("[%s] -> %s records exported.", StringUtils.trimToNull(tableName), counter));
                }

            } // end of while for the whole ResultSet

            csvPrinter.flush();
            log.info(String.format("[%s] -> %s records exported in total.", StringUtils.trimToNull(tableName), counter));
        }

        log.info(String.format("[%s] -> successfully exported.", StringUtils.trimToNull(tableName)));
    }

    /***/
    // todo: move to some dbpilot module
    public static void dumpDBToCSV(@NonNull Connection connection, int fetchSize, int reportStep,
                                   @NonNull String[] tablesList, @NonNull String dumpDir) throws SQLException, IOException {
        log.debug("MyIOUtils.dumpDBToCSV() is working.");

        try (Statement stmt = connection.createStatement()) {
            stmt.setFetchSize(fetchSize); // mandatory parameter to speed up dumping db

            String csvFile;
            for (String table : tablesList) {  // iterate over tables to export
                log.info(String.format("[%s] -> export started.", StringUtils.trimToNull(table)));

                try (ResultSet rs = stmt.executeQuery(String.format(SELECT_SQL, StringUtils.trimToNull(table)))) {
                    log.debug("Got ResultSet, starting output to CSV.");
                    // write one CSV for one table
                    csvFile = dumpDir + "/" + StringUtils.trimToNull(table) + ".csv";
                    MyIOUtils.dumpResultSetToCSV(csvFile, reportStep, rs, table); // dump ResultSet to CSV file
                } catch (SQLException e) {
                    log.error(String.format("Can't export table [%s]! Skipped.", table), e);
                } // end of internal TRY statement (with result set)

            } // end of FOR

        } // end of external TRY statement (with connection and statement)

    }

    /***/
    public static JSONObject readJsonObjectFromFile(@NonNull String jsonFile) throws IOException, org.json.simple.parser.ParseException {
        log.debug(String.format("MyIOUtils.readJsonObjectFromFile() is working. Read from [%s].", jsonFile));
        return (JSONObject) JSON_PARSER.parse(new FileReader(jsonFile));
    }

    /***/
    public static JSONArray readJsonArrayFromFile(@NonNull String jsonFile) throws IOException, org.json.simple.parser.ParseException {
        log.debug(String.format("MyIOUtils.readJsonObjectFromFile() is working. Read from [%s].", jsonFile));
        return (JSONArray) JSON_PARSER.parse(new FileReader(jsonFile));
    }

    /**
     * Метод удаляет дерево каталогов, начиная с указанного пути dir (этот каталог также будет удален). Если указан не
     * каталог, а файл - он просто будет удален. Если указан несуществующий путь - ничего не произойдет. Если удаляемый
     * каталог или файл занят другой программой, то будет выдано сообщение об ошибке и данный объект будет пропущен (ИС не
     * будет возбуждена).
     *
     * @param dir String каталог или файл для удаления.
     */
    public static void delTree(String dir) {
        // Создали объект "файл" для переданного в качестве параметра пути к каталогу
        File pathName = new File(dir);

        // Если указанный каталог существует - работаем
        if (pathName.exists()) {
            // Если данный "файл" - каталог - нужно удалить в нем все файлы и продолжить рекурсию
            if (pathName.isDirectory()) {
                // Получаем список всех файлов в данном каталоге
                String[] fileNames = pathName.list();
                // Если список файлов пуст - удаляем каталог
                if ((fileNames == null) || (fileNames.length <= 0)) {
                    log.debug("DELTREE: deleting dir [" + pathName.getPath() + "].");
                    if (!pathName.delete()) log.error("Can't delete dir [" + pathName.getPath() + "]!");
                } else {
                    // В цикле проходим по всему списку полученных файлов
                    for (String fileName : fileNames) {
                        // Опять создаем объект "файл"
                        File file = new File(pathName.getPath(), fileName);
                        // Если полученный объект "файл" - является каталогом, рекурсивно вызываем данный метод
                        if (file.isDirectory()) MyIOUtils.delTree(file.getPath());
                            // Если же полученный объект "файл" - файл, то удаляем его
                        else if (file.isFile())
                            if (!file.delete()) log.error("Can't delete file [" + file.getPath() + "]!");
                    }
                    // Удаляем текущий каталог после удаления из него всех файлов
                    log.debug("DELTREE: deleting dir [" + pathName.getPath() + "].");
                    if (!pathName.delete()) log.error("Can't delete dir [" + pathName.getPath() + "]!");
                }
            }
            // Если же "файл" - просто файл - удаляем его
            else if (!pathName.delete()) log.error("Can't delete file [" + pathName.getPath() + "]!");
        } else log.warn("Specifyed path [" + dir + "] doesn't exists!");
    }

    /**
     * Метод очищает указанный каталог dir от содержимого (используется метод delTree() данного класса). Если указан
     * несуществующий каталог - ничего выполнено не будет. Если указан файл - существующий или нет - также ничего
     * выполнено не будет.
     *
     * @param dir String каталог, очищаемый от содержимого.
     */
    public static void clearDir(String dir) {
        log.info("Clearing catalog [" + dir + "].");
        // Если каталог не существует или это файл - ничего не делаем
        if ((new File(dir).exists()) && (new File(dir).isDirectory())) {
            // Удаление полностью дерева каталогов вместе с родительским
            MyIOUtils.delTree(dir);
            // Воссоздание удаленного родительского каталога
            if (!new File(dir).mkdirs()) log.error("Can't re-create catalog [" + dir + "]!");
        } else log.warn("Path [" + dir + "] doesn't exists or not a directory!");
    }

    /**
     * Метод заменяет в переданном ему пути к файлу все разделители на стандартный - DEFAULT_DIR_DELIMITER
     * (см. JLibConsts). Если переданный путь пуст (null или пустая строка) - метод возвращает тоже самое значение
     * (null или пустая строка). Если путь начинается с символа-разделителя - этот символ не пропадет, он будет
     * сконвертирован в правильный (если это необходимо).
     *
     * @param fPath       String путь к файлу, переданный для коррекции разделителей.
     * @param appendSlash boolean добавлять или нет в конец откорректированного пути символ "слэш".
     * @return откорректированный путь к файлу или значение null.
     */
    public static String fixFPath(String fPath, boolean appendSlash) {
        String result = fPath;
        // Если путь не пуст - работаем
        if (!StringUtils.isBlank(fPath)) {
            // В цикле проходим по всему массиву запрещенных символов и заменяем их на стандартный разделитель
            for (char aDeprecated : DEPRECATED_DELIMITERS) {
                result = result.replace(aDeprecated, DEFAULT_DIR_DELIMITER);
            }
            // Есть ли уже на конце данного пути символ-разделитель
            boolean isEndsWithDelimiter = (result.endsWith(String.valueOf(DEFAULT_DIR_DELIMITER)));

            // Теперь разбирем полученный путь на части, разделитель - стандартный. Это необходимо для уничтожения
            // конструкций вида / // /// //// и т.п.
            String[] splittedResult = result.split(String.valueOf(DEFAULT_DIR_DELIMITER));
            StringBuilder resultPath = new StringBuilder();
            // Если исходный путь начинался с символа-разделителя, то этот символ не должен пропасть
            if (result.startsWith(String.valueOf(DEFAULT_DIR_DELIMITER))) {
                resultPath.append(DEFAULT_DIR_DELIMITER);
            }
            // Если путь уже оканчивается на символ-разделитель, то граница добавления разделителей сдвигается на 1
            int delimiterBoundary;
            if (isEndsWithDelimiter) {
                delimiterBoundary = splittedResult.length;
            } else {
                delimiterBoundary = splittedResult.length - 1;
            }
            // В цикле собираем обратно разобранный путь
            for (int i = 0; i < splittedResult.length; i++) {
                // Если текущий каталог не пустой - добавляем его к результирующему пути
                if (!StringUtils.isBlank(splittedResult[i])) {
                    // Добавляем каталог
                    resultPath.append(splittedResult[i]);
                    // Добавляем разделитель
                    if (i < delimiterBoundary) {
                        resultPath.append(DEFAULT_DIR_DELIMITER);
                    }
                }
            }
            result = resultPath.toString();
            // Если указана опция - добавлять слэш - добавляем его (если путь еще не содержит "/" в конце). Также
            // необходимо проверить - не файл ли это? Если это путь к файлу - слэш добавлять низзя!!!
            if (appendSlash && (!result.endsWith(String.valueOf(DEFAULT_DIR_DELIMITER))) &&
                    (!new File(result).isFile())) {
                result += String.valueOf(DEFAULT_DIR_DELIMITER);
            }
        }
        //log.debug("WORKING FSUtils.fixFPath(). RESULT: " + result);
        return result;
    }

    /**
     * Метод сериализует указанный объект object в файл с именем fileName и расширением
     * JLibConsts.SERIALIZED_OBJECT_EXTENSION. Полученный файл будет сохранен в каталог fullPath, если такого
     * каталога нет - он будет создан. Затем файл будет заархивирован (в этом же каталоге) и получит имя
     * fileName и расширение JLibConsts.ZIPPED_OBJECT_EXTENSION. Файл с сериализованным объектом будет
     * удален. Возвратит метод полный путь к полученному архивному файлу (путь + имя файла).<br>
     * <b>ВАЖНО!</b> Сериализуемый объект обязательно должен реализовывать интерфейс Serializable!
     *
     * @param object                Object сериализуемый объект.
     * @param fullPath              String путь к каталогу для сериализации объекта. Если путь пуст - объект будет сериализован в
     *                              текущий каталог.
     * @param fileName              String имя файла для сериализованного объекта и его архива (УКАЗЫВАЕТСЯ БЕЗ РАСШИРЕНИЯ!).
     * @param fileExt               String расширение архивного файла с объектом. Если не указано - используется расширение
     *                              по умолчанию - JLibConsts.ZIPPED_OBJECT_EXTENSION. Расширение указывается БЕЗ точки!
     * @param useFilePathCorrection boolean использовать или нет функцию коррекции файлового пути для указанного
     *                              полного пути к сериализуемому файлу. Если функция используется, то не работают UNC-пути (\\server\folder).
     * @return String полный путь к сериализованному и заархивированному объекту.
     * @throws IOException                          ошибка ввода/вывода при работе с файловой системой.
     */
    public static String serializeObject(@NonNull Object object, String fullPath, String fileName,
                                         String fileExt, boolean useFilePathCorrection) throws IOException {
        log.debug("WORKING FSUtils.serializeObject().");

        // Проверка полученного имени файла для сохранения объекта. Если имя файла не указано, вместо него будет
        // использовано имя класса(объекта).
        String localFileName;
        if (StringUtils.isBlank(fileName)) {
            localFileName = object.getClass().getSimpleName();
        } else {
            localFileName = fileName;
        }

        // Выбираем каталог для сериализации - если пуст указанный, сериализуем в текущий!
        String localFullPath;
        if (StringUtils.isBlank(fullPath)) {
            localFullPath = MyIOUtils.fixFPath(SystemUtils.getUserDir().getAbsolutePath(), false);
        } else {
            // Если используем коррекцию файлового пути - выполняем ее
            if (useFilePathCorrection) {
                localFullPath = MyIOUtils.fixFPath(fullPath, false);
            }
            // Если коррекция не используется - берем путь из параметров без коррекции
            else {
                localFullPath = fullPath;
            }
        }

        // Если указанный каталог для сериализации не существует - создаем его
        if (!new File(localFullPath).exists()) {
            log.debug("Creating catalog [" + localFullPath + "].");
            boolean result = new File(localFullPath).mkdirs();
            // Если не удалось создать каталог для сериализации - ошибка!
            if (!result) {
                throw new IOException("Can't create catalog [" + localFullPath + "] for object!");
            }
        }
        // Если же каталог существует, но это не каталог (файл например) - ошибка (ИС)!
        else if (!new File(localFullPath).isDirectory()) {
            throw new IOException("Path [" + localFullPath + "] is not directory!");
        }

        // Полный путь к файлу с сериализованным объектом
        StringBuilder fullPathSerialized = new StringBuilder(localFullPath);
        // Если путь не оканчивается на символ по умолчанию (/) - добавим в конец пути этот символ
        if (!fullPathSerialized.toString().endsWith(String.valueOf(DEFAULT_DIR_DELIMITER)))
            fullPathSerialized.append(DEFAULT_DIR_DELIMITER);

        // Расширение для файла с сериализованным объектом не должно совпадать с указанным нам расширением
        // для конечного файла, поэтому если расширение нам указано, то надо проверить - не совпадает ли оно
        // с расширением по умолчанию и выбрать одно из двух расширений, которое не совпадает.
        String serializedExt = ".";
        if ((fileExt != null) && (!fileExt.trim().equals(""))) {
            if (SERIALIZED_OBJECT_EXTENSION_1.equals(fileExt)) {
                serializedExt += SERIALIZED_OBJECT_EXTENSION_2;
            } else {
                serializedExt += SERIALIZED_OBJECT_EXTENSION_1;
            }
        }
        // Если расширение не указано - используем первое расширение по умолчанию.
        else {
            serializedExt += SERIALIZED_OBJECT_EXTENSION_1;
        }
        // Непосредственно добавляем расширение к полному пути к файлу
        fullPathSerialized.append(localFileName).append(serializedExt);

        // Полный путь к файлу с архивом сериализованного объекта.
        StringBuilder fullPathZipped = new StringBuilder(localFullPath);
        if (!fullPathZipped.toString().endsWith("/")) {
            fullPathZipped.append("/");
        }
        fullPathZipped.append(localFileName);

        // Если нам указали расширение - используем его, если же нет - используем расширение по умолчанию.
        if (!StringUtils.isBlank(fileExt)) {
            // Если указанное расширение начинается с точки - добавляем его как есть, если же не с точки -
            // сначала добавим точку перед расширением файла
            if (fileExt.startsWith(".")) {
                fullPathZipped.append(fileExt);
            } else {
                fullPathZipped.append(".").append(fileExt);
            }
        } else {
            fullPathZipped.append(ZIPPED_OBJECT_EXTENSION);
        }

        // Применение такой конструкции гарантирует закрытие потока ObjectOutputStream при возникновении ИС
        // во время записи объекта в файл (сериализации)
        ObjectOutputStream out = null;
        try {
            // Запись объекта в файл (сериализация)
            log.debug("Writing object to disk.");
            out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(fullPathSerialized.toString())));
            out.writeObject(object);
        }
        // ИС - что-то не так с сериализуемым классом
        catch (InvalidClassException e) {
            log.error("Something is wrong with a class " + object.getClass().getName() + " [" + e.getMessage() + "]");
        }
        // ИС - сериализуемый класс не реализует интерфейс Serializable
        catch (NotSerializableException e) {
            log.error("Class " + object.getClass().getName() + " doesn't implement the java.io.Serializable " +
                    "interface! [" + e.getMessage() + "]");
        } catch (IOException e) {
            log.error("I/O error! [" + e.getMessage() + "]");
        } finally {
            log.debug("Trying to close ObjectOutputStream...");
            if (out != null) out.close();
        }

        // Применение такой конструкции гарантирует закрытие потоков при возникновении ИС во время работы с ними
        FileOutputStream fout = null;
        ZipOutputStream zout = null;
        FileInputStream fin = null;
        try {
            // Архивация объектного файла
            fout = new FileOutputStream(fullPathZipped.toString());
            zout = new ZipOutputStream(new BufferedOutputStream(fout));
            // Уровень компрессии файлов в архиве
            zout.setLevel(Deflater.BEST_COMPRESSION);
            // Запись в архив инфы об архивируемом файле
            ZipEntry ze = new ZipEntry(localFileName + serializedExt);
            zout.putNextEntry(ze);
            // Непосредственно запись архивируемого файла в архив
            fin = new FileInputStream(fullPathSerialized.toString());
            byte ipBuf[] = new byte[FILE_BUFFER];
            int lenRead;
            while ((lenRead = fin.read(ipBuf)) > 0) {
                zout.write(ipBuf, 0, lenRead);
            }
            zout.closeEntry();
        } finally {
            log.debug("Trying to close zip and file streams...");
            if (fin != null) fin.close();
            if (zout != null) zout.close();
            if (fout != null) fout.close();
        }

        // Удаление исходного файла с данными
        if (!new File(fullPathSerialized.toString()).delete())
            log.warn("Can't delete source file [" + fullPathSerialized + "]!");

        return fullPathZipped.toString();
    }

    /**
     * Метод распаковывает и десериализует объект, который был сериализован и запакован методом serializeObject
     * данного класса. Формат архива - ZIP - [один архив - один файл с объектом], если файлов в архиве больше одного, то
     * распакован будет только первый. Параметр fullFilePath содержит полный путь к файлу архива.
     *
     * @param filePath              String полный путь к файлу архива.
     * @param deleteSource          boolean удалять или нет исходный файл после удачной десериализации объекта.
     * @param useFilePathCorrection boolean использовать или нет функцию коррекции файлового пути для указанного
     *                              полного пути к сериализуемому файлу. Если функция используется, то не работают UNC-пути (\\server\folder).
     * @return Object распакованный и десериализованный объект.
     * @throws IOException            ошибка ввода/вывода при работе с файловой системой.
     * @throws ClassNotFoundException ошибка при десериализации объекта из файла.
     */
    public static Object deserializeObject(String filePath, boolean deleteSource, boolean useFilePathCorrection)
            throws IOException, ClassNotFoundException {
        log.debug("WORKING FSUtils.deserializeObject().");

        // Десериализованный и распакованный объект
        Object object = null;

        // Проверка переданного пути к файлу и проверка существования файла
        if (StringUtils.isBlank(filePath)) {
            throw new IOException("Received path is empty!");
        }
        // Проверка существования
        else if (!new File(filePath).exists()) {
            throw new IOException("File [" + filePath + "] doesn't exists!");
        }
        // Проверка того, что путь указывает именно на файл
        else if (!new File(filePath).isFile()) {
            throw new IOException("Path [" + filePath + "] not a file!");
        }

        // Локальная переменная с полным путем к файлу (при этом исправили все символы-разделители в указанном пути).
        // Корректировка пути производится только в зависимости от значения параметра useFilePathCorrection.
        String localFilePath;
        if (useFilePathCorrection) {
            localFilePath = MyIOUtils.fixFPath(filePath, true);
        } else {
            localFilePath = filePath;
        }

        // Получаем путь к рабочему каталогу, в который будем распаковывать объект из файла. Рабочий каталог - текущий.
        String tempFilePath = MyIOUtils.fixFPath(SystemUtils.getUserDir().getAbsolutePath(), true);

        // Применение такой конструкции гарантирует закрытие потоков при возникновении ИС во время работы с ними
        ZipInputStream zin = null;
        String unpackedFileName = null; // <- имя распакованного файла
        try {
            // Распаковка первого файла из архива (читаем указанный файл архива)
            zin = new ZipInputStream(new BufferedInputStream(new FileInputStream(localFilePath)));
            ZipEntry entry;
            int counter = 0;
            while (((entry = zin.getNextEntry()) != null) && (counter < 1)) {
                log.debug("Extracting from archive -> " + entry.getName());
                unpackedFileName = entry.getName();
                int count;
                byte data[] = new byte[FILE_BUFFER];
                // Применение такой конструкции гарантирует закрытие потоков при возникновении ИС во время работы с ними
                BufferedOutputStream dest = null;
                try {
                    // Пишем на диск распакованный файл. Пишем в тот же каталог, где находится исходный архив
                    if (!StringUtils.isBlank(unpackedFileName)) // <- защита от NullPointerException
                    {
                        dest = new BufferedOutputStream(new FileOutputStream(tempFilePath + unpackedFileName), FILE_BUFFER);
                        while ((count = zin.read(data, 0, FILE_BUFFER)) != -1) {
                            dest.write(data, 0, count);
                        }
                        dest.flush();
                    }
                    // Если имя распакованного файла осталось пусто - непредвиденная ошибка (фатальная)!
                    else {
                        throw new IOException("Unpacked file name is blank!");
                    }
                }
                // Пытаемся освободить ресурсы
                finally {
                    if (dest != null) dest.close();
                }
                zin.closeEntry();
                counter++;
            }
        }
        // Пытаемся освободить ресурсы
        finally {
            if (zin != null) zin.close();
        }

        // Применение такой конструкции гарантирует закрытие потоков при возникновении ИС во время работы с ними
        ObjectInputStream in = null;
        try {
            // Десериализация из файла распакованного объекта
            if (!StringUtils.isBlank(unpackedFileName)) // <- защита от NullPointerException
            {
                in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(tempFilePath + unpackedFileName)));
                object = in.readObject();
            }
            // Если имя распакованного файла осталось пусто - непредвиденная ошибка (фатальная)!
            else {
                throw new IOException("Unpacked file name is blank!");
            }
        }
        // Пытаемся освободить ресурсы. И в любом случае удаляем распакованный временный файл.
        finally {
            if (in != null) in.close();
            // Удаление распакованного файла (временного), если его имя не пусто.
            if (!StringUtils.isBlank(unpackedFileName)) // <- защита от NullPointerException
            {
                if (!new File(tempFilePath + unpackedFileName).delete()) {
                    log.warn("Can't delete file [" + (tempFilePath + unpackedFileName) + "]!");
                } else {
                    log.debug("Deleted unpacked file [" + (tempFilePath + unpackedFileName) + "].");
                }
            }
        }

        // Если указано удаление исходного архивного файла с сериализованным объектом - удаляем
        if (deleteSource) {
            log.debug("Trying to delete source file [" + filePath + "].");
            if (!new File(filePath).delete()) {
                log.warn("Can't delete source file [" + filePath + "]!");
            } else {
                log.debug("Source file [" + filePath + "] deleted successfully.");
            }
        } else {
            log.debug("No deleting source file [" + filePath + "].");
        }
        // Возвращаем результат
        return object;
    }

    /**
     * Метод возвращает значение ИСТИНА/ЛОЖЬ в зависимости от того, содержит ли указанный каталог файлы (если это вообще
     * каталог). Находящиеся в данном каталоге подкаталоги не учитываются, метод проверяет наличие ТОЛЬКО файлов. Если
     * указанное значение вообще пусто - метод возвращает значение ЛОЖЬ. Если путь указывает на файл (а не на каталог),
     * то метод возвращает значение ЛОЖЬ.Строго говоря, метод проверяет наличие файлов с информацией в данном каталоге,
     * а не файлов, содержащих другие файлы (это и есть каталоги).
     *
     * @param path String абсолютный путь к каталогу, наличие файлов в котором проверяем.
     * @return boolean ИСТИНА/ЛОЖЬ - в зависимости от наличия файлов в указанном каталоге.
     */
    public static boolean containFiles(String path) {
        log.debug("FSUtils.containFiles(). Checking path [" + path + "].");
        boolean result = false;
        // Если указанный путь не пуст - работаем
        if (!StringUtils.isBlank(path)) {
            // Проверяем существование каталога и то, что это именно каталог
            File dir = new File(MyIOUtils.fixFPath(path, false));
            if (dir.exists() && dir.isDirectory()) {
                // Получаем список содержимого каталога и смотрим - есть ли там файлы
                File[] fileList = dir.listFiles();
                int counter = 0;
                while ((counter < fileList.length) && !result) {
                    if (fileList[counter].isFile()) {
                        result = true;
                    }
                    counter++;
                }
            }
            // Ошибку - в лог!
            else {
                log.error("Path [" + path + "] doesn't exists or not a directory!");
            }
        }
        // Если же путь пуст - сообщим об этом в лог
        else {
            log.error("Empty path!");
        }
        return result;
    }

    /**
     * Метод возвращает значение ИСТИНА/ЛОЖЬ в зависимости от того, пуст ли указанный каталог (содержит ли указанный каталог
     * файлы или другие каталоги (если указан именно каталог). ИСТИНА - каталог пуст, ЛОЖЬ - в каталоге есть подкаталоги/файлы.
     * Если указанное значение пусто - метод возвращает значение ИСТИНА. Если путь указывает на файл (а не на каталог), то метод
     * возвращает значение ИСТИНА.
     */
    public static boolean isEmptyDir(String path) {
        log.debug("FSUtils.isEmptyDir(). Checking path [" + path + "].");
        boolean result = true;
        // Если указанный путь не пуст - работаем
        if (!StringUtils.isBlank(path)) {
            // Проверяем существование каталога и то, что это именно каталог
            File dir = new File(MyIOUtils.fixFPath(path, false));
            if (dir.exists() && dir.isDirectory()) {
                // Получаем список содержимого каталога и смотрим - есть ли там файлы/подкаталоги. Если мы получили
                // список = NULL, это означает ошибку и метод вернет ЛОЖЬ.
                String[] filesList = dir.list();
                if ((filesList != null) && (filesList.length > 0)) {
                    result = false;
                }
            }
            // Ошибку - в лог!
            else {
                log.error("Path [" + path + "] doesn't exists or not a directory!");
            }
        }
        // Если же путь пуст - сообщим об этом в лог
        else {
            log.error("Empty path!");
        }
        return result;
    }

    /**
     * Метод проверяет каталог (что это именно каталог) catalogPath и если надо (параметр clearPath) очищает его. Если
     * каталога не существует - он будет создан. Если создать каталог (если он не существовал) не удалось - возникает ИС.
     * Если путь указывает не на каталог - возникает ИС. Данный метод часто используется в веб-приложениях при инициализации
     * для "проверки"/очистки/создания необходимых каталогов.
     */
    public static void processPath(String catalogPath, boolean clearPath) throws IOException {
        // Если путь не пуст - обрабатываем его
        if (!StringUtils.isBlank(catalogPath)) {
            File catalog = new File(catalogPath);
            // Если путь существует - проверяем его и очищаем (опционально)
            if (catalog.exists()) {
                // Если это не каталог - ИС
                if (!catalog.isDirectory()) {
                    throw new IOException("Path [" + catalogPath + "] is not a directory!");
                }
                // Если каталог - очищаем (в зависимости от параметра)
                else if (clearPath) {
                    MyIOUtils.clearDir(catalogPath);
                }
            }
            // Если пути не существует - создаем. При неудаче создания - ИС
            else {
                if (!catalog.mkdirs()) {
                    throw new IOException("Can't create catalog [" + catalogPath + "]!");
                }
            }
        }
        // Если путь пуст - ИС
        else {
            throw new IOException("Path is empty!");
        }
    }

    /**
     * Метод обрабатывает указанный файл (fileName) как текстовый и удаляет из него пустые строки (файл читается построчно
     * и перезаписывается без пустых строк и строк, состоящих из одних пробелов/табуляций). При ошибках в работе метода
     * возбуждается ИС IOException (пустое имя, нет файла, пустой файл, путь указывает не на файл и т.п.).
     *
     * @param fileName String обрабатываемый файл
     * @throws IOException ошибки обработки файла.
     */
    public static void removeEmptyLines(String fileName) throws IOException {
        // Имя файла должно быть непустым
        if (!StringUtils.isBlank(fileName)) {
            List list = FileUtils.readLines(new File(fileName));
            if ((list != null) && (!list.isEmpty())) {
                ArrayList<String> newList = new ArrayList<String>();
                for (Object listElement : list) {
                    if ((listElement != null) && !StringUtils.isBlank((String) listElement)) {
                        newList.add((String) listElement);
                    }
                }
                FileUtils.writeLines(new File(fileName), newList);
            }
        }
        // Имя файла пусто
        else {
            throw new IOException("Empty file name!");
        }
    }

    /** Метод возвращает контрольную сумму CRC32 для файла fileName. */
    // todo: duplicate for getCRC()!!!
    public static long getChecksum(@NonNull String fileName) throws IOException {
        log.debug("MyIOUtils.getChecksum() is working.");

        if (StringUtils.isBlank(fileName)) {
            throw new IllegalArgumentException("Provided empty file name for checksum calculating!");
        }

        try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(fileName))) {

            long result;
            int iByte;

            CRC32 crc = new CRC32();
            while ((iByte = in.read()) != -1) { // Непосредственно цикл вычисления контрольной суммы файла
                crc.update(iByte);
            }

            result = crc.getValue();
            log.info(String.format("CRC for file [%s]: %s", fileName, result));
            return result;
        }

    }

    /**
     * Метод возвращает контрольную сумму CRC32 для файла fileName. Если файла не существует или подсчет не
     * удался (возникла ИС), метод возвращает значение 0.
     *
     * @param file File file for CRCr calculating
     * @return long value of CRC32.
     */
    // todo: duplicate for getChecksum()!!!
    public static long getCRC(File file) throws IOException {
        if (file == null) { // check file object
            throw new IOException("Empty file name!");
        }
        CRC32 crc;
        try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(file))) {
            crc = new CRC32();
            int iByte;
            while ((iByte = in.read()) != -1) { // cycle for calculating
                crc.update(iByte);
            }
            //log.debug("CalcCRC: file [" + file + "]; result [" + result + "]");
        }
        return crc.getValue();
    }

    /**
     * Метод находит и возвращает уникальное для каталога catalogPath имя файла с расширением fileExtension. Расширение
     * указывается БЕЗ точки. Параметр usePathCorrection указывает, использовать ли коррекцию (метод fixFPath) пути
     * (для catalogPath) или нет. Если указан режим без коррекции пути, то необходимо следить, чтобы указанный путь
     * оканчивался на символ-разделитель пути. Если в указанном каталоге много файлов - работа метода может занять
     * некоторое время. Если указан пустой путь к каталогу или каталога не существует - метод вернет значение null.
     * Если не указано расширение, то будет найдено уникальное имя для файла без расширения.
     */
    public static String findUniqueFileName(String catalogPath, String fileExtension, boolean usePathCorrection) {
        log.debug("MyIOUtils.findUniqueFileName() is working.");

        String result = null;
        // Если указанный каталог для поиска имени существует - работаемс
        if ((!StringUtils.isBlank(catalogPath)) && (new File(catalogPath).exists())) {
            log.debug("Path [" + catalogPath + "] exists! Processing.");
            // Если используется коррекция имени файла - выполняем ее
            String localPath;
            if (usePathCorrection) {
                localPath = MyIOUtils.fixFPath(catalogPath, true);
            } else {
                localPath = catalogPath;
            }
            // Используемое расширение файла
            String localExt;
            if (!StringUtils.isBlank(fileExtension)) {
                localExt = "." + fileExtension;
            } else {
                localExt = "";
            }

            // Генерация случайного имени файла
            Random random = new Random();
            int randomFileName;
            File destFile;
            boolean nameFound = false;
            // В цикле генерируем имя файла до тех пор, пока не найдем уникальное (в идеале на это необходим один проход).
            do {
                randomFileName = random.nextInt(Integer.MAX_VALUE); // <- генерация большого случайного числа
                // Файл со случайным именем
                destFile = new File(localPath + randomFileName + localExt);
                // Если такого файла не существует - мы нашли искомое имя.
                if (!destFile.exists()) {
                    nameFound = true;
                }
            }
            while (!nameFound);
            log.debug("Found random file name [" + randomFileName + "].");
            // Сохраняем результат
            result = String.valueOf(randomFileName);
        }
        // Если же указан пустой каталог или каталог просто не существует - сообщим об этом в лог
        else {
            log.warn("Path [" + catalogPath + "] is empty or doesn't exists!");
        }
        // Возвращаем результат
        return result;
    }

}
