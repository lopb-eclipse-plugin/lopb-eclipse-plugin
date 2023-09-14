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
package org.lopb.plugin.ui.views.ignoredjobsview;

import java.io.Serializable;
import java.util.Comparator;

import org.eclipse.swt.SWT;
import org.lopb.plugin.aliases.JobIdAliasManager;
import org.lopb.plugin.platform.jobs.JobId;

// TODO: Auto-generated Javadoc
/**
 * The Class LopbViewColumn.
 */
public class LopbIgnoredJobsViewColumn implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -1L;

	/** The Constant JOB_ID_COLUMN. */
	public static final int JOB_ID_COLUMN_INDEX = 0;

	/** The name. */
	private final String name;

	/** The width. */
	private final int width;

	/** The alignment. */
	private final int alignment;

	/** The comparator. */
	private final Comparator<JobId> comparator;

	/** The Constant jobIdComparator. */
	private static final Comparator<JobId> jobIdComparator = new Comparator<JobId>() {
		public int compare(JobId rowA, JobId rowB) {
			final String aliasAsStringA = JobIdAliasManager.getInstance().getJobIdAliasAsString(rowA);
			final String aliasAsStringB = JobIdAliasManager.getInstance().getJobIdAliasAsString(rowB);
			return aliasAsStringA.compareToIgnoreCase(aliasAsStringB);
		}
	};

	/** The Constant runsComparator. */
	private static final Comparator<JobId> classComparator = new Comparator<JobId>() {
		public int compare(JobId rowA, JobId rowB) {
			return rowA.getJobClassName().compareTo(rowB.getJobClassName());
		}
	};

	/** The Constant errorsComparator. */
	private static final Comparator<JobId> descComparator = new Comparator<JobId>() {
		public int compare(JobId rowA, JobId rowB) {
			return rowA.getJobName().compareTo(rowB.getJobName());
		}
	};

	/** The Constant jobIdColumn. */
	private static final LopbIgnoredJobsViewColumn jobIdColumn = new LopbIgnoredJobsViewColumn("Job", 320, SWT.LEFT,
			jobIdComparator);

	/** The Constant typeColumn. */
	private static final LopbIgnoredJobsViewColumn classColumn = new LopbIgnoredJobsViewColumn("Class", 160, SWT.LEFT,
			classComparator);

	/** The Constant runsColumn. */
	private static final LopbIgnoredJobsViewColumn descColumn = new LopbIgnoredJobsViewColumn("Description", 120,
			SWT.LEFT, descComparator);

	/** The Constant columns. */
	private static final LopbIgnoredJobsViewColumn[] columns = new LopbIgnoredJobsViewColumn[] { jobIdColumn,
			classColumn, descColumn };

	/**
	 * Instantiates a new lopb view column.
	 * 
	 * @param name
	 *            the name
	 * @param width
	 *            the width
	 * @param alignment
	 *            the alignment
	 * @param c
	 *            the c
	 */
	private LopbIgnoredJobsViewColumn(String name, int width, int alignment, Comparator<JobId> c) {
		this.name = name;
		this.width = width;
		this.alignment = alignment;
		this.comparator = c;
	}

	/**
	 * Gets the columns.
	 * 
	 * @return the columns
	 */
	public static LopbIgnoredJobsViewColumn[] getColumns() {
		return columns;
	}

	/**
	 * Gets the name.
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the width.
	 * 
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Gets the alignment.
	 * 
	 * @return the alignment
	 */
	public int getAlignment() {
		return alignment;
	}

	/**
	 * Gets the comparator.
	 * 
	 * @return the comparator
	 */
	public Comparator<JobId> getComparator() {
		return comparator;
	}

}
