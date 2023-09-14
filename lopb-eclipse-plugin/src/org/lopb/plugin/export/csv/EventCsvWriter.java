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

import org.apache.commons.lang.time.DateFormatUtils;

// TODO: Auto-generated Javadoc
/**
 * The Class EventCsvWriter.
 */
public class EventCsvWriter extends CsvWriter {

	/** The Constant columnNames. */
	private static final String[] columnNames = new String[] { "EVENT", "TIMESTAMP" };
	
	/** The event name. */
	private final String eventName;

	/**
	 * Instantiates a new event csv writer.
	 * 
	 * @param baseDir the base dir
	 * @param now the now
	 * @param eventName the event name
	 */
	public EventCsvWriter(final String baseDir, final Date now, final String eventName) {
		super(baseDir, now, eventName);
		this.eventName = eventName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.lopb.plugin.export.csv.CsvWriter#getColumnNames()
	 */
	@Override
	protected String[] getColumnNames() {
		return columnNames;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.lopb.plugin.export.csv.CsvWriter#getRowData()
	 */
	@Override
	protected String[][] getRowData() {
		return new String[][] { { eventName, DateFormatUtils.format(getNow(), "yyyy-MM-dd HH:mm:ss") } };
	}

}
