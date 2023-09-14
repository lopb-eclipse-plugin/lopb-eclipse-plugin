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

// TODO: Auto-generated Javadoc
/**
 * The Class JobStatsChangedEvent.
 */
public class JobStatsChangedEvent implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -1L;
	
	/** The job stats. */
	private final JobStats jobStats;
	
	/** The is new. */
	private final boolean isNew;

	/**
	 * Instantiates a new job stats changed event.
	 * 
	 * @param jobStats the job stats
	 * @param isNew the is new
	 */
	public JobStatsChangedEvent(final JobStats jobStats, final boolean isNew) {
		this.jobStats = jobStats;
		this.isNew = isNew;
	}

	/**
	 * Gets the job stats.
	 * 
	 * @return the jobStats
	 */
	public JobStats getJobStats() {
		return jobStats;
	}

	/**
	 * Checks if is new.
	 * 
	 * @return the isNew
	 */
	public boolean isNew() {
		return isNew;
	}

}
