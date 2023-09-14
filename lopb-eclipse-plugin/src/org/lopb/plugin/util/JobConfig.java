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

// TODO: Auto-generated Javadoc
/**
 * The Interface JobConfig.
 */
public interface JobConfig {

	/**
	 * Gets the name.
	 * 
	 * @return the name
	 */
	public String getName();

	/**
	 * Gets the delay.
	 * 
	 * @return the delay
	 */
	public long getDelay();

	/**
	 * Gets the period.
	 * 
	 * @return the period
	 */
	public long getPeriod();

	/**
	 * Gets the runnable.
	 * 
	 * @return the runnable
	 */
	public Runnable getRunnable();

	/**
	 * Checks if is repeatable.
	 * 
	 * @return true, if is repeatable
	 */
	public boolean isRepeatable();
}
