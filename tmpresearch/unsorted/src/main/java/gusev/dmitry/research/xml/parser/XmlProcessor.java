package gusev.dmitry.research.xml.parser;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Xml document processing class.
 * @author Gusev Dmitry
 * @version 1.0 (DATE: 29.07.11)
*/

public final class XmlProcessor
 {
  /**
   * Recursive function for go through xml-file. Function will count all found element values.
   * @param node Node 
   * @param element String
   * @return int
  */
  public static int scanXmlDocumentFromNode(Node node, String element)
   {
    int count = 0;
    // found values counter
    if (node.getNodeName().equals(element)) {count++;}
    if (node.hasChildNodes())
     {
      NodeList list = node.getChildNodes();
      for (int i = 0; i < list.getLength(); i++)
       {count += scanXmlDocumentFromNode(list.item(i), element);}
     }
    return count;
   }

 }