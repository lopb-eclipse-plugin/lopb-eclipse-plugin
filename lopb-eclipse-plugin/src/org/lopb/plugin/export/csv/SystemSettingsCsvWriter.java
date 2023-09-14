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

import org.lopb.plugin.platform.jobs.SystemSettingsManager;

// TODO: Auto-generated Javadoc
/**
 * The Class SystemSettingsCsvWriter.
 */
public class SystemSettingsCsvWriter extends CsvWriter {

	/** The Constant BYTES_IN_ONE_MB. */
	private static final long BYTES_IN_ONE_MB = 1024 * 1024;
	
	/** The system stats manager. */
	private final SystemSettingsManager systemStatsManager;
	
	/** The column names. */
	private String[] columnNames = new String[] { "MAX_MEMORY", "FREE_MEMORY", "HEAP_SIZE", "NUM_PROCESSORS" };

	/**
	 * Instantiates a new system settings csv writer.
	 * 
	 * @param baseDir the base dir
	 * @param now the now
	 * @param systemStatsManager the system stats manager
	 */
	public SystemSettingsCsvWriter(final String baseDir, final Date now, final SystemSettingsManager systemStatsManager) {
		super(baseDir, now, "system");
		this.systemStatsManager = systemStatsManager;
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
		final String[][] rowData = new String[1][columnNames.length];
		int i = 0, j = 0;
		rowData[i] = new String[columnNames.length];
		rowData[i][j++] = formatMB(systemStatsManager.getMaxHeapSize() / BYTES_IN_ONE_MB);
		rowData[i][j++] = formatMB(systemStatsManager.getFreeMemory() / BYTES_IN_ONE_MB);
		rowData[i][j++] = formatMB(systemStatsManager.getHeapSize() / BYTES_IN_ONE_MB);
		rowData[i][j++] = Integer.toString(systemStatsManager.getAvailableProcessors());
		return rowData;
	}
}
