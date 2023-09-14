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

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.lopb.plugin.util.TimeUtil;

// TODO: Auto-generated Javadoc
/**
 * The listener interface for receiving run events. The class that is interested
 * in processing a run event implements this interface, and the object created
 * with that class is registered with a component using the component's
 * <code>addRunListener<code> method. When
 * the run event occurs, that object's appropriate
 * method is invoked.
 * 
 * @see RunEvent
 */
public class RunListener implements IJobChangeListener {

	/** The Constant log. */
	private static final Log log = LogFactory.getLog(RunListener.class);

	/** The job id. */
	private final JobId jobId;

	/** The run started at. */
	private final long runStartedAt;

	private final List<IResource> resources;

	/** The lock that protects numConcurrentRuns, and endOfPreviousRunAt */
	private static final Lock concurrentRunLock = new ReentrantLock();

	/** The num concurrent runs, protected by concurrentRunLock. */
	private static long numConcurrentRuns = 0;

	/** The end of previous run at, protected by concurrentRunLock. */
	private static long endOfPreviousRunAt = 0;

	/**
	 * Instantiates a new run listener.
	 * 
	 * @param jobId
	 *            the job id
	 * @param runStartedAt
	 *            the run started at
	 */
	public RunListener(final JobId jobId, final List<IResource> resources, final long runStartedAt) {
		this.jobId = jobId;
		this.runStartedAt = runStartedAt;
		this.resources = resources;
		incrementNumConcurrentRunsAndMaybeInitEndOfPreviousRun(runStartedAt);
	}

	/**
	 * Inits the end of last run after session reset.
	 * 
	 * @param now
	 *            the now
	 */
	public static void initEndOfLastRunAfterSessionReset(final long now) {
		concurrentRunLock.lock();
		try {
			endOfPreviousRunAt = now;
		} finally {
			concurrentRunLock.unlock();
		}
	}

	/**
	 * Increment num concurrent runs and maybe init end of previous run.
	 * 
	 * @param runStartedAt
	 *            the run started at
	 */
	private static void incrementNumConcurrentRunsAndMaybeInitEndOfPreviousRun(final long runStartedAt) {
		concurrentRunLock.lock();
		try {
			if (numConcurrentRuns < 1) {
				endOfPreviousRunAt = runStartedAt;
			}
			numConcurrentRuns++;
		} finally {
			concurrentRunLock.unlock();
		}
	}

	/**
	 * Decrement num concurrent runs and return time elapsed since end of
	 * previous run.
	 * 
	 * @param runEndedAt
	 *            the run ended at
	 * 
	 * @return the long
	 */
	private static long[] decrementNumConcurrentRunsAndReturnTimeElapsedSinceEndOfPreviousRun(final long runEndedAt) {
		concurrentRunLock.lock();
		try {
			numConcurrentRuns--;
			final long timeElapsedSinceEndOfPreviousRun = TimeUtil.getElapsedMilliseconds(runEndedAt,
					endOfPreviousRunAt);
			endOfPreviousRunAt = runEndedAt;
			return new long[] { timeElapsedSinceEndOfPreviousRun, numConcurrentRuns };
		} finally {
			concurrentRunLock.unlock();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.jobs.IJobChangeListener#aboutToRun(org.eclipse.core.runtime.jobs.IJobChangeEvent)
	 */
	public void aboutToRun(IJobChangeEvent event) {
		log.debug("aboutToRun: " + jobId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.jobs.IJobChangeListener#awake(org.eclipse.core.runtime.jobs.IJobChangeEvent)
	 */
	public void awake(IJobChangeEvent event) {
		log.debug("awake: " + jobId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.jobs.IJobChangeListener#done(org.eclipse.core.runtime.jobs.IJobChangeEvent)
	 */
	public void done(IJobChangeEvent event) {
		final Job job = event.getJob();
		log.debug("done: " + jobId);

		final long now = TimeUtil.now();
		final long[] retVals = decrementNumConcurrentRunsAndReturnTimeElapsedSinceEndOfPreviousRun(now);

		final long runTime = TimeUtil.getElapsedMilliseconds(now, this.runStartedAt);
		JobStatsManager.getInstance().processJobStats(jobId, job.isSystem(), job.isBlocking(), runTime,
				job.getResult(), retVals[0], retVals[1], resources);
		job.removeJobChangeListener(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.jobs.IJobChangeListener#running(org.eclipse.core.runtime.jobs.IJobChangeEvent)
	 */
	public void running(IJobChangeEvent event) {
		log.debug("running: " + jobId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.jobs.IJobChangeListener#scheduled(org.eclipse.core.runtime.jobs.IJobChangeEvent)
	 */
	public void scheduled(IJobChangeEvent event) {
		log.debug("scheduled: " + jobId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.jobs.IJobChangeListener#sleeping(org.eclipse.core.runtime.jobs.IJobChangeEvent)
	 */
	public void sleeping(IJobChangeEvent event) {
		log.debug("sleeping: " + jobId);
	}
}
