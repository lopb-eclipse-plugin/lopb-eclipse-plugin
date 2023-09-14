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

import java.io.PrintWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;
import org.lopb.plugin.backport.HandlerUtil;
import org.lopb.plugin.ui.preferences.LopbPreferenceChangeManager;
import org.lopb.plugin.util.Manager;
import org.lopb.plugin.util.TimeUtil;

// TODO: Auto-generated Javadoc
/**
 * The Class JobStatsManager.
 */
public class JobStatsManager implements IJobChangeListener, Manager {

	/** The Constant log. */
	private static final Log log = LogFactory.getLog(JobStatsManager.class);

	/** The Constant instance. */
	private static final JobStatsManager instance = new JobStatsManager();

	/** The persistence helper. */
	private final JobStatsPersistenceHelper persistenceHelper = new JobStatsPersistenceHelper();

	/** The lock that protects jobMap, sessionStartAt, and totalRunTime */
	private final Lock jobStatsLock = new ReentrantLock();

	/** The job map, protected by jobStatsLock. */
	private final Map<JobId, JobStats> jobMap = new LinkedHashMap<JobId, JobStats>(256);

	/** The session start at, protected by jobStatsLock. */
	private long sessionStartAt;

	/** The total run time, protected by jobStatsLock. */
	private long totalRunTime = 0;

	/** The ignored jobs. */
	private final Map<JobId, Boolean> ignoredJobs = new ConcurrentHashMap<JobId, Boolean>(16);

	/** The job status listeners. */
	private final Map<String, JobStatsChangeListener> jobStatusChangeListeners = new ConcurrentHashMap<String, JobStatsChangeListener>(
			4);

	/** The ignored jobs change listeners. */
	private final Map<String, IgnoredJobsChangeListener> ignoredJobsChangeListeners = new ConcurrentHashMap<String, IgnoredJobsChangeListener>(
			4);

	/** The system jobs included. */
	private final AtomicBoolean systemJobsIncluded = new AtomicBoolean();

	/**
	 * Instantiates a new job stats manager.
	 */
	private JobStatsManager() {
		log.debug("constructor");
		this.systemJobsIncluded.set(LopbPreferenceChangeManager.getInstance().isSystemJobsIncluded());
		resetSession();
	}

	/**
	 * Inits this manager.
	 */
	public void init() {
		persistenceHelper.load();
		final IJobManager manager = Job.getJobManager();
		manager.addJobChangeListener(getInstance());
	}

	/**
	 * Shutdown.
	 */
	public void shutdown() {
		persistenceHelper.persist();
		final IJobManager manager = Job.getJobManager();
		manager.removeJobChangeListener(getInstance());
	}

