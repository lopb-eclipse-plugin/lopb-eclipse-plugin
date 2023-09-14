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
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IResource;

// TODO: Auto-generated Javadoc
/**
 * The Class JobId.
 */
public class JobId implements Serializable, Comparable<JobId> {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -1L;

	/** The Constant log. */
	private static final Log log = LogFactory.getLog(JobId.class);

	/** The job name. */
	private final String jobName;

	/** The job class name. */
	private final String jobClassName;

	/** The job id as string. */
	private final String jobIdAsString;

	/**
	 * Instantiates a new job id.
	 * 
	 * @param jobClassName
	 *            the job class name
	 * @param jobName
	 *            the job name
	 */
	private JobId(final String jobClassName, final String jobName) {
		this.jobClassName = jobClassName;
		this.jobName = jobName;
		this.jobIdAsString = this.jobClassName + "(" + this.jobName + ")";
	}

	/**
	 * Creates the job id.
	 * 
	 * @param jobIdAsString
	 *            the job id as string
	 * 
	 * @return the job id
	 */
	public static JobId createJobId(final String jobIdAsString) {
		try {
			final int indexOfLeftBracket = jobIdAsString.indexOf('(');
			final String jobClassName = jobIdAsString.substring(0, indexOfLeftBracket);
			final String jobName = jobIdAsString.substring(indexOfLeftBracket + 1, jobIdAsString.lastIndexOf(')'));
			return new JobId(jobClassName, jobName);
		} catch (Exception e) {
			log.error(e);
			return null;
		}
	}

	public static JobId createJobId(final String jobClassName, final String jobName, final List<IResource> resources) {
		if (resources == null || resources.isEmpty()) {
			return new JobId(jobClassName, jobName);
		} else {
			// I want to replace any occurences of a resource name in the
			// jobName with a marker ONLY ONCE!
			// example: "Update Resources A.java B.java C.java" becomes " Update
			// Resources FILE(s)"
			String tmpJobName = jobName;
			
			// test code
//			if(tmpJobName.contains("Update")) {
//				tmpJobName = tmpJobName + " LopbPlugin.java LopbPlugin.java LopbStartup.java LopbPlugin.java";
//			}

			boolean alreadyReplacedOnce = false;
			for (final IResource resource : resources) {
				if (tmpJobName.contains(resource.getName())) {
					if (alreadyReplacedOnce) {
						tmpJobName = tmpJobName.replaceAll(resource.getName(), "");
					} else {
						switch (resource.getType()) {
						case IResource.FILE:
							tmpJobName = tmpJobName.replaceFirst(resource.getName(), "FILE(s)"); // replace
																							// first
							tmpJobName = tmpJobName.replaceAll(resource.getName(), ""); // remove
																						// any
																						// following
							break;
						case IResource.FOLDER:
							tmpJobName = tmpJobName.replaceFirst(resource.getName(), "FOLDER(s)"); // replace
																								// first
							tmpJobName = tmpJobName.replaceAll(resource.getName(), ""); // remove
																						// any
																						// following
							break;
						case IResource.PROJECT:
							tmpJobName = tmpJobName.replaceFirst(resource.getName(), "PROJECT(s)"); // replace
																								// first
							tmpJobName = tmpJobName.replaceAll(resource.getName(), ""); // remove
																						// any
																						// following
							break;
						case IResource.ROOT:
							tmpJobName = tmpJobName.replaceFirst(resource.getName(), "ROOT"); // replace
																							// first
							tmpJobName = tmpJobName.replaceAll(resource.getName(), ""); // remove
																						// any
																						// following
							break;
						}
						alreadyReplacedOnce = true;
					}
					tmpJobName = tmpJobName.trim();
				}
			}
			return new JobId(jobClassName, tmpJobName);
		}
	}

	/**
	 * Gets the job name.
	 * 
	 * @return the jobName
	 */
	public String getJobName() {
		return jobName;
	}

	/**
	 * Gets the job class name.
	 * 
	 * @return the jobClassName
	 */
	public String getJobClassName() {
		return jobClassName;
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
		result = prime * result + ((jobIdAsString == null) ? 0 : jobIdAsString.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final JobId other = (JobId) obj;
		if (jobIdAsString == null) {
			if (other.jobIdAsString != null)
				return false;
		} else if (!jobIdAsString.equals(other.jobIdAsString))
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(JobId other) {
		if (this == other) {
			return 0; // small optimization
		} else {
			// read the javadoc for Comparable... no need to check for nulls
			return this.jobIdAsString.compareTo(other.jobIdAsString);
		}
	}

	/**
	 * Gets the job id as string.
	 * 
	 * @return the job id as string
	 */
	public String getJobIdAsString() {
		return jobIdAsString;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getJobIdAsString();
	}
}
