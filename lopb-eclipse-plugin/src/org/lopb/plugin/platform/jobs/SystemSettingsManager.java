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

import java.net.InetAddress;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

// TODO: Auto-generated Javadoc
/**
 * The Class SystemSettingsManager.
 */
public class SystemSettingsManager {

	/** The Constant log. */
	private static final Log log = LogFactory.getLog(SystemSettingsManager.class);

	/** The Constant instance. */
	private static final SystemSettingsManager instance = new SystemSettingsManager();

	/**
	 * Instantiates a new system settings manager.
	 */
	private SystemSettingsManager() {

	}

	/**
	 * Gets the single instance of SystemSettingsManager.
	 * 
	 * @return single instance of SystemSettingsManager
	 */
	public static SystemSettingsManager getInstance() {
		return instance;
	}

	/**
	 * Get current size of heap in bytes.
	 * 
	 * @return current size of heap in bytes
	 */
	public long getHeapSize() {
		return Runtime.getRuntime().totalMemory();
	}

	/**
	 * Get maximum size of heap in bytes.
	 * 
	 * @return maximum size of heap in bytes
	 */
	public long getMaxHeapSize() {
		return Runtime.getRuntime().maxMemory();
	}

	/**
	 * Get amount of free memory within the heap in bytes.
	 * 
	 * @return amount of free memory within the heap in bytes
	 */
	public long getFreeMemory() {
		return Runtime.getRuntime().freeMemory();
	}

	/**
	 * Gets the number of processors available to the JVM.
	 * 
	 * @return the number of processors available to the JVM
	 */
	public int getAvailableProcessors() {
		return Runtime.getRuntime().availableProcessors();
	}

	/**
	 * Gets the hostname of the localhost machine. If cannot be retrieved for
	 * whatever reason, it will return null.
	 * 
	 * @return hostname of localmachine or null if error
	 */
	public String getHostName() {
		try {
			final InetAddress localMachine = InetAddress.getLocalHost();
			return localMachine.getHostName();
		} catch (java.net.UnknownHostException uhe) {
			log.error("Could not get hostname for localhost.", uhe);
			return null;
		}
	}
}
