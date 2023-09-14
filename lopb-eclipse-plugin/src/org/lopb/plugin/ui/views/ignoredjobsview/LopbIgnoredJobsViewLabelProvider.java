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

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.lopb.plugin.aliases.JobIdAliasManager;
import org.lopb.plugin.platform.jobs.JobId;

/**
 * The Class LopbIgnoredJobsViewLabelProvider.
 */
public class LopbIgnoredJobsViewLabelProvider extends LabelProvider implements ITableLabelProvider {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang
	 *      .Object, int)
	 */
	public String getColumnText(Object obj, int index) {
		final JobId jobId = (JobId) obj;
		switch (index) {
		case 0:
			return JobIdAliasManager.getInstance().getJobIdAliasAsString(jobId);
		case 1:
			return jobId.getJobClassName();
		case 2:
			return jobId.getJobName();
		default:
			return "undefined for index: " + index;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang
	 *      .Object, int)
	 */
	public Image getColumnImage(Object obj, int index) {
		return null;
	}
}
