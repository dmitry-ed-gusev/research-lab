package gusev.dmitry.research.xml.parser;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import org.apache.log4j.Logger;

/**
 * Class-implementation for xml parsing errors handler.
 * @author Gusev Dmitry
 * @version 1.0 (DATE: 29.07.11)
*/

public final class ParseErrorHandler implements ErrorHandler
 {
  private final Logger logger = Logger.getLogger(getClass().getName());

  @Override
  public void warning(SAXParseException e) throws SAXException
   {logger.warn("Xml file parsing warning: " + e.getMessage());}

  @Override
  public void error(SAXParseException e) throws SAXException
   {logger.error("Xml file parsing error: " + e.getMessage());}

  @Override
  public void fatalError(SAXParseException e) throws SAXException
   {logger.fatal("Xml file parsing fatal error: " + e.getMessage());}
 }