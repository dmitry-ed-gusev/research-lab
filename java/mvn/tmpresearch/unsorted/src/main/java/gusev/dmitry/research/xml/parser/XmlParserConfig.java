package gusev.dmitry.research.xml.parser;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;

/**
 * Config class for XmlParser. Class is immutable. It uses library Apache Commons configuration.
 * @author Gusev Dmitry
 * @version 1.0 (DATE: 29.07.11)
*/

public final class XmlParserConfig
 {
  private final String xmlFile;
  private final String searchElement;

  public XmlParserConfig(String xmlParserConfig) throws IOException, ConfigurationException
   {
    Logger logger = Logger.getLogger(getClass().getName());
    logger.debug("WORKING XmlParserConfig constructor.");
    // empty file name - error
    if (StringUtils.isBlank(xmlParserConfig)) {throw new IOException("File name is blank!");}
    // if file doens't exist or not a file - error
    else if ((!new File(xmlParserConfig).exists()) || (!new File(xmlParserConfig).isFile()))
     {throw new IOException("Config file [" + xmlParserConfig + "] doesn't exists or not a file!");}
    // xml-config object
    XMLConfiguration config = new XMLConfiguration(xmlParserConfig);
    logger.debug("Reading XML file...");
    // load config from file
    config.load();
    this.xmlFile       = config.getString("gusev.xmlFile");
    this.searchElement = config.getString("gusev.searchElement");
   }

  public String getXmlFile()       {return xmlFile;}
  public String getSearchElement() {return searchElement;}

  @Override
  public String toString()
   {
    return new ToStringBuilder(this).
            append("xmlFile", xmlFile).
            append("searchElement", searchElement).
            toString();
   }

 }