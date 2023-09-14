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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.lopb.plugin.LopbPlugin;
import org.lopb.plugin.backport.LopbWorkbenchTrimWidget;
import org.lopb.plugin.platform.jobs.JobStatsManager;
import org.lopb.plugin.ui.views.LopbView;
import org.lopb.plugin.util.Manager;

// TODO: Auto-generated Javadoc
/**
 * The Class LopbPreferenceChangeManager.
 */
public class LopbPreferenceChangeManager implements IPropertyChangeListener, Manager {

	/** The pref store. */
	private final IPreferenceStore prefStore;

	/** The Constant log. */
	private static final Log log = LogFactory.getLog(LopbPreferenceChangeManager.class);

	/** The Constant instance. */
	private static final LopbPreferenceChangeManager instance = new LopbPreferenceChangeManager();

	/**
	 * Instantiates a new lopb preference change manager.
	 */
	private LopbPreferenceChangeManager() {
		this.prefStore = LopbPlugin.getDefault().getPreferenceStore();
	}

	/**
	 * Gets the single instance of LopbPreferenceChangeManager.
	 * 
	 * @return single instance of LopbPreferenceChangeManager
	 */
	public static LopbPreferenceChangeManager getInstance() {
		return instance;
	}

	/**
	 * Start listening.
	 */
	public void init() {
		this.prefStore.addPropertyChangeListener(this);
	}

	/**
	 * Stop listening.
	 */
	public void shutdown() {
		prefStore.removePropertyChangeListener(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent event) {
		final String property = event.getProperty();
		log.debug("changing property: " + property);

		if (LopbPreferenceConstants.LOPB_JOBS_INCLUDE_SYSTEM_JOBS.equals(property)) {
			processSystemJobsIncludedChange();
		} else if (LopbPreferenceConstants.LOPB_ENABLE_JOB_RENAMING.equals(property)) {
			processEnableJobRenamingChange();
		} else if (LopbPreferenceConstants.LOPB_CSV_OUTPUT_BASE_DIR.equals(property)) {
			processCsvOutputBaseDirChange();
		} else if (LopbPreferenceConstants.LOPB_EXPORT_SNAPSHOT_PERIOD.equals(property)) {
			processExportSnapshotPeriodChange();
		} else if (LopbPreferenceConstants.LOPB_SHOW_STATUS.equals(property)) {
			processShowLopbStatus((Boolean) event.getNewValue());
		}
	}

	/**
	 * Checks if is system jobs included.
	 * 
	 * @return true, if is system jobs included
	 */
	public boolean isSystemJobsIncluded() {
		return prefStore.getBoolean(LopbPreferenceConstants.LOPB_JOBS_INCLUDE_SYSTEM_JOBS);
	}

	/**
	 * Process system jobs included change.
	 */
	private void processSystemJobsIncludedChange() {
		JobStatsManager.getInstance().setSystemJobsIncluded(
				prefStore.getBoolean(LopbPreferenceConstants.LOPB_JOBS_INCLUDE_SYSTEM_JOBS));

		// lets refresh the table view now
		try {
			final LopbView view = (LopbView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
					.showView(LopbView.id);
			view.getTableViewer().refresh();
		} catch (PartInitException pie) {
			log.error(pie);
		}
	}

	/**
	 * Process enable job renaming change.
	 */
	private void processEnableJobRenamingChange() {
		try {
			final LopbView view = (LopbView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
					.showView(LopbView.id);
			if (prefStore.getBoolean(LopbPreferenceConstants.LOPB_ENABLE_JOB_RENAMING)) {
				view.enableJobIdEditor();
			} else {
				view.disableJobIdEditor();
			}
		} catch (PartInitException pie) {
			log.error(pie);
		}
	}

	private void processExportSnapshotPeriodChange() {
		// nothing to do
	}

	/**
	 * Process enable job renaming change.
	 */
	private void processShowLopbStatus(final Boolean b) {
		LopbWorkbenchTrimWidget.getInstance().setVisible(b);
	}

	/**
	 * Checks if is job renaming enabled.
	 * 
	 * @return true, if is job renaming enabled
	 */
	public boolean isJobRenamingEnabled() {
		return prefStore.getBoolean(LopbPreferenceConstants.LOPB_ENABLE_JOB_RENAMING);
	}

	/**
	 * Process csv output base dir change.
	 */
	private void processCsvOutputBaseDirChange() {
		// nothing to do really
	}

	public boolean isLopbStatusEnabled() {
		return prefStore.getBoolean(LopbPreferenceConstants.LOPB_SHOW_STATUS);
	}

	/**
	 * Gets the csv output base dir.
	 * 
	 * @return the csv output base dir
	 */
	public String getCsvOutputBaseDir() {
		return prefStore.getString(LopbPreferenceConstants.LOPB_CSV_OUTPUT_BASE_DIR);
	}

	public long getExportSnapshotPeriodMinutes() {
		return prefStore.getInt(LopbPreferenceConstants.LOPB_EXPORT_SNAPSHOT_PERIOD);
	}

}
