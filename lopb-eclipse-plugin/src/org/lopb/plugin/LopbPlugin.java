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

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.lopb.plugin.export.ExportManager;
import org.lopb.plugin.labels.LabelsManager;
import org.lopb.plugin.platform.jobs.JobStatsManager;
import org.lopb.plugin.ui.preferences.LopbPreferenceChangeManager;
import org.osgi.framework.BundleContext;

/**
 * An Eclipse plug-in runtime implementation that manages LoPB's lifecycle.
 */
public class LopbPlugin extends AbstractUIPlugin {

	private static final Log log = LogFactory.getLog(LopbPlugin.class);

	/** The unique ID for the LoPB plug-in */
	private static final String PLUGIN_ID = "org.lopb.plugin";

	/** The singleton instance of this plug-in */
	private static LopbPlugin plugin;

	/**
	 * The constructor.
	 */
	public LopbPlugin() {
		super();
		log.debug("constructor");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext )
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		log.debug("start");
		// two lines below are from the Eclipse sample code
		super.start(context);
		plugin = this;

		// load job labels
		LabelsManager.getInstance().init(); // init here instead of LopbStartup
											// because we only need this when we
											// have loaded the view UI.
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext )
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		log.debug("stop");

		LabelsManager.getInstance().shutdown();

		
		// NOTE: these three managers are initialized in LopbStartup.		
		// execute shutdown routines on the JobStatsManager
		JobStatsManager.getInstance().shutdown();
		// stop listening for preference changes
		LopbPreferenceChangeManager.getInstance().shutdown();
		// dump last bunch of stats to file
		ExportManager.getInstance().shutdown();
					
		
		// two lines below are from the Eclipse sample code
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance of this plug-in runtime class.
	 * 
	 * @return the shared instance
	 */
	public static LopbPlugin getDefault() {
		log.debug("getDefault");
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path.
	 * 
	 * @param path
	 *            the path
	 * 
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		log.debug("getImageDescriptor");
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	/**
	 * Gets the LoPB config file.
	 * 
	 * @return the config file
	 */
	public static File getUserConfigFile() {
		return getDefault().getStateLocation().append("lopb-config.xml").toFile();
	}

	public static File getExportSnapshotsBaseDir() {
		return getDefault().getStateLocation().append("data").toFile();
	}

	/**
	 * Gets the default LoPB config file.
	 * 
	 * @return the default config file
	 */
	public static File getDefaultConfigFile() {
		try {
			final URL url = FileLocator.find(getDefault().getBundle(), new Path("/config/default-lopb-config.xml"),
					null);
			if (url != null) {
				return new File(FileLocator.toFileURL(url).getPath());
			}
		} catch (final IOException ioe) {
			log.error(ioe);
		}
		return null;
	}
}
