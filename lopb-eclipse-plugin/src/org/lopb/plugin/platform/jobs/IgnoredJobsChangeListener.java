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

public interface IgnoredJobsChangeListener {

	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	public String getId();

	/**
	 * Job stats changed.
	 * 
	 * @param event
	 *            the event
	 */
	public void ignoredJobsChanged(final IgnoredJobsChangedEvent event);

}