	/**
	 * Gets the single instance of JobStatsManager.
	 * 
	 * @return single instance of JobStatsManager
	 */
	public static JobStatsManager getInstance() {
		return instance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.jobs.IJobChangeListener#aboutToRun(org.eclipse.core.runtime.jobs.IJobChangeEvent)
	 */
	public void aboutToRun(IJobChangeEvent event) {
		// final Job job = event.getJob();
		// final JobId jobId = new JobId(job.getClass().getName(),
		// job.getName());
		// log.debug("aboutToRun: " + jobId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.jobs.IJobChangeListener#awake(org.eclipse.core.runtime.jobs.IJobChangeEvent)
	 */
	public void awake(IJobChangeEvent event) {
		// final Job job = event.getJob();
		// final JobId jobId = new JobId(job.getClass().getName(),
		// job.getName());
		// log.debug("awake: " + jobId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.jobs.IJobChangeListener#done(org.eclipse.core.runtime.jobs.IJobChangeEvent)
	 */
	public void done(IJobChangeEvent event) {
		// final Job job = event.getJob();
		// final JobId jobId = new JobId(job.getClass().getName(),
		// job.getName());
		// log.debug("done: " + jobId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.jobs.IJobChangeListener#running(org.eclipse.core.runtime.jobs.IJobChangeEvent)
	 */
	public void running(IJobChangeEvent event) {
		// final Job job = event.getJob();
		// final JobId jobId = new JobId(job.getClass().getName(),
		// job.getName());
		// log.debug("running: " + jobId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.jobs.IJobChangeListener#scheduled(org.eclipse.core.runtime.jobs.IJobChangeEvent)
	 */
	public void scheduled(IJobChangeEvent event) {
		final Job job = event.getJob();
		if (!job.isSystem() || (job.isSystem() && this.isSystemJobsIncluded())) {
			final List<IResource> resources = HandlerUtil.getActiveResources();
			final JobId jobId = JobId.createJobId(job.getClass().getName(), job.getName(), resources);
			if (isIgnored(jobId)) {
				log.debug("ignoring job: " + job.getClass().getName());
			} else {
				final long now = TimeUtil.now();
				if (log.isDebugEnabled()) {
					log.debug("scheduled: " + jobId);
				}

				job.addJobChangeListener(new RunListener(jobId, resources, now));
			}
		} else {
			log.debug("ignoring job: " + job.getClass().getName());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.jobs.IJobChangeListener#sleeping(org.eclipse.core.runtime.jobs.IJobChangeEvent)
	 */
	public void sleeping(IJobChangeEvent event) {
		// final Job job = event.getJob();
		// final JobId jobId = new JobId(job.getClass().getName(),
		// job.getName());
		// log.debug("sleeping: " + jobId);
	}

	/**
	 * Process job stats.
	 * 
	 * @param jobId
	 *            the job id
	 * @param isSystemJob
	 *            the is system job
	 * @param runTime
	 *            the run time
	 * @param result
	 *            the result
	 * @param timeElapsedSinceEndOfPreviousRun
	 *            the time elapsed since end of previous run
	 * @param isBlockingJob
	 *            the is blocking job
	 * @param numConcurrentRuns
	 *            the num concurrent runs
	 */
	public void processJobStats(final JobId jobId, final boolean isSystemJob, final boolean isBlockingJob,
			final long runTime, final IStatus result, final long timeElapsedSinceEndOfPreviousRun,
			final long numConcurrentRuns, final List<IResource> resources) {
		if (log.isDebugEnabled()) {
			log.debug("processJobStats: " + jobId + ", runTime: " + runTime + ", timeElapsedSinceEndOfPreviousRun: "
					+ timeElapsedSinceEndOfPreviousRun + ", numConcurrentRuns: " + numConcurrentRuns + ", resources: "
					+ resourcesToString(resources));
		}
		final JobStatsChangedEvent event = updateJobStats(jobId, isSystemJob, isBlockingJob, runTime, result,
				timeElapsedSinceEndOfPreviousRun);
		if (event != null) {
			fireJobStatsChangedEvent(event);
		}
	}

	/**
	 * Update job stats.
	 * 
	 * @param jobId
	 *            the job id
	 * @param isSystemJob
	 *            the is system job
	 * @param runTime
	 *            the run time
	 * @param result
	 *            the result
	 * @param timeElapsedSinceEndOfPreviousRun
	 *            the time elapsed since end of previous run
	 * @param isBlockingJob
	 *            the is blocking job
	 * 
	 * @return the job stats changed event
	 */
	private JobStatsChangedEvent updateJobStats(final JobId jobId, final boolean isSystemJob,
			final boolean isBlockingJob, final long runTime, final IStatus result,
			final long timeElapsedSinceEndOfPreviousRun) {
		jobStatsLock.lock();
		try {
			totalRunTime += timeElapsedSinceEndOfPreviousRun;

			boolean isNew = false;
			if (!this.jobMap.containsKey(jobId)) {
				this.jobMap.put(jobId, new JobStats(jobId, isSystemJob, isBlockingJob));
				isNew = true;
			}
			final JobStats jobStats = this.jobMap.get(jobId);
			jobStats.processEndOfRun(runTime, result);
			return new JobStatsChangedEvent(jobStats, isNew);
		} finally {
			jobStatsLock.unlock();
		}
	}

	/**
	 * Checks if is ignored.
	 * 
	 * @param jobId
	 *            the job id
	 * 
	 * @return true, if is ignored
	 */
	private boolean isIgnored(final JobId jobId) {
		return this.ignoredJobs.containsKey(jobId);
	}

	/**
	 * Gets the ignored job ids.
	 * 
	 * @return the ignored job ids
	 */
	public Collection<JobId> getIgnoredJobIds() {
		return this.ignoredJobs.keySet();
	}

	/**
	 * Gets the all job ids.
	 * 
	 * @return the all job ids
	 */
	public Collection<JobId> getAllJobIds() {
		jobStatsLock.lock();
		try {
			return Collections.unmodifiableSet(this.jobMap.keySet());
		} finally {
			jobStatsLock.unlock();
		}
	}

	/**
	 * Write stats.
	 * 
	 * @param out
	 *            the out
	 * @param withSystemJobs
	 *            the with system jobs
	 */
	public void writeStats(final PrintWriter out, boolean withSystemJobs) {
		jobStatsLock.lock();
		try {
			out.println("Time spent running jobs (seconds, numRuns, numErrors):");
			for (final Map.Entry<JobId, JobStats> entry : jobMap.entrySet()) {
				final JobStats jobStats = entry.getValue();
				if (!jobStats.isSystemJob() || (jobStats.isSystemJob() && withSystemJobs)) {
					out.println(jobStats.toString());
				}
			}
		} finally {
			jobStatsLock.unlock();
		}
	}

	/**
	 * Gets the all job stats.
	 * 
	 * @return the all job stats
	 */
	public JobStats[] getAllJobStats() {
		jobStatsLock.lock();
		try {
			final Collection<JobStats> jobStats = this.jobMap.values();
			return jobStats.toArray(new JobStats[jobStats.size()]);
		} finally {
			jobStatsLock.unlock();
		}
	}

	/**
	 * Gets the job stats aggregated data.
	 * 
	 * @return the job stats aggregated data
	 */
	public JobStatsAggregatedData getJobStatsAggregatedData() {
		jobStatsLock.lock();
		try {
			return new JobStatsAggregatedData(totalRunTime, TimeUtil.getElapsedMilliseconds(this.sessionStartAt));
		} finally {
			jobStatsLock.unlock();
		}
	}

	/**
	 * Adds the listener.
	 * 
	 * @param listener
	 *            the listener
	 */
	public void addJobStatsChangeListener(final JobStatsChangeListener listener) {
		this.jobStatusChangeListeners.put(listener.getId(), listener);
	}

	public void addIgnoredJobsChangeListener(final IgnoredJobsChangeListener listener) {
		this.ignoredJobsChangeListeners.put(listener.getId(), listener);
	}

	/**
	 * Removes the listener.
	 * 
	 * @param listener
	 *            the listener
	 */
	public void removeJobStatsChangeListener(final JobStatsChangeListener listener) {
		this.jobStatusChangeListeners.remove(listener.getId());
	}

	public void removeIgnoredJobsChangeListener(final IgnoredJobsChangeListener listener) {
		this.ignoredJobsChangeListeners.remove(listener.getId());
	}

	/**
	 * Checks for listener.
	 * 
	 * @param listener
	 *            the listener
	 * 
	 * @return true, if successful
	 */
	public boolean hasJobStatsChangeListener(final JobStatsChangeListener listener) {
		return this.jobStatusChangeListeners.containsKey(listener.getId());
	}

	public boolean hasIgnoredJobsChangeListener(final IgnoredJobsChangeListener listener) {
		return this.ignoredJobsChangeListeners.containsKey(listener.getId());
	}

	/**
	 * Ignore job.
	 * 
	 * @param jobId
	 *            the job id
	 */
	public void ignoreJob(final JobId jobId) {
		log.debug("adding to ignore map: " + jobId);
		this.ignoredJobs.put(jobId, Boolean.TRUE);
		jobStatsLock.lock();
		try {
			this.jobMap.remove(jobId);
		} finally {
			jobStatsLock.unlock();
		}
		this.fireIgnoredJobsChangedEvent(new IgnoredJobsChangedEvent(jobId, true));
	}

	public void unIgnoreJob(final JobId jobId) {
		log.debug("removing from ignore map: " + jobId);
		this.ignoredJobs.remove(jobId);
		this.fireIgnoredJobsChangedEvent(new IgnoredJobsChangedEvent(jobId, false));
	}

	/**
	 * Reset session.
	 */
	public void resetSession() {
		jobStatsLock.lock();
		try {
			this.jobMap.clear();
			this.totalRunTime = 0;
			this.sessionStartAt = TimeUtil.now();
			RunListener.initEndOfLastRunAfterSessionReset(this.sessionStartAt);
		} finally {
			jobStatsLock.unlock();
		}
		for (final JobStatsChangeListener listener : this.jobStatusChangeListeners.values()) {
			listener.jobStatsReset();
		}
	}

	/**
	 * Refresh stats.
	 */
	public void refreshStats() {
		for (final JobStats jobStats : getAllJobStats()) {
			fireJobStatsChangedEvent(new JobStatsChangedEvent(jobStats, false));
		}
	}

	/**
	 * Fire job stats changed event.
	 * 
	 * @param event
	 *            the event
	 */
	private void fireJobStatsChangedEvent(final JobStatsChangedEvent event) {
		// log.debug("fireJobStatsChangedEvent: " +
		// event.getJobStats().getJobId());
		for (final JobStatsChangeListener listener : this.jobStatusChangeListeners.values()) {
			listener.jobStatsChanged(event);
		}
	}

	private void fireIgnoredJobsChangedEvent(final IgnoredJobsChangedEvent event) {
		// log.debug("fireJobStatsChangedEvent: " +
		// event.getJobStats().getJobId());
		for (final IgnoredJobsChangeListener listener : this.ignoredJobsChangeListeners.values()) {
			listener.ignoredJobsChanged(event);
		}
	}

	/**
	 * Checks if is system jobs included.
	 * 
	 * @return true, if is system jobs included
	 */
	public boolean isSystemJobsIncluded() {
		return this.systemJobsIncluded.get();
	}

	/**
	 * Sets the system jobs included.
	 * 
	 * @param value
	 *            the new system jobs included
	 */
	public void setSystemJobsIncluded(final boolean value) {
		this.systemJobsIncluded.set(value);
	}

	private String resourcesToString(List<IResource> resources) {
		if (resources == null) {
			return "{null}";
		} else if (resources.isEmpty()) {
			return "{empty}";
		} else {
			final StringBuilder buffer = new StringBuilder(256);
			buffer.append("{");
			final int sz = resources.size();
			int i = 0;
			for (final IResource resource : resources) {
				switch (resource.getType()) {
				case IResource.FILE:
					buffer.append("FILE: " + resource.getName());
					break;
				case IResource.FOLDER:
					buffer.append("FOLDER: " + resource.getName());
					break;
				case IResource.PROJECT:
					buffer.append("PROJECT: " + resource.getName());
					break;
				case IResource.ROOT:
					buffer.append("ROOT: " + resource.getName());
					break;
				default:
					buffer.append(resource.getType() + resource.getName());
					break;
				}
				i++;
				if (i < sz) {
					buffer.append(", ");
				}

			}
			buffer.append("}");
			return buffer.toString();
		}
	}

	/**
	 * @return the sessionStartAt
	 */
	public long getSessionStartAt() {
		jobStatsLock.lock();
		try {
			return sessionStartAt;
		} finally {
			jobStatsLock.unlock();
		}
	}
}
