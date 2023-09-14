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
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

// TODO: Auto-generated Javadoc
/**
 * The Class XmlWriter.
 */
public class XmlWriter {

	/** The Constant log. */
	private static final Log log = LogFactory.getLog(XmlWriter.class);
	
	/** The out. */
	private final PrintWriter out;
	
	/** The file. */
	private final File file;

	/**
	 * Instantiates a new xml writer.
	 * 
	 * @param file the file
	 */
	public XmlWriter(final File file) {
		this.file = file;

		PrintWriter printWriter = null;
		try {
			printWriter = new PrintWriter(file, "UTF-8");
			printWriter.println("<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"yes\"?>");
		} catch (FileNotFoundException fnfe) {
			log.error(fnfe);
		} catch (UnsupportedEncodingException uee) {
			log.error(uee);
		} finally {
			this.out = printWriter;
		}
	}

	/**
	 * Write map.
	 * 
	 * @param mapName the map name
	 * @param map the map
	 */
	public void writeMap(final String mapName, final Map<String, String> map) {
		beginElement(mapName);
		this.out.println();
		for (final Map.Entry<String, String> entry : map.entrySet()) {
			writeEntry(entry.getKey(), entry.getValue());
			this.out.println();
		}
		endElement(mapName);
	}

	/**
	 * Begin element.
	 * 
	 * @param element the element
	 */
	public void beginElement(final String element) {
		this.out.append("<").append(element).append(">\n");
	}

	/**
	 * End element.
	 * 
	 * @param element the element
	 */
	public void endElement(final String element) {
		this.out.append("</").append(element).append(">\n");
	}

	/**
	 * Write entry.
	 * 
	 * @param key the key
	 * @param value the value
	 */
	public void writeEntry(final String key, final String value) {
		this.out.append("    <entry ");
		this.out.append("key=\"").append(escapeChars(key)).append("\" ");
		this.out.append("value=\"").append(escapeChars(value)).append("\" />\n");
	}

	/**
	 * Gets the output file.
	 * 
	 * @return the output file
	 */
	public File getOutputFile() {
		return file;
	}

	/**
	 * Close.
	 */
	public void close() {
		this.out.flush();
		this.out.close();
	}

	/**
	 * Escape chars.
	 * 
	 * @param s the s
	 * 
	 * @return the string
	 */
	private String escapeChars(final String s) {
		return StringEscapeUtils.escapeXml(s);
	}

	/**
	 * The main method.
	 * 
	 * @param args the arguments
	 */
	public static void main(String[] args) {

		final String dir = System.getProperty("java.io.tmpdir");
		final String filename = (dir.endsWith(File.separator) ? dir : dir + File.separator) + "xmloutputtest.xml";

		final Map<String, String> map = new LinkedHashMap<String, String>(10);
		map.put("prop1", "hello");
		map.put("prop2", "goodbye");
		map.put("prop3", "salut");

		final XmlWriter xmlWriter = new XmlWriter(new File(filename));
		xmlWriter.writeMap("properties", map);
		xmlWriter.close();

		System.out.println("Wrote: " + xmlWriter.getOutputFile());
	}
}
