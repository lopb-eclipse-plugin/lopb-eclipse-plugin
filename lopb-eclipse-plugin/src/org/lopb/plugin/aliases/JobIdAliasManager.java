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
package org.lopb.plugin.aliases;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.lopb.plugin.platform.jobs.JobId;

/**
 * Manages a map of {@link JobId} to {@link JobIdAlias} objects, and provides a default alias if a JobId does not have
 * an alias set.
 */
public final class JobIdAliasManager {

	/** The Constant instance. */
	private static final JobIdAliasManager instance = new JobIdAliasManager();

	/** The job id alias map. */
	private final Map<JobId, JobIdAlias> jobIdAliasMap = new ConcurrentHashMap<JobId, JobIdAlias>();

	/**
	 * Instantiates a new job id alias manager.
	 */
	private JobIdAliasManager() {
		// singleton
	}

	/**
	 * Gets the single instance of JobIdAliasManager.
	 * 
	 * @return single instance of JobIdAliasManager
	 */
	public static final JobIdAliasManager getInstance() {
		return instance;
	}

	/**
	 * Gets the job id alias as string. If no alias is set for the given job id, a default alias is generated.
	 * 
	 * @param jobId
	 *            the job id
	 * 
	 * @return the job id alias as string
	 * @see #getDefaultJobIdAlias(JobId)
	 */
	public String getJobIdAliasAsString(final JobId jobId) {
		final JobIdAlias alias = jobIdAliasMap.get(jobId);
		if (alias != null) {
			return alias.getAliasAsString();
		} else {
			return getDefaultJobIdAlias(jobId);
		}
	}

	/**
	 * Sets the job id alias for a job Id, replacing any existing alias.
	 * 
	 * @param jobId
	 *            the job id, must not be null
	 * @param jobIdAlias
	 *            the job id alias, must not be null
	 */
	public void setJobIdAlias(final JobId jobId, final JobIdAlias jobIdAlias) {
		if (jobId == null) {
			throw new IllegalArgumentException("jobId is null");
		}
		if (jobIdAlias == null) {
			throw new IllegalArgumentException("jobIdAlias is null");
		}

		jobIdAliasMap.put(jobId, jobIdAlias);
	}

	/**
	 * Removes the job id alias for a given job id.
	 * 
	 * @param jobId
	 *            the job id
	 */
	public void removeJobIdAlias(final JobId jobId) {
		if (jobId != null) {
			jobIdAliasMap.remove(jobId);
		}
	}

	/**
	 * Gets the job id alias map.
	 * 
	 * @return the job id alias map
	 */
	public Map<JobId, JobIdAlias> getJobIdAliasMap() {
		return jobIdAliasMap;
	}

	/**
	 * Generates a default job id alias based on the class name of the Job.
	 * 
	 * @param jobId
	 *            the job id to generate an alias for
	 * @return a generated alias
	 */
	private String getDefaultJobIdAlias(final JobId jobId) {
		final String[] classNameParts = jobId.getJobClassName().split("[\\.\\$]"); // split
		// by
		// dot
		// notation
		if (classNameParts.length < 4) {
			return jobId.getJobIdAsString();
		} else {
			final String tld = classNameParts[0];
			final String company = classNameParts[1];
			final String product = classNameParts[2];
			final String subproduct = classNameParts[3];
			final String className = extractClassName(classNameParts);
			final StringBuilder buffer = new StringBuilder(40);
			buffer.append(capitalize(company)).append(" - ");
			buffer.append(capitalize(product)).append(" - ");
			buffer.append(capitalize(subproduct)).append(" - ");
			buffer.append(className).append(" (");
			buffer.append(jobId.getJobName()).append(")");
			return buffer.toString();
		}
	}

	/**
	 * Changes a string to title format, or capitalizes the entire string if it is 3 or less characters long.
	 * 
	 * @param s
	 *            the string to capitalize
	 * @return the capitalized version of the string
	 */
	private String capitalize(final String s) {
		if (s.length() < 4) { // example : IBM, SVN,
			return s.toUpperCase();
		} else { // example: Eclipse, Polarion, Clearcase
			return WordUtils.capitalize(s);
		}
	}

	/**
	 * Extract the last class name from an array of a class path parts. For example, the result of
	 * <code>org.mycompany.myplugin.subpackage.MyClass$3</code> would be <code>MyClass</code>.
	 * 
	 * @param classNameParts
	 *            the class path parts
	 * @return the last class name
	 */
	private String extractClassName(final String[] classNameParts) {
		for (int i = classNameParts.length - 1; i >= 0; i--) {
			if (StringUtils.isNumeric(classNameParts[i])) {
				// try next part
			} else {
				return classNameParts[i];
			}
		}
		return classNameParts[classNameParts.length - 1];
	}
}
