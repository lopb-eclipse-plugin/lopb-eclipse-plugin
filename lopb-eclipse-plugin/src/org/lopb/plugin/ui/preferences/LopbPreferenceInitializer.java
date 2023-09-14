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
package org.lopb.plugin.ui.preferences;

import java.io.File;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.lopb.plugin.LopbPlugin;
import org.lopb.plugin.util.JobFactory;

// TODO: Auto-generated Javadoc
/**
 * The Class LopbPreferenceInitializer.
 */
public class LopbPreferenceInitializer extends AbstractPreferenceInitializer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
		final IPreferenceStore store = LopbPlugin.getDefault().getPreferenceStore();
		store.setDefault(LopbPreferenceConstants.LOPB_JOBS_INCLUDE_SYSTEM_JOBS, false);
		store.setDefault(LopbPreferenceConstants.LOPB_ENABLE_JOB_RENAMING, false);
		store.setDefault(LopbPreferenceConstants.LOPB_SHOW_STATUS, false);
		store.setDefault(LopbPreferenceConstants.LOPB_CSV_OUTPUT_BASE_DIR, createBaseDir());
		store.setDefault(LopbPreferenceConstants.LOPB_EXPORT_SNAPSHOT_PERIOD, 60); // default is to save every hour (60 minutes)
	}

	/**
	 * Creates the base dir.
	 * 
	 * @return the string
	 */
	private String createBaseDir() {
		String tmpDirName = System.getProperty("java.io.tmpdir");
		if (!tmpDirName.endsWith(File.separator)) {
			tmpDirName += File.separator;
		}
		final File baseDir = new File(tmpDirName + "lopb");
		if (!baseDir.exists()) {
			baseDir.mkdirs();
		}
		return baseDir.getAbsolutePath();
	}
}
