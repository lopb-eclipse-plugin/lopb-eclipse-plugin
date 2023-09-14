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
package org.lopb.plugin.ui.views;

import java.io.Serializable;
import java.util.Comparator;

import org.eclipse.swt.SWT;
import org.lopb.plugin.aliases.JobIdAliasManager;
import org.lopb.plugin.labels.LabelsManager;
import org.lopb.plugin.platform.jobs.JobStats;

// TODO: Auto-generated Javadoc
/**
 * The Class LopbViewColumn.
 */
public class LopbViewColumn implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -1L;

	/** The Constant JOB_ID_COLUMN. */
	public static final int JOB_ID_COLUMN_INDEX = 0;

	/** The Constant LABELS_COLUMN. */
	public static final int LABELS_COLUMN_INDEX = 1;

	/** The Constant LABELS_TEXT. */
	public static final String LABELS_TEXT = "Labels";

	/** The name. */
	private final String name;

	/** The width. */
	private final int width;

	/** The alignment. */
	private final int alignment;

	/** The comparator. */
	private final Comparator<JobStats> comparator;

	/** The Constant jobIdComparator. */
	private static final Comparator<JobStats> jobIdComparator = new Comparator<JobStats>() {
		public int compare(JobStats rowA, JobStats rowB) {
			final String aliasAsStringA = JobIdAliasManager.getInstance().getJobIdAliasAsString(rowA.getJobId());
			final String aliasAsStringB = JobIdAliasManager.getInstance().getJobIdAliasAsString(rowB.getJobId());
			return aliasAsStringA.compareToIgnoreCase(aliasAsStringB);
		}
	};

	/** The Constant runsComparator. */
	private static final Comparator<JobStats> runsComparator = new Comparator<JobStats>() {
		public int compare(JobStats rowA, JobStats rowB) {
			return Integer.valueOf(rowA.getNumRuns()).compareTo(Integer.valueOf(rowB.getNumRuns()));
		}
	};

	/** The Constant errorsComparator. */
	private static final Comparator<JobStats> errorsComparator = new Comparator<JobStats>() {
		public int compare(JobStats rowA, JobStats rowB) {
			return Integer.valueOf(rowA.getNumErrors()).compareTo(Integer.valueOf(rowB.getNumErrors()));
		}
	};

	/** The Constant lastRunComparator. */
	private static final Comparator<JobStats> lastRunTimeComparator = new Comparator<JobStats>() {
		public int compare(JobStats rowA, JobStats rowB) {
			return Long.valueOf(rowA.getLastRunMilliseconds()).compareTo(Long.valueOf(rowB.getLastRunMilliseconds()));
		}
	};

	/** The Constant totalTimeComparator. */
	private static final Comparator<JobStats> totalTimeComparator = new Comparator<JobStats>() {
		public int compare(JobStats rowA, JobStats rowB) {
			return Long.valueOf(rowA.getTotalMilliseconds()).compareTo(Long.valueOf(rowB.getTotalMilliseconds()));
		}
	};

	/** The Constant avgTimeComparator. */
	private static final Comparator<JobStats> avgTimeComparator = new Comparator<JobStats>() {
		public int compare(JobStats rowA, JobStats rowB) {
			return Long.valueOf(rowA.getAvgMilliseconds()).compareTo(Long.valueOf(rowB.getAvgMilliseconds()));
		}
	};

	/** The Constant sysOrUserComparator. */
	private static final Comparator<JobStats> sysOrUserComparator = new Comparator<JobStats>() {
		public int compare(JobStats rowA, JobStats rowB) {
			return Boolean.valueOf(rowA.isSystemJob()).compareTo(Boolean.valueOf(rowB.isSystemJob()));
		}
	};

	/** The Constant sysOrUserComparator. */
	private static final Comparator<JobStats> blockingComparator = new Comparator<JobStats>() {
		public int compare(JobStats rowA, JobStats rowB) {
			return Boolean.valueOf(rowA.isBlockingJob()).compareTo(Boolean.valueOf(rowB.isBlockingJob()));
		}
	};

	/** The Constant labelComparator. */
	private static final Comparator<JobStats> labelComparator = new Comparator<JobStats>() {
		public int compare(JobStats rowA, JobStats rowB) {
			final String labelsAsStringA = LabelsManager.getInstance().getLabelsAsString(rowA.getJobId());
			final String labelsAsStringB = LabelsManager.getInstance().getLabelsAsString(rowB.getJobId());
			return labelsAsStringA.compareToIgnoreCase(labelsAsStringB);
		}
	};

	/** The Constant jobIdColumn. */
	private static final LopbViewColumn jobIdColumn = new LopbViewColumn("Job", 220, SWT.LEFT, jobIdComparator);

	/** The Constant typeColumn. */
	private static final LopbViewColumn labelColumn = new LopbViewColumn("Labels", 120, SWT.CENTER, labelComparator);

	/** The Constant runsColumn. */
	private static final LopbViewColumn runsColumn = new LopbViewColumn("Runs", 60, SWT.CENTER, runsComparator);

	/** The Constant errorsColumn. */
	private static final LopbViewColumn errorsColumn = new LopbViewColumn("Errors", 60, SWT.CENTER, errorsComparator);

	/** The Constant lastRunColumn. */
	private static final LopbViewColumn lastRunTimeColumn = new LopbViewColumn("Last Run (sec)", 100, SWT.CENTER,
			lastRunTimeComparator);

	/** The Constant totalTimeColumn. */
	private static final LopbViewColumn totalTimeColumn = new LopbViewColumn("Total (sec)", 100, SWT.CENTER,
			totalTimeComparator);

	/** The Constant avgTimeColumn. */
	private static final LopbViewColumn avgTimeColumn = new LopbViewColumn("Avg (sec)", 100, SWT.CENTER,
			avgTimeComparator);

	/** The Constant typeColumn. */
	private static final LopbViewColumn sysOrUserColumn = new LopbViewColumn("System?", 60, SWT.CENTER,
			sysOrUserComparator);

	/** The Constant blockingColumn. */
	private static final LopbViewColumn blockingColumn = new LopbViewColumn("Blocking?", 60, SWT.CENTER,
			blockingComparator);

	/** The Constant columns. */
	private static final LopbViewColumn[] columns = new LopbViewColumn[] { jobIdColumn, labelColumn, runsColumn,
			errorsColumn, lastRunTimeColumn, totalTimeColumn, avgTimeColumn, sysOrUserColumn, blockingColumn };

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
	private LopbViewColumn(String name, int width, int alignment, Comparator<JobStats> c) {
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
	public static LopbViewColumn[] getColumns() {
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
	public Comparator<JobStats> getComparator() {
		return comparator;
	}

}
