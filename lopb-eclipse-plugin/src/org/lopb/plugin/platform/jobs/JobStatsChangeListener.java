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

// TODO: Auto-generated Javadoc
/**
 * The listener interface for receiving jobStatsChange events.
 * The class that is interested in processing a jobStatsChange
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addJobStatsChangeListener<code> method. When
 * the jobStatsChange event occurs, that object's appropriate
 * method is invoked.
 * 
 * @see JobStatsChangeEvent
 */
public interface JobStatsChangeListener {

	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	public String getId();

	/**
	 * Job stats changed.
	 * 
	 * @param event the event
	 */
	public void jobStatsChanged(final JobStatsChangedEvent event);

	/**
	 * Job stats reset.
	 */
	public void jobStatsReset();

}
