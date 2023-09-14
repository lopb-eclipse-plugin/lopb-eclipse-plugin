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
package org.lopb.plugin.export;

import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lopb.plugin.LopbPlugin;
import org.lopb.plugin.export.csv.CsvExporter;
import org.lopb.plugin.platform.jobs.JobStatsManager;
import org.lopb.plugin.ui.preferences.LopbPreferenceChangeManager;
import org.lopb.plugin.util.JobConfig;
import org.lopb.plugin.util.JobFactory;
import org.lopb.plugin.util.Manager;
import org.lopb.plugin.util.TimeUtil;

// TODO: Auto-generated Javadoc
/**
 * The Class ExportManager.
 */
public class ExportManager implements Manager {

	/** The Constant instance. */
	private static final ExportManager instance = new ExportManager();

	/** The Constant log. */
	private static final Log log = LogFactory.getLog(ExportManager.class);

	/**
	 * Instantiates a new export manager.
	 */
	private ExportManager() {

	}

	/**
	 * Gets the instance.
	 * 
	 * @return the instance
	 */
	public static final ExportManager getInstance() {
		return instance;
	}

	public void init() {
		log.debug("Initializing...");
		final JobConfig exportConfig = new JobConfig() {

			private final Runnable runnable = new Runnable() {

				/*
				 * (non-Javadoc)
				 * 
				 * @see java.lang.Runnable#run()
				 */
				public void run() {
					if (isSessionStartedYesterday()) {
						JobStatsManager.getInstance().resetSession();
						ExportManager.getInstance().resetSession();
					} else {
						ExportManager.getInstance().export(LopbPlugin.getExportSnapshotsBaseDir().getAbsolutePath());
					}
				}
			};

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.lopb.plugin.util.JobConfig#getDelay()
			 */
			public long getDelay() {
				return JobFactory.ONE_MINUTE_MS;
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.lopb.plugin.util.JobConfig#getName()
			 */
			public String getName() {
				return "Lopb Periodic Task - Export CSV Snapshot";
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.lopb.plugin.util.JobConfig#getPeriod()
			 */
			public long getPeriod() {
				return LopbPreferenceChangeManager.getInstance().getExportSnapshotPeriodMinutes()
						* JobFactory.ONE_MINUTE_MS;
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.lopb.plugin.util.JobConfig#getRunnable()
			 */
			public Runnable getRunnable() {
				return runnable;
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.lopb.plugin.util.JobConfig#isRepeatable()
			 */
			public boolean isRepeatable() {
				return true;
			}

			private boolean isSessionStartedYesterday() {
				final Date sessionStartedDate = new Date(JobStatsManager.getInstance().getSessionStartAt());
				final Date now = new Date(TimeUtil.now());
				if (DateUtils.isSameDay(sessionStartedDate, now)) {
					return false;
				} else {
					return true;
				}
			}

		};
		JobFactory.getInstance().createJob(exportConfig);
		CsvExporter.getInstance().exportOnInit(LopbPlugin.getExportSnapshotsBaseDir().getAbsolutePath());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.lopb.plugin.util.Manager#shutdown()
	 */
	public void shutdown() {
		CsvExporter.getInstance().exportOnShutdown(LopbPlugin.getExportSnapshotsBaseDir().getAbsolutePath());
	}

	/**
	 * Reset session.
	 */
	public void resetSession() {
		CsvExporter.getInstance().exportOnReset(LopbPlugin.getExportSnapshotsBaseDir().getAbsolutePath());
	}

	/**
	 * Export.
	 * 
	 * @param baseDir
	 *            the base dir
	 */
	public void export(final String baseDir) {
		CsvExporter.getInstance().export(baseDir);
	}

}
