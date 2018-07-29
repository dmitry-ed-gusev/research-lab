package jlib.xml;

/**
 * �����-������������ ������ DOM (xml-��������). ����� ��������� ������������ (������ � ����������
 * ���������) ����������� ��� ������� org.w3c.dom.Document. �������� ������ ������� � ����������
 * �����, � �������� ����� ��� � xml-����.
 * @author Gusev Dmitry (019gus)
 * @version 1.0 (DATE: 30.11.2007)
*/

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.apache.log4j.Logger;

public class DOMSerializer
 {
  /** ��������� ������������ xml-����� (������ xml). */
  private static final String XML_DOCUMENT_HEADER = "<?xml version = '1.0'?>"; //"<xml version=\"1.0\">"

  /** ������ (������, �����������) � ������. */
  private String indent;
  /** ����������� ����� (������ ����� ������). */
  private String lineSeparator;
  /** ���������-������ ������� ������. */
  private Logger logger = Logger.getLogger(getClass().getName());

  /** ����������� �� ���������. �������������� ���� ������ � ������ ������. */
  public DOMSerializer()
   {
    logger.debug("WORKING DOMSerializer constructor.");
    indent = " ";
    lineSeparator = "\n";
   }

  /**
   * ����� ������������� ����� �������� ���� "����������� �����".
   * @param lineSeparator String ����� �������� ���� "����������� �����".
  */
  public void setLineSeparator(String lineSeparator) 
   {
    logger.debug("ENTERING DOMSerializer.setLineSeparator(). Setting new separator [" + lineSeparator + "]!");
    this.lineSeparator = lineSeparator;
   }

  /**
   * ����� ��������� ������������ ��������� org.w3c.dom.Document � �������� ����� ������.
   * @param doc Document ������������� ��������.
   * @param out OutputStream �������� ����� ������ ��� ������ �������.
   * @throws IOException ��, ������� ����� ���������� ��� ������ ��������� � �����.
  */
  public void serialize(Document doc, OutputStream out) throws IOException
   {
    logger.debug("ENTERING DOMSerializer.serialize(Document, OutputStream).");
    Writer writer = new OutputStreamWriter(out);
    logger.debug("Calling DOMSerializer.serialize(Document, Writer);");
    serialize(doc, writer);
   }

  /**
   * ������������ DOM-��������� � xml-���� File.
   * @param doc Document ������������� ��������.
   * @param file File ���� ��� ������ �������������� ���������.
   * @throws IOException ��, ������� ����� ���������� ��� ������ � ���� (File).
  */
  public void serialize(Document doc, File file) throws IOException
   {
    logger.debug("ENTERING DOMSerializer.serialize(Document, File).");
    Writer writer = new FileWriter(file);
    logger.debug("Calling DOMSerializer.serialize(Document, Writer).");
    serialize(doc, writer);
   }

  /**
   * ������������ DOM-��������� � ���������� ����� Writer.
   * @param doc Document ������������� ��������.
   * @param writer Writer ���������� ����� ��� ������������ ���������.
   * @throws IOException ��, ������� ����� ���������� ��� ������ � ����� ������ (Writer).
  */
  public void serialize(Document doc, Writer writer) throws IOException
   {
    logger.debug("ENTERING DOMSerializer.serialize(Document, Writer).");
    logger.debug("Calling DOMSerializer.serializeNode().");
    // ����������� ������������ ��������� (��� ��������)
    serializeNode(doc, writer, "");
    // ������������ �� ������ �������� ������ �� ����
    writer.flush();
   }

  /**
   * ����� ����������� ���� ���� ������ xml. ����� �������� ����������� (������� �� �������������
   * ��������� � ������ ����� ����� ����������� ������).
   * @param node Node ������������� ���� DOM-������.
   * @param writer Writer ���������� ����� ������ ��� ������ �������������� ����.
   * @param indentLevel String ������� ������� ��� ������ ����.
   * @throws IOException ��, ������� ����� ���������� ��� ������ � ����� ������ (Writer).
  */
  public void serializeNode(Node node, Writer writer, String indentLevel) throws IOException
   {
    //logger.debug("WORKING DOMSerializer.serializeNode().[NODE: " + node.getNodeName() + "]");
    // ����� �������� � ����������� �� ���� ���� ������
    switch (node.getNodeType())
     {
      case Node.DOCUMENT_NODE:
       writer.write(XML_DOCUMENT_HEADER);
       writer.write(lineSeparator);
       // ����������� ����� ������ ��� ������� ��������� ��������
       NodeList nodes = node.getChildNodes();
       if (nodes != null) {for (int i = 0; i < nodes.getLength(); i++) {serializeNode(nodes.item(i), writer, "");}}
       /*
        *  Document doc = (Document)node;
        *  serializeNode(doc.getDocumentElement( ), writer, " ");
       */
       break;

      case Node.ELEMENT_NODE:
       String name = node.getNodeName();
       writer.write(indentLevel + "<" + name);
       NamedNodeMap attributes = node.getAttributes();
       for (int i = 0; i < attributes.getLength(); i++)
        {Node current = attributes.item(i); writer.write(" " + current.getNodeName() + "=\"" + current.getNodeValue() + "\"");}
       writer.write(">");
       // ����������� ����� ������ ��� ������� ��������� ��������
       NodeList children = node.getChildNodes();
       if (children != null)
        {
         if ((children.item(0) != null) && (children.item(0).getNodeType() == Node.ELEMENT_NODE)) {writer.write(lineSeparator);}
         for (int i = 0; i < children.getLength(); i++) {serializeNode(children.item(i), writer, indentLevel + indent);}
         if ((children.item(0) != null) && (children.item(children.getLength() - 1).getNodeType() == Node.ELEMENT_NODE))
          {writer.write(indentLevel);}
        }
       writer.write("</" + name + ">");
       writer.write(lineSeparator);
       break;

      case Node.TEXT_NODE: writer.write(node.getNodeValue()); break;

      case Node.CDATA_SECTION_NODE: writer.write("<![CDATA[" + node.getNodeValue() + "]]>"); break;

      case Node.COMMENT_NODE:
       writer.write(indentLevel + "<!-- " + node.getNodeValue() + " -->");
       writer.write(lineSeparator);
       break;

      case Node.PROCESSING_INSTRUCTION_NODE:
       writer.write("<?" + node.getNodeName() + " " + node.getNodeValue() + "?>");
       writer.write(lineSeparator);
       break;

      case Node.ENTITY_REFERENCE_NODE: writer.write("&" + node.getNodeName() + ";"); break;

      // ���� "��� ���������" - ���� ���������� ���
      /*
      case Node.DOCUMENT_TYPE_NODE:
       DocumentType docType = (DocumentType) node;
       writer.write("<!DOCTYPE " + docType.getName());
       if (docType.getPublicId() != null) {System.out.print(" PUBLIC \"" + docType.getPublicId() + "\" ");}
       else {writer.write(" SYSTEM ");}
       writer.write("\"" + docType.getSystemId() + "\">");
       writer.write(lineSeparator);
       break;
      */ 
     } // ��������� ��������� SWITCH
   }

  /**
   * ����� ������������ ������ ��� ������������ ������� ������.
   * @param args String[] ��������� ������� ������.
  */
  public static void main(String[] args) {}

 }