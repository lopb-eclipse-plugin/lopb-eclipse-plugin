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
package org.lopb.plugin.platform.jobs;

import java.io.Serializable;


/**
 * The Class JobStatsChangedEvent.
 */
public class IgnoredJobsChangedEvent implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -1L;

	/** The job stats. */
	private final JobId jobId;

	/** The is new. */
	private final boolean isIgnored;

	/**
	 * Instantiates a new job stats changed event.
	 * 
	 * @param jobStats
	 *            the job stats
	 * @param isNew
	 *            the is new
	 */
	public IgnoredJobsChangedEvent(final JobId jobId, final boolean isIgnored) {
		this.jobId = jobId;
		this.isIgnored = isIgnored;
	}

	/**
	 * @return the jobId
	 */
	public JobId getJobId() {
		return jobId;
	}

	/**
	 * @return the isIgnored
	 */
	public boolean isIgnored() {
		return isIgnored;
	}

}
