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
package org.lopb.plugin.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.lopb.plugin.LopbPlugin;

/**
 * A factory for creating Job objects.
 */
public class JobFactory {

	/** The Constant instance. */
	private static final JobFactory instance = new JobFactory();

	/** The Constant log. */
	private static final Log log = LogFactory.getLog(JobFactory.class);

	/** The Constant ONE_MINUTE_MS. */
	public static final long ONE_MINUTE_MS = 60 * 1000;

	/** The Constant TEN_MINUTES_MS. */
	public static final long TEN_MINUTES_MS = 10 * ONE_MINUTE_MS;

	/** The Constant THIRTY_MINUTES_MS. */
	public static final long THIRTY_MINUTES_MS = 30 * ONE_MINUTE_MS;

	/**
	 * Instantiates a new job factory.
	 */
	private JobFactory() {

	}

	/**
	 * Gets the single instance of JobFactory.
	 * 
	 * @return single instance of JobFactory
	 */
	public static final JobFactory getInstance() {
		return instance;
	}

	public void createJob(final JobConfig config) {
		final Job job = new Job(config.getName()) {
			protected IStatus run(IProgressMonitor monitor) {
				try {
					monitor.beginTask(config.getName(), 1);
					config.getRunnable().run();
					monitor.worked(1);
					return Status.OK_STATUS;
				} catch (Exception e) {
					log.error(e);
					return new Status(Status.ERROR, LopbPlugin.getDefault().getBundle().getSymbolicName(), -1, e
							.getMessage(), e);
				} finally {
					monitor.done();
					if (config.isRepeatable()) {
						schedule(config.getPeriod());
					}
				}
			}
		};
		job.schedule(config.getDelay());
	}
}
