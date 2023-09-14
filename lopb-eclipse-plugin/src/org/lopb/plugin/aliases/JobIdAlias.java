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

import java.io.Serializable;

/**
 * Holds an alias to a Job Id. A Job Id Alias is used to assign a more user-friendly name to a job for display and
 * reporting.
 */
public class JobIdAlias implements Serializable, Comparable<JobIdAlias> {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -1L;

	/** The alias as a string. */
	private final String aliasAsString;

	/**
	 * Instantiates a new job id alias.
	 * 
	 * @param aliasAsString
	 *            the alias as string
	 * @see #createJobIdAlias(String)
	 */
	private JobIdAlias(String aliasAsString) {
		super();
		this.aliasAsString = aliasAsString;
	}

	/**
	 * Creates the job id alias.
	 * 
	 * @param aliasAsString
	 *            the alias as string
	 * 
	 * @return the job id alias
	 */
	public static JobIdAlias createJobIdAlias(final String aliasAsString) {
		// TODO do some validation here
		return new JobIdAlias(aliasAsString);
	}

	/**
	 * Gets the alias as string.
	 * 
	 * @return the alias
	 */
	public String getAliasAsString() {
		return aliasAsString;
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
		result = prime * result + ((aliasAsString == null) ? 0 : aliasAsString.hashCode());
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
		final JobIdAlias other = (JobIdAlias) obj;
		if (aliasAsString == null) {
			if (other.aliasAsString != null) {
				return false;
			}
		} else if (!aliasAsString.equals(other.aliasAsString)) {
			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(JobIdAlias other) {
		if (this == other) {
			return 0; // small optimization
		} else if (this.aliasAsString == null) {
			return -1;
		} else {
			// read the javadoc for Comparable... no need to check for nulls
			return this.aliasAsString.compareTo(other.aliasAsString);
		}
	}
}
