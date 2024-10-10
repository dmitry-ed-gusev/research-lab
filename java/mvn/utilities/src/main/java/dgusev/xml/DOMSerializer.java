package dgusev.xml;

/**
 * Класс-сериализатор дерева DOM (xml-документ). Класс реализует сериализацию (запись в постоянное
 * хранилище) переданного ему объекта org.w3c.dom.Document. Возможна запись объекта в символьный
 * поток, в байтовый поток или в xml-файл.
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
  /** Заголовок создаваемого xml-файла (версия xml). */
  private static final String XML_DOCUMENT_HEADER = "<?xml version = '1.0'?>"; //"<xml version=\"1.0\">"

  /** Отступ (разрыв, разделитель) в тексте. */
  private String indent;
  /** Разделитель строк (символ новой строки). */
  private String lineSeparator;
  /** Компонент-логгер данного класса. */
  private Logger logger = Logger.getLogger(getClass().getName());

  /** Конструктор по умолчанию. Инициализирует поля класса и логгер класса. */
  public DOMSerializer()
   {
    logger.debug("WORKING DOMSerializer constructor.");
    indent = " ";
    lineSeparator = "\n";
   }

  /**
   * Метод устанавливает новое значение поля "разделитель строк".
   * @param lineSeparator String новое значение поля "разделитель строк".
  */
  public void setLineSeparator(String lineSeparator) 
   {
    logger.debug("ENTERING DOMSerializer.setLineSeparator(). Setting new separator [" + lineSeparator + "]!");
    this.lineSeparator = lineSeparator;
   }

  /**
   * Метод выполняет сериализацию документа org.w3c.dom.Document в байтовый поток вывода.
   * @param doc Document сериализуемый документ.
   * @param out OutputStream байтовый поток вывода для записи объекта.
   * @throws IOException ИС, которая может возникнуть при записи документа в поток.
  */
  public void serialize(Document doc, OutputStream out) throws IOException
   {
    logger.debug("ENTERING DOMSerializer.serialize(Document, OutputStream).");
    Writer writer = new OutputStreamWriter(out);
    logger.debug("Calling DOMSerializer.serialize(Document, Writer);");
    serialize(doc, writer);
   }

  /**
   * Сериализация DOM-документа в xml-файл File.
   * @param doc Document сериализуемый документ.
   * @param file File файл для записи сериализуемого документа.
   * @throws IOException ИС, которая может возникнуть при записи в файл (File).
  */
  public void serialize(Document doc, File file) throws IOException
   {
    logger.debug("ENTERING DOMSerializer.serialize(Document, File).");
    Writer writer = new FileWriter(file);
    logger.debug("Calling DOMSerializer.serialize(Document, Writer).");
    serialize(doc, writer);
   }

  /**
   * Сериализация DOM-документа в символьный поток Writer.
   * @param doc Document сериализуемый документ.
   * @param writer Writer символьный поток для сериализации документа.
   * @throws IOException ИС, которая может возникнуть при записи в поток вывода (Writer).
  */
  public void serialize(Document doc, Writer writer) throws IOException
   {
    logger.debug("ENTERING DOMSerializer.serialize(Document, Writer).");
    logger.debug("Calling DOMSerializer.serializeNode().");
    // Рекурсивная сериализация документа (без отступов)
    serializeNode(doc, writer, "");
    // Выталкивание из потока остатков данных на диск
    writer.flush();
   }

  /**
   * Метод сериализует один узел дерева xml. Метод является рекурсивным (поэтому не рекомендуется
   * добавлять в данный метод много отладочного вывода).
   * @param node Node сериализуемый узел DOM-дерева.
   * @param writer Writer символьный поток вывода для записи сериализуемого узла.
   * @param indentLevel String уровень отступа для записи узла.
   * @throws IOException ИС, которая может возникнуть при записи в поток вывода (Writer).
  */
  public void serializeNode(Node node, Writer writer, String indentLevel) throws IOException
   {
    //logger.debug("WORKING DOMSerializer.serializeNode().[NODE: " + node.getNodeName() + "]");
    // Выбор действия в зависимости от типа узла дерева
    switch (node.getNodeType())
     {
      case Node.DOCUMENT_NODE:
       writer.write(XML_DOCUMENT_HEADER);
       writer.write(lineSeparator);
       // рекурсивный вызов метода для каждого дочернего элемента
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
       // рекурсивный вызов метода для каждого дочернего элемента
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

      // Узел "тип документа" - пока игнорируем его
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
     } // окончание оператора SWITCH
   }

  /**
   * Метод предназначен только для тестирования данного класса.
   * @param args String[] параметры данного метода.
  */
  public static void main(String[] args) {}

 }