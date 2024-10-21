package gusev.dmitry.research.xml.parser;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

/**
 * Main class for XmlParser application.
 * @author Gusev Dmitry
 * @version 1.0 (DATE: 29.07.11)
*/

public final class XmlParser
 {
  private static final String CONFIG_FILE_APP    = "xmlParser.config.xml";
  private static final String CONFIG_FILE_LOGGER = "xmlParser.log4j.properties";
  private static Logger logger = Logger.getLogger(XmlParser.class.getName());

  /**
   * Main application method.
  */
  public static void main(String[] args)
   {
    // log4j configuration
    PropertyConfigurator.configure(CONFIG_FILE_LOGGER);
    logger.info("Application XmlParser started.");
    try
     {
      // loading app config from file
      XmlParserConfig config = new XmlParserConfig(CONFIG_FILE_APP);
      logger.debug("Application configuration: \n" + config.toString());
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setValidating(true);
      DocumentBuilder        builder = factory.newDocumentBuilder();
      // our parsing errors handler
      builder.setErrorHandler(new ParseErrorHandler());
      File xmlFile = new File(config.getXmlFile());
      // file parsing
      Document doc = builder.parse(xmlFile);
      logger.info("Document [" + config.getXmlFile() + "] parsed.");
      int count = XmlProcessor.scanXmlDocumentFromNode(doc.getDocumentElement(), config.getSearchElement());
      logger.info("Elements <" + config.getSearchElement() + "> found in file [" + config.getXmlFile() + "]: " + count);
      logger.info("Application XmlParser finished.");
     }
    catch (ParserConfigurationException e) {logger.error(e.getMessage());}
    catch (SAXException e)                 {logger.error(e.getMessage());}
    catch (IOException e)                  {logger.error(e.getMessage());}
    catch (ConfigurationException e)       {logger.error(e.getMessage());}
   }
 }
