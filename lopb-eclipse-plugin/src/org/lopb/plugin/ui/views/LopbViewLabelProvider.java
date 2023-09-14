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

import java.text.DecimalFormat;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.lopb.plugin.aliases.JobIdAliasManager;
import org.lopb.plugin.labels.LabelsManager;
import org.lopb.plugin.platform.jobs.JobStats;
import org.lopb.plugin.util.FormatUtil;

// TODO: Auto-generated Javadoc
/**
 * The Class LopbViewLabelProvider.
 */
public class LopbViewLabelProvider extends LabelProvider implements ITableLabelProvider {

	private static final String TIME_FORMAT = "###,##0.00";

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang .Object, int)
	 */
	public String getColumnText(Object obj, int index) {
		final JobStats jobStats = (JobStats) obj;
		final DecimalFormat timeFormat = FormatUtil.getDecimalFormat(TIME_FORMAT);

		switch (index) {
		case 0:
			return JobIdAliasManager.getInstance().getJobIdAliasAsString(jobStats.getJobId());
		case 1:
			return LabelsManager.getInstance().getLabelsAsString(jobStats.getJobId());
		case 2:
			return Long.toString(jobStats.getNumRuns());
		case 3:
			return Long.toString(jobStats.getNumErrors());
		case 4:
			return timeFormat.format(jobStats.getLastRunMilliseconds() / 1000f);
		case 5:
			return timeFormat.format(jobStats.getTotalMilliseconds() / 1000f);
		case 6:
			return timeFormat.format(jobStats.getAvgMilliseconds() / 1000f);
		case 7:
			return jobStats.isSystemJob() ? "Yes" : "No";
		case 8:
			return jobStats.isBlockingJob() ? "Yes" : "No";
		default:
			return "undefined for index: " + index;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang .Object, int)
	 */
	public Image getColumnImage(Object obj, int index) {
		return null;
	}
}
