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
import java.text.DecimalFormat;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.IStatus;
import org.lopb.plugin.util.FormatUtil;

// TODO: Auto-generated Javadoc
/**
 * The Class JobStats.
 */
public class JobStats implements Serializable {

	/** The Constant log. */
	private static final Log log = LogFactory.getLog(JobStats.class);

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -1L;

	/** The job id. */
	private final JobId jobId;

	/** The is system job. */
	private final boolean isSystemJob;

	/** The is blocking job. */
	private final boolean isBlockingJob;

	/** The timers lock, protects totalMilliseconds, avgMilliseconds, numRuns, and numErrors. */
	private final Lock timersLock = new ReentrantLock();

	/** The total milliseconds, protected by timersLock. */
	private long totalMilliseconds = 0;

	/** The avg milliseconds, protected by timersLock. */
	private long avgMilliseconds = 0;

	/** The num runs, protected by timersLock. */
	private int numRuns = 0;

	/** The num errors, protected by timersLock. */
	private int numErrors = 0;

	/** The last run milliseconds, protected by timersLock. */
	private long lastRunMilliseconds = 0;

	/**
	 * Instantiates a new job stats.
	 * 
	 * @param jobId
	 *            the job id
	 * @param isSystemJob
	 *            the is system job
	 * @param isBlockingJob
	 *            the is blocking job
	 */
	public JobStats(final JobId jobId, final boolean isSystemJob, final boolean isBlockingJob) {
		this.jobId = jobId;
		this.isSystemJob = isSystemJob;
		this.isBlockingJob = isBlockingJob;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder buffer = new StringBuilder(80);
		if (this.isSystemJob) {
			buffer.append("[System] ");
		} else {
			buffer.append("         ");
		}

		float lastRunSeconds;
		float totalSeconds;		
		long numRunsCopy;
		long numErrorsCopy;
		timersLock.lock();
		try {
			lastRunSeconds = this.lastRunMilliseconds / 1000f;
			totalSeconds = this.totalMilliseconds / 1000f;
			numRunsCopy = this.numRuns;
			numErrorsCopy = this.numErrors;
		} finally {
			timersLock.unlock();
		}

		final DecimalFormat lastRunMillisecondsFormat = FormatUtil.getDecimalFormat("######0.00");
		buffer.append(lastRunMillisecondsFormat.format(lastRunSeconds));
		buffer.append(" : ");

		final DecimalFormat totalMillisecondsFormat = FormatUtil.getDecimalFormat("######0.00");
		buffer.append(totalMillisecondsFormat.format(totalSeconds));
		buffer.append(" : ");

		final DecimalFormat numRunsFormat = FormatUtil.getDecimalFormat("###0");
		buffer.append(numRunsFormat.format(numRunsCopy));
		buffer.append(" : ");

		final DecimalFormat numErrorsFormat = FormatUtil.getDecimalFormat("###0");
		buffer.append(numErrorsFormat.format(numErrorsCopy));
		buffer.append(" : ");

		buffer.append(this.jobId);
		return buffer.toString();
	}

	/**
	 * Process end of run.
	 * 
	 * @param runTime
	 *            the run time
	 * @param result
	 *            the result
	 */
	public void processEndOfRun(final long runTime, final IStatus result) {
		log.debug("processEndOfRun");
		timersLock.lock();
		try {
			this.lastRunMilliseconds = runTime;
			this.totalMilliseconds += runTime;
			this.numRuns += 1;
			this.avgMilliseconds = this.totalMilliseconds / this.numRuns;
			if (result != null && !result.isOK()) {
				this.numErrors += 1;
			}
		} finally {
			timersLock.unlock();
		}
	}

	/**
	 * Checks if is system job.
	 * 
	 * @return the isSystemJob
	 */
	public boolean isSystemJob() {
		return isSystemJob;
	}

	/**
	 * Checks if is blocking job.
	 * 
	 * @return the isBlockingJob
	 */
	public boolean isBlockingJob() {
		return isBlockingJob;
	}

	/**
	 * Gets the job id.
	 * 
	 * @return the jobId
	 */
	public JobId getJobId() {
		return jobId;
	}

	/**
	 * Gets the num runs.
	 * 
	 * @return the numRuns
	 */
	public int getNumRuns() {
		timersLock.lock();
		try {
			return numRuns;
		} finally {
			timersLock.unlock();
		}
	}

	/**
	 * Gets the total milliseconds.
	 * 
	 * @return the totalMilliseconds
	 */
	public long getTotalMilliseconds() {
		timersLock.lock();
		try {
			return totalMilliseconds;
		} finally {
			timersLock.unlock();
		}
	}

	/**
	 * Gets the avg milliseconds.
	 * 
	 * @return the avgMilliseconds
	 */
	public long getAvgMilliseconds() {
		timersLock.lock();
		try {
			return avgMilliseconds;
		} finally {
			timersLock.unlock();
		}
	}

	/**
	 * Gets the num errors.
	 * 
	 * @return the numErrors
	 */
	public int getNumErrors() {
		timersLock.lock();
		try {
			return numErrors;
		} finally {
			timersLock.unlock();
		}
	}

	/**
	 * @return the lastRunMilliseconds
	 */
	public long getLastRunMilliseconds() {
		timersLock.lock();
		try {
			return lastRunMilliseconds;
		}
		finally {
			timersLock.unlock();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((jobId == null) ? 0 : jobId.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final JobStats other = (JobStats) obj;
		if (jobId == null) {
			if (other.jobId != null) {
				return false;
			}
		} else if (!jobId.equals(other.jobId)) {
			return false;
		}
		return true;
	}

}
