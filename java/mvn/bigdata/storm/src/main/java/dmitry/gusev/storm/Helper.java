package dmitry.gusev.storm;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;
import java.util.stream.Stream;

/**
 * Helper utility class. Contains some useful methods.
 * @author Gusev Dmitry (vinnypuhh)
 * @version 1.0 (DATE: 03.04.2016)
 */
public final class Helper {

    private static final Log log = LogFactory.getLog(Helper.class);

    private final DocumentBuilder builder;
    private final Transformer transformer;

    /***/
    public Helper() throws ParserConfigurationException, TransformerConfigurationException {
        log.debug("Helper constructor() working.");
        this.builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        this.transformer = TransformerFactory.newInstance().newTransformer();
    }

    /**
     * Utility method. Useful for debug. Converts string to XML document.
     * @param str String source for converting to XML object.
     * @return Document
     * @throws IOException, SAXException
     */
    public Document strToDocument(String str) throws IOException, SAXException {
        log.trace(String.format("Helper.strToDocument(). Input [%s].", str)); // <- too much output
        return builder.parse(new InputSource(new StringReader(str)));
    }

    /**
     * Utility method. Useful for debug. Converts XML document to string representation.
     * @param doc Document source XML document for converting
     * @return String
     * @throws TransformerException
     */
    public String documentToStr(Document doc) throws TransformerException {
        log.trace("Helper.documentToStr() working."); // <- too much output
        DOMSource domSource = new DOMSource(doc);
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        this.transformer.transform(domSource, result);
        return writer.toString();
    }

    /**
     * Returns count of lines in given file fileName. In case of error returns 0.
     * @param fileName String file name for counting lines.
     * @return long lines count (0 in case of error or empty file)
     */
    // todo: maybe throw IOException outside method (for calling code)?
    // todo: return -1 in case of error?
    public static long getLinesInFileCount(String fileName) {
        log.debug("Helper.getLinesInFileCount() working.");
        try (Stream<String> fileLines = Files.lines(Paths.get(fileName))) {
            return fileLines.count();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return 0;
        }
    }

    /**
     * Removes file. If file doesnt exists - ok, returns true. If file exists and was deleted - ok, returns true.
     * If file exists, but can't be deleted - not ok, returns false.
     */
    public static boolean removeFile(String fileName) {
        log.debug(String.format("Helper.removeFile() working. Trying to remove fiel [%s].", fileName));

        if (StringUtils.isBlank(fileName)) { //FAIL FAST
            throw new IllegalArgumentException("Empty file name for removing!");
        }

        boolean result;
        File outputFile = new File(fileName);
        if (outputFile.exists()) {
            result = outputFile.delete();
            log.info(String.format("File [%s] exists. Removing -> [%s].", fileName, result ? "OK" : "Fail"));
        } else {
            result = true;
        }

        return result;
    }

    /**
     * Method creates text files with random alpha-numeric content.
     * @param fileName String created file name.
     * @param linesCount long number of lines for created file, must be > 0.
     * @param maxLineLength int max length for file line. used as upper bound to random generated length. if value is <= 0,
     *                      then value StormAppDefaults.GENERATED_FILE_MAX_LINE_LENGTH will be used.
     * @param deleteIfExists boolean if true - delete file if exists, otherwise - append to the existing file.
     */
    // todo: throw errors by method - to caller code (???)
    public static void generateTextFile(String fileName, long linesCount, int maxLineLength, boolean deleteIfExists) {
        log.debug("Helper.generateTextFile() working.");

        if (StringUtils.isBlank(fileName) || linesCount < 0) { // check input parameters
            throw new IllegalArgumentException(String.format("Empty file name [%s] or negative lines count [%s]!", fileName, linesCount));
        }

        // check for file existence
        File file = new File(fileName);
        if (file.exists() && deleteIfExists) {
            boolean isDeleteOK = file.delete();
            log.info(String.format("File [%s] exists. Removing -> [%s].", fileName, isDeleteOK ? "OK" : "Fail"));
            if (!isDeleteOK) { // if can't delete - we won't process.
                log.error(String.format("Cant't delete file [%s]!", fileName));
                return;
            }
        }

        Random random = new Random();
        final long debugStep = linesCount / 5; // every 20%
        final int lineLength = maxLineLength > 0 ? maxLineLength : StormAppDefaults.GENERATED_FILE_MAX_LINE_LENGTH;
        try (FileWriter fw = new FileWriter(fileName, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {

            for (long i = 1; i <= linesCount; i++) { // generating lines for file
                out.println(RandomStringUtils.randomAlphanumeric(random.nextInt(lineLength)));
                if (i % debugStep == 0) {
                    log.debug(String.format("Writed [%s] lines to file [%s].", i, fileName));
                }
            } // end of for

            if (linesCount % debugStep != 0) {
                log.debug(String.format("Writed [%s] lines to file [%s].", linesCount, fileName));
            }

        } catch (IOException e) {
            log.error(e);
        }

    }

}
