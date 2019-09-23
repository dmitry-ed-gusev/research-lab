/*
 * Copyright © 2017 Merck Sharp & Dohme Corp., a subsidiary of Merck & Co., Inc.
 * All rights reserved.
 */

package com.msd.bdp.XmlToDbIntegrity;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.om.xpath.AXIOMXPath;
import org.jaxen.JaxenException;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class XPathXmlReader {

    /**
     * Iterates XML nodes and extracts their sub-nodes as Strings.
     *
     * @param xmlInput Input XML file.
     * @param iterateOverPath Name of outer element to stream over.
     * @param elementPath XPath to elements to iterate over.
     * @param fieldsPaths List of XPaths to extract, relative to root paths.
     * @return List of String[] rows we extracted from XML or nulls in case the fields are missing.
     * @throws XMLStreamException We can't recover from this - broken XML?
     * @throws JaxenException We can't recover from this - broken XML?
     */
    public static List<String[]> getRows(FileInputStream xmlInput, String iterateOverPath,
                                         String elementPath, String[] fieldsPaths)
            throws XMLStreamException, JaxenException {

        List<String[]> rows = new ArrayList<>();
        QName iterateOverElement = new QName(iterateOverPath);
        AXIOMXPath xpathExpression = new AXIOMXPath(elementPath);

        // we build a streaming reader to iterate over the top level elements
        XMLStreamReader reader =
                OMXMLBuilderFactory.createOMBuilder(xmlInput).getDocument().getXMLStreamReader(false);

        // if we get one top-level element (from path iterateOverPath), we build it
        while (reader.hasNext()) {
            if (reader.getEventType() == XMLStreamReader.START_ELEMENT &&
                    reader.getName().equals(iterateOverElement)) {

                // we prepare the subtree
                OMElement topLevelElement = OMXMLBuilderFactory.createStAXOMBuilder(reader)
                        .getDocumentElement();
                topLevelElement.build();

                // inside the subtree, we can use XPath as normal
                // get all sub-elements
                List nodeList = xpathExpression.selectNodes(topLevelElement);

                // for each sub-element, use XPath to get the data
                for (Object o : nodeList) { // Object :(
                    OMElement e = (OMElement) o;

                    String[] row = new String[fieldsPaths.length];
                    for (int j = 0; j < fieldsPaths.length; j++) {
                        row[j] = getXPathField(e, fieldsPaths[j]);
                    }
                    rows.add(row);
                }

            } else { // jump to next top-level element
                reader.next();
            }
        }

        return rows;
    }

    /**
     * Returns one element addressed by XPath as a String, starting from root. Returns null if
     * element doesn't exist.
     * @param root Root where to start.
     * @param path XPath relative to root.
     * @return Element from document.
     */
    private static String getXPathField(OMElement root, String path) throws JaxenException {
        AXIOMXPath xpathExpression = new AXIOMXPath(path);
        OMElement e = (OMElement) xpathExpression.selectSingleNode(root);

        // if we don't find the field, threat the value as null
        if (e == null) {
            return null;
        }

        // otherwise, return the value
        return e.getText();
    }

}