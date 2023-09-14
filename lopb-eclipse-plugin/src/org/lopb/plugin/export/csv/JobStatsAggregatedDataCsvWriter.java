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

import java.util.Date;

import org.lopb.plugin.platform.jobs.JobStatsAggregatedData;
import org.lopb.plugin.platform.jobs.JobStatsManager;

// TODO: Auto-generated Javadoc
/**
 * The Class JobStatsAggregatedDataCsvWriter.
 */
public class JobStatsAggregatedDataCsvWriter extends CsvWriter {

	/** The job stats manager. */
	private final JobStatsManager jobStatsManager;
	
	/** The column names. */
	private String[] columnNames = new String[] { "TOTAL_TIME_ALL_JOBS", "TOTAL_TIME_SESSION", "FRACTION_OF_SESSION" };

	/**
	 * Instantiates a new job stats aggregated data csv writer.
	 * 
	 * @param baseDir the base dir
	 * @param now the now
	 * @param jobStatsManager the job stats manager
	 */
	public JobStatsAggregatedDataCsvWriter(final String baseDir, final Date now, final JobStatsManager jobStatsManager) {
		super(baseDir, now, "jobstats_aggregated");
		this.jobStatsManager = jobStatsManager;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.lopb.plugin.util.CsvWriter#getColumnNames()
	 */
	@Override
	protected String[] getColumnNames() {
		return columnNames;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.lopb.plugin.util.CsvWriter#getRows()
	 */
	@Override
	protected String[][] getRowData() {
		final JobStatsAggregatedData jobStatsAggregatedData = jobStatsManager.getJobStatsAggregatedData();
		final String[][] rowData = new String[1][columnNames.length];
		int i = 0, j = 0;
		rowData[i][j++] = formatDecimal(((float) jobStatsAggregatedData.getTotalMillisecondsAllJobs()) / 1000f);
		rowData[i][j++] = formatDecimal(((float) jobStatsAggregatedData.getSessionDurationMilliseconds()) / 1000f);
		rowData[i][j++] = formatDecimal(jobStatsAggregatedData.getFractionOfSession());
		return rowData;
	}

}
