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
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lopb.plugin.platform.jobs.JobStatsManager;
import org.lopb.plugin.platform.jobs.SystemSettingsManager;

// TODO: Auto-generated Javadoc
/**
 * The Class CsvExporter.
 */
public class CsvExporter {

	/** The Constant log. */
	private static final Log log = LogFactory.getLog(CsvExporter.class);

	/** The Constant instance. */
	private static final CsvExporter instance = new CsvExporter();

	/**
	 * Instantiates a new csv exporter.
	 */
	private CsvExporter() {
	}

	/**
	 * Gets the single instance of CsvExporter.
	 * 
	 * @return single instance of CsvExporter
	 */
	public static CsvExporter getInstance() {
		return instance;
	}

	public String exportOnInit(final String baseDir) {
		final Date now = new Date();
		log.debug("exporting: Init");
		final CsvWriter out = new InitEventCsvWriter(baseDir, now);
		out.writeCsv();
		out.close();
		return export(baseDir, now);
	}

	public String exportOnReset(final String baseDir) {
		final Date now = new Date();
		log.debug("exporting: Reset");
		final CsvWriter out = new ResetEventCsvWriter(baseDir, now);
		out.writeCsv();
		out.close();
		return export(baseDir, now);
	}

	public String exportOnShutdown(final String baseDir) {
		final Date now = new Date();
		log.debug("exporting: Shutdown");
		final CsvWriter out = new ShutdownEventCsvWriter(baseDir, now);
		out.writeCsv();
		out.close();
		return export(baseDir, now);
	}

	/**
	 * Export.
	 * 
	 * @return the string
	 */
	public String export(final String baseDir) {
		return export(baseDir, new Date());
	}

	private String export(final String baseDir, final Date now) {
		log.debug("exporting: JobStats");
		final CsvWriter out1 = new JobStatsCsvWriter(baseDir, now, JobStatsManager.getInstance());
		out1.writeCsv();
		out1.close();

		log.debug("exporting: aggregated JobStats");
		final CsvWriter out2 = new JobStatsAggregatedDataCsvWriter(baseDir, now, JobStatsManager.getInstance());
		out2.writeCsv();
		out2.close();

		log.debug("exporting: SystemStats");
		final CsvWriter out3 = new SystemSettingsCsvWriter(baseDir, now, SystemSettingsManager.getInstance());
		out3.writeCsv();
		out3.close();

		return convertToOutputDir(out1.getOutputFilename());
	}

	/**
	 * Convert to output dir.
	 * 
	 * @param s
	 *            the s
	 * 
	 * @return the string
	 */
	private String convertToOutputDir(final String s) {
		return s.substring(0, s.lastIndexOf(File.separator));
	}
}
