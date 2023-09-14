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
package org.lopb.plugin.export.csv;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lopb.plugin.platform.jobs.SystemSettingsManager;
import org.lopb.plugin.util.FormatUtil;

/**
 * The Class CsvWriter.
 */
public abstract class CsvWriter {

	/** The Constant log. */
	private static final Log log = LogFactory.getLog(CsvWriter.class);

	/** The out. */
	private final PrintWriter out;

	private final Date now;

	/** The output filename. */
	private final String outputFilename;

	/**
	 * Instantiates a new csv writer.
	 * 
	 * @param baseDir
	 *            the base dir
	 * @param now
	 *            the now
	 * @param filenameSuffix
	 *            the filename suffix
	 */
	public CsvWriter(final String baseDir, final Date now, final String filenameSuffix) {
		PrintWriter printWriter = null;
		String filename = null;
		final String filenamePrefix = getFilenamePrefix();
		try {
			final Calendar cal = Calendar.getInstance();
			cal.setTime(now);
			final String dir = generateCsvDirectory(cal, baseDir);
			mkdirs(dir);
			filename = generateCsvFilename(cal, dir, filenamePrefix, filenameSuffix);
			printWriter = new PrintWriter(filename);
		} catch (final FileNotFoundException fnfe) {
			log.error(fnfe);
		} finally {
			this.now = now;
			this.out = printWriter;
			this.outputFilename = filename;
		}
	}

	/**
	 * Gets the output filename.
	 * 
	 * @return the output filename
	 */
	public String getOutputFilename() {
		return this.outputFilename;
	}

	/**
	 * Mkdirs.
	 * 
	 * @param dir
	 *            the dir
	 */
	private void mkdirs(final String dir) {
		final File d = new File(dir);
		if (!d.exists()) {
			d.mkdirs();
		}
	}

	/**
	 * Gets the filename prefix.
	 * 
	 * @return the filename prefix
	 */
	private String getFilenamePrefix() {
		String hostname = SystemSettingsManager.getInstance().getHostName();
		if (hostname != null) {
			// remove any non-word chars
			hostname = hostname.replaceAll("\\W", "");
			// make sure I actually have something left to work with
			if (hostname.length() > 0) {
				return hostname;
			}
		}
		// last report, return this string
		return "localhost";
	}

	/**
	 * Generate csv directory.
	 * 
	 * @param now
	 *            the now
	 * @param baseDir
	 *            the base dir
	 * 
	 * @return the string
	 */
	private String generateCsvDirectory(final Calendar now, final String baseDir) {
		final StringBuilder buffer = new StringBuilder(40);
		buffer.append(baseDir);
		if (!baseDir.endsWith(File.separator)) {
			buffer.append(File.separator);
		}
		buffer.append(now.get(Calendar.YEAR));
		buffer.append(File.separator);
		buffer.append(formatTwoDigits(now.get(Calendar.MONTH) + 1));
		buffer.append(File.separator);
		buffer.append(formatTwoDigits(now.get(Calendar.DAY_OF_MONTH)));
		return buffer.toString();
	}

	/**
	 * Generate csv filename.
	 * 
	 * @param now
	 *            the now
	 * @param dir
	 *            the dir
	 * @param filenamePrefix
	 *            the filename prefix
	 * @param filenameSuffix
	 *            the filename suffix
	 * 
	 * @return the string
	 */
	private String generateCsvFilename(final Calendar now, final String dir, final String filenamePrefix,
			final String filenameSuffix) {
		final StringBuilder buffer = new StringBuilder(40);
		buffer.append(dir);
		if (!dir.endsWith(File.separator)) {
			buffer.append(File.separator);
		}
		buffer.append(filenamePrefix);
		buffer.append("_");
		buffer.append(DateFormatUtils.format(now, "yyyyMMdd_HHmmss"));
		buffer.append("_");
		buffer.append(filenameSuffix);
		buffer.append(".csv");
		return buffer.toString();
	}

	/**
	 * Write csv.
	 */
	public void writeCsv() {
		final String[] columnNames = getColumnNames();
		for (int i = 0; i < columnNames.length; i++) {
			out.append(columnNames[i]);
			if (i < columnNames.length - 1) {
				out.append(",");
			}
		}
		out.println();
		for (final String[] rowData : getRowData()) {
			for (int i = 0; i < rowData.length; i++) {
				final String value = rowData[i];
				try {
					StringEscapeUtils.escapeCsv(out, value);
				} catch (final IOException ioe) {
					log.error("Unable to encode CSV value: " + value, ioe);
				}
				if (i < rowData.length - 1) {
					out.append(",");
				}
			}
			out.println();
		}
	}

	/**
	 * Close.
	 */
	public void close() {
		this.out.flush();
		this.out.close();
	}

	/**
	 * Gets the column names.
	 * 
	 * @return the column names
	 */
	protected abstract String[] getColumnNames();

	/**
	 * Gets the row data.
	 * 
	 * @return the row data
	 */
	protected abstract String[][] getRowData();

	/**
	 * Format two digits.
	 * 
	 * @param num
	 *            the num
	 * 
	 * @return the string
	 */
	protected final String formatTwoDigits(final long num) {
		final DecimalFormat twoDigitFormat = FormatUtil.getDecimalFormat("00");
		return twoDigitFormat.format(num);
	}

	/**
	 * Format decimal.
	 * 
	 * @param num
	 *            the num
	 * 
	 * @return the string
	 */
	protected final String formatDecimal(final double num) {
		final DecimalFormat decimalFormat = FormatUtil.getDecimalFormat("######0.##");
		return decimalFormat.format(num);
	}

	/**
	 * Format mb.
	 * 
	 * @param mb
	 *            the mb
	 * 
	 * @return the string
	 */
	protected final String formatMB(final long mb) {
		final DecimalFormat mbFormat = FormatUtil.getDecimalFormat("######0");
		return mbFormat.format(mb);
	}

	/**
	 * @return the now
	 */
	public Date getNow() {
		return now;
	}

}
