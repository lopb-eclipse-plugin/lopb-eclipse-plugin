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
package org.lopb.plugin;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.ui.IStartup;
import org.lopb.plugin.export.ExportManager;
import org.lopb.plugin.platform.jobs.JobStatsManager;
import org.lopb.plugin.ui.preferences.LopbPreferenceChangeManager;

/**
 * An Eclipse startup extension that initializes the job tracking system and preference change listener. This extension
 * is executed after the workbench is started.
 */
public class LopbStartup implements IStartup {

	private static final Log log = LogFactory.getLog(LopbStartup.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IStartup#earlyStartup()
	 */
	public void earlyStartup() {
		log.debug("earlyStartup");

		// initialize job stats manager
		JobStatsManager.getInstance().init();

		// start listening for any changes to preferences
		LopbPreferenceChangeManager.getInstance().init();
		
		// start periodic exports
		ExportManager.getInstance().init();
	}
}
