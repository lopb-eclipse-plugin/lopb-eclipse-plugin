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

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.XMLMemento;
import org.lopb.plugin.LopbPlugin;
import org.lopb.plugin.aliases.JobIdAlias;
import org.lopb.plugin.aliases.JobIdAliasManager;

// TODO: Auto-generated Javadoc
/**
 * The Class JobStatsPersistenceHelper.
 */
public class JobStatsPersistenceHelper {

	/** The Constant log. */
	private static final Log log = LogFactory
			.getLog(JobStatsPersistenceHelper.class);

	/** The Constant TAG_ROOT. */
	private static final String TAG_ROOT = "lopbConfig";

	/** The Constant TAG_IGNORED_JOBS. */
	private static final String TAG_IGNORED_JOBS = "ignoredJobs";

	/** The Constant TAG_IGNORED_JOB. */
	private static final String TAG_IGNORED_JOB = "ignoredJob";

	/** The Constant ATTRIB_JOBID. */
	private static final String ATTRIB_JOBID = "jobId";

	/** The Constant TAG_JOBID_ALIASES. */
	private static final String TAG_JOBID_ALIASES = "jobIdAliases";

	/** The Constant TAG_JOBID_ALIAS. */
	private static final String TAG_JOBID_ALIAS = "jobIdAlias";

	/** The Constant ATTRIB_JOBID_ALIAS. */
	private static final String ATTRIB_JOBID_ALIAS = "alias";

	/**
	 * Instantiates a new job stats persistence helper.
	 */
	public JobStatsPersistenceHelper() {

	}

	/**
	 * Persist.
	 */
	public void persist() {
		FileWriter writer = null;
		try {
			final XMLMemento memento = XMLMemento.createWriteRoot(TAG_ROOT);
			persistIgnoredJobs(memento);
			persistJobIdAliases(memento);

			writer = new FileWriter(LopbPlugin.getUserConfigFile());
			memento.save(writer);
		} catch (Exception e) {
			log.error("Could not persist ignored jobs", e);
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException ioe) {
					log.error("Could not close file", ioe);
				}
			}
		}
	}

	/**
	 * Persist ignored jobs.
	 * 
	 * @param memento the memento
	 */
	private void persistIgnoredJobs(final XMLMemento memento) {
		try {
			final IMemento ignoredJobs = memento.createChild(TAG_IGNORED_JOBS);
			for (final JobId ignoredJobId : JobStatsManager.getInstance()
					.getIgnoredJobIds()) {
				final IMemento ignoredJob = ignoredJobs
						.createChild(TAG_IGNORED_JOB);
				ignoredJob.putString(ATTRIB_JOBID, ignoredJobId
						.getJobIdAsString());
			}
		} catch (Exception e) {
			log.error("Could not persist ignored jobs", e);
		}
	}

	/**
	 * Persist job id aliases.
	 * 
	 * @param memento the memento
	 */
	private void persistJobIdAliases(final XMLMemento memento) {
		try {
			final IMemento ignoredJobs = memento.createChild(TAG_JOBID_ALIASES);
			for (final Map.Entry<JobId, JobIdAlias> entry : JobIdAliasManager
					.getInstance().getJobIdAliasMap().entrySet()) {
				final IMemento jobIdAlias = ignoredJobs
						.createChild(TAG_JOBID_ALIAS);
				jobIdAlias.putString(ATTRIB_JOBID, entry.getKey()
						.getJobIdAsString());
				jobIdAlias.putString(ATTRIB_JOBID_ALIAS, entry.getValue()
						.getAliasAsString());
			}
		} catch (Exception e) {
			log.error("Could not persist ignored jobs", e);
		}
	}

	/**
	 * Load.
	 */
	public void load() {
		FileReader reader = null;
		try {
			File lopbConfigFile = LopbPlugin.getUserConfigFile();

			if (lopbConfigFile == null || !lopbConfigFile.exists()
					|| !lopbConfigFile.canRead()) {
				lopbConfigFile = LopbPlugin.getDefaultConfigFile();
			}

			if (lopbConfigFile != null && lopbConfigFile.exists()
					&& lopbConfigFile.canRead()) {
				reader = new FileReader(lopbConfigFile);
				final XMLMemento memento = XMLMemento.createReadRoot(reader);
				loadIgnoredJobs(memento);
				loadJobIdAliases(memento);
			} else {
				log.warn("Could not find config file: "
						+ lopbConfigFile.getAbsolutePath());
			}
		} catch (Exception e) {
			log.error("Could not load config file: " + e);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException ioe) {
					log.error("Could not close config file: " + ioe);
				}
			}
		}
	}

	/**
	 * Load ignored jobs.
	 * 
	 * @param memento the memento
	 */
	private void loadIgnoredJobs(final XMLMemento memento) {
		try {
			final IMemento[] ignoredJobs = memento
					.getChildren(TAG_IGNORED_JOBS);
			if (ignoredJobs != null && ignoredJobs.length > 0) {
				for (final IMemento ignoredJobMemento : ignoredJobs[0]
						.getChildren(TAG_IGNORED_JOB)) {
					final String jobIdAsString = ignoredJobMemento
							.getString(ATTRIB_JOBID);
					if (jobIdAsString != null) {
						final JobId jobId = JobId.createJobId(jobIdAsString);
						JobStatsManager.getInstance().ignoreJob(jobId);
					}
				}
			}
		} catch (Exception e) {
			log.error("Could not load ignored jobs", e);
		}
	}

	/**
	 * Load job id aliases.
	 * 
	 * @param memento the memento
	 */
	private void loadJobIdAliases(final XMLMemento memento) {
		try {
			final IMemento[] jobIdAliases = memento
					.getChildren(TAG_JOBID_ALIASES);
			if (jobIdAliases != null && jobIdAliases.length > 0)
				for (final IMemento jobIdAliasMemento : jobIdAliases[0]
						.getChildren(TAG_JOBID_ALIAS)) {
					final String aliasAsString = jobIdAliasMemento
							.getString(ATTRIB_JOBID_ALIAS);
					final String jobIdAsString = jobIdAliasMemento
							.getString(ATTRIB_JOBID);
					if (aliasAsString != null && jobIdAsString != null) {
						final JobId jobId = JobId.createJobId(jobIdAsString);
						final JobIdAlias jobIdAlias = JobIdAlias
								.createJobIdAlias(aliasAsString);
						JobIdAliasManager.getInstance().setJobIdAlias(jobId,
								jobIdAlias);
					}
				}
		} catch (Exception e) {
			log.error("Could not load ignored jobs", e);
		}
	}

}
