/*******************************************************************************
 * Copyright (c) 2009 Alex De Marco, Aaron Silinskas, Abhishek Nath.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Alex De Marco, Aaron Silinskas, Abhishek Nath - initial API and implementation
 *******************************************************************************/
package org.lopb.plugin.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

// TODO: Auto-generated Javadoc
/**
 * The Class XmlReader.
 */
public class XmlReader {

	/** The Constant log. */
	private static final Log log = LogFactory.getLog(XmlReader.class);

	/** The file. */
	private final File file;

	/**
	 * Instantiates a new xml reader.
	 * 
	 * @param file the file
	 */
	public XmlReader(final File file) {
		this.file = file;
	}

	/**
	 * Parses the.
	 * 
	 * @return the document
	 */
	public Document parse() {
		try {
			final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true); // never forget this!
			final DocumentBuilder builder = factory.newDocumentBuilder();
			return builder.parse(file);
		} catch (ParserConfigurationException pce) {
			log.error(pce);
		} catch (IOException ioe) {
			log.error(ioe);
		} catch (SAXException se) {
			log.error(se);
		}
		return null;
	}

	/**
	 * Gets the map.
	 * 
	 * @param document the document
	 * @param xpathExpression the xpath expression
	 * @param keyAttributeName the key attribute name
	 * @param valueAttributeName the value attribute name
	 * 
	 * @return the map
	 */
	public Map<String, String> getMap(final Document document, final String xpathExpression,
			final String keyAttributeName, final String valueAttributeName) {

		final Map<String, String> resultMap = new LinkedHashMap<String, String>(16);
		try {
			final XPathFactory factory = XPathFactory.newInstance();
			final XPath xpath = factory.newXPath();
			final XPathExpression expr = xpath.compile(xpathExpression);

			final Object resultObj = expr.evaluate(document, XPathConstants.NODESET);
			final NodeList nodes = (NodeList) resultObj;
			for (int i = 0; i < nodes.getLength(); i++) {
				final NamedNodeMap namedNodeMap = nodes.item(i).getAttributes();
				final String key = namedNodeMap.getNamedItem(keyAttributeName).getNodeValue();
				final String value = namedNodeMap.getNamedItem(valueAttributeName).getNodeValue();
				resultMap.put(key, value);
			}
		} catch (XPathExpressionException xee) {
			log.error(xee);
		}
		return resultMap;
	}

	/**
	 * Gets the list.
	 * 
	 * @param document the document
	 * @param xpathExpression the xpath expression
	 * 
	 * @return the list
	 */
	public List<String> getList(final Document document, final String xpathExpression) {

		final List<String> resultList = new ArrayList<String>(16);
		try {
			final XPathFactory factory = XPathFactory.newInstance();
			final XPath xpath = factory.newXPath();
			final XPathExpression expr = xpath.compile(xpathExpression);

			final Object resultObj = expr.evaluate(document, XPathConstants.NODESET);
			final NodeList nodes = (NodeList) resultObj;
			for (int i = 0; i < nodes.getLength(); i++) {
				resultList.add(nodes.item(i).getNodeValue());
			}
		} catch (XPathExpressionException xee) {
			log.error(xee);
		}
		return resultList;
	}

	/**
	 * The main method.
	 * 
	 * @param args the arguments
	 */
	public static void main(String[] args) {

		final String dir = System.getProperty("java.io.tmpdir");
		final String filename = (dir.endsWith(File.separator) ? dir : dir + File.separator) + "lopb-config.xml";

		final XmlReader xmlReader = new XmlReader(new File(filename));
		final Document doc = xmlReader.parse();

		for (final Map.Entry<String, String> entry : xmlReader.getMap(doc, "/ignoredJobs/entry", "key", "value")
				.entrySet()) {
			System.out.println("Read: " + entry.getKey() + " --> " + entry.getValue());
		}

		for (final String entry : xmlReader.getList(doc, "/ignoredJobs/entry/@key")) {
			System.out.println("Read: " + entry);
		}
	}

}
