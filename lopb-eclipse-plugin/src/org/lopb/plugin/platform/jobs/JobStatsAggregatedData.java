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
 * The Class JobStatsAggregatedData.
 */
public class JobStatsAggregatedData implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -1L;
	
	/** The total milliseconds all jobs. */
	private final long totalMillisecondsAllJobs;
	
	/** The session duration milliseconds. */
	private final long sessionDurationMilliseconds;

	/**
	 * Instantiates a new job stats aggregated data.
	 * 
	 * @param totalMillisecondsAllJobs the total milliseconds all jobs
	 * @param sessionDurationMilliseconds the session duration milliseconds
	 */
	public JobStatsAggregatedData(final long totalMillisecondsAllJobs, final long sessionDurationMilliseconds) {
		this.totalMillisecondsAllJobs = totalMillisecondsAllJobs;
		this.sessionDurationMilliseconds = sessionDurationMilliseconds;
	}

	/**
	 * Gets the total milliseconds all jobs.
	 * 
	 * @return the totalMillisecondsAllJobs
	 */
	public long getTotalMillisecondsAllJobs() {
		return totalMillisecondsAllJobs;
	}

	/**
	 * Gets the session duration milliseconds.
	 * 
	 * @return the sessionDurationMilliseconds
	 */
	public long getSessionDurationMilliseconds() {
		return sessionDurationMilliseconds;
	}

	/**
	 * Gets the percentage of session.
	 * 
	 * @return the percentage of session
	 */
	public float getPercentageOfSession() {
		return getFractionOfSession() * 100;
	}

	/**
	 * Gets the fraction of session.
	 * 
	 * @return the fraction of session
	 */
	public float getFractionOfSession() {
		return (((float) totalMillisecondsAllJobs) / sessionDurationMilliseconds);
	}
}
