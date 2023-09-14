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

import org.lopb.plugin.aliases.JobIdAliasManager;
import org.lopb.plugin.labels.LabelsManager;
import org.lopb.plugin.platform.jobs.JobId;
import org.lopb.plugin.platform.jobs.JobStats;
import org.lopb.plugin.platform.jobs.JobStatsManager;

// TODO: Auto-generated Javadoc
/**
 * The Class JobStatsCsvWriter.
 */
public class JobStatsCsvWriter extends CsvWriter {

	/** The job stats manager. */
	private final JobStatsManager jobStatsManager;

	/** The column names. */
	private final String[] columnNames = new String[] { "CLASS_NAME", "DESCRIPTION", "ALIAS", "LABELS", "NUM_RUNS",
			"NUM_ERRORS", "TOTAL_TIME_SECS", "AVG_TIME_SECS", "IS_SYSTEM_JOB", "IS_BLOCKING_JOB" };

	/**
	 * Instantiates a new job stats csv writer.
	 * 
	 * @param baseDir
	 *            the base dir
	 * @param now
	 *            the now
	 * @param jobStatsManager
	 *            the job stats manager
	 */
	public JobStatsCsvWriter(final String baseDir, final Date now, final JobStatsManager jobStatsManager) {
		super(baseDir, now, "jobstats");
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
		final JobStats[] jobStats = jobStatsManager.getAllJobStats();
		final String[][] rowData = new String[jobStats.length][columnNames.length];
		for (int i = 0; i < jobStats.length; i++) {
			final JobStats stats = jobStats[i];
			final JobId id = stats.getJobId();

			rowData[i] = new String[columnNames.length];
			int j = 0;
			rowData[i][j++] = id.getJobClassName();
			rowData[i][j++] = id.getJobName();
			rowData[i][j++] = JobIdAliasManager.getInstance().getJobIdAliasAsString(id);
			rowData[i][j++] = LabelsManager.getInstance().getLabelsAsString(id);
			rowData[i][j++] = Integer.toString(stats.getNumRuns());
			rowData[i][j++] = Integer.toString(stats.getNumErrors());
			rowData[i][j++] = formatDecimal((stats.getTotalMilliseconds()) / 1000f);
			rowData[i][j++] = formatDecimal((stats.getAvgMilliseconds()) / 1000f);
			rowData[i][j++] = Boolean.toString(stats.isSystemJob());
			rowData[i][j++] = Boolean.toString(stats.isBlockingJob());
		}
		return rowData;
	}

}
