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
package org.lopb.plugin.labels;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.map.MultiValueMap;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.XMLMemento;
import org.lopb.plugin.LopbPlugin;
import org.lopb.plugin.platform.jobs.JobId;

// TODO: Auto-generated Javadoc

/**
 * The Class LabelsPersistenceHelper.
 */
public class LabelsPersistenceHelper {

	/** The Constant log. */
	private static final Log log = LogFactory
			.getLog(LabelsPersistenceHelper.class);

	/** The Constant LABELS_CONFIG_FILE. */
	static final private String LABELS_CONFIG_FILE = "lopb-labels.xml";

	/** The Constant TAG_ROOT. */
	private static final String TAG_ROOT = "lopb-labels";

	/** The Constant TAG_LABELS. */
	private static final String TAG_LABELS = "labels";

	/** The Constant TAG_LABEL. */
	private static final String TAG_LABEL = "label";

	/** The Constant TAG_LABEL_JOBS. */
	private static final String TAG_LABEL_JOBS = "labelJobs";

	/** The Constant TAG_LABEL_JOB. */
	private static final String TAG_LABEL_JOB = "labelJob";

	/** The Constant ATTRIB_NAME. */
	private static final String ATTRIB_NAME = "name";

	/** The Constant ATTRIB_IS_INCLUDED. */
	private static final String ATTRIB_IS_INCLUDED = "isIncluded";

	/** The Constant ATTRIB_JOB_ID. */
	private static final String ATTRIB_JOB_ID = "jobId";

	/**
	 * Persist.
	 */
	static public void load() {
		FileReader reader = null;
		try {
			File lopbLabelFile = LabelsPersistenceHelper.getUserLabelFile();
			if (lopbLabelFile == null || !lopbLabelFile.exists()
					|| !lopbLabelFile.canRead()) {
				lopbLabelFile = LabelsPersistenceHelper.getDefaultLabelFile();
			}

			if (lopbLabelFile.exists() && lopbLabelFile.canRead()) {
				reader = new FileReader(lopbLabelFile);

				final XMLMemento memento = XMLMemento.createReadRoot(reader);

				loadLabels(memento);
				loadLabelJobs(memento);
			} else {
				log.warn("Could not find config file: "
						+ lopbLabelFile.getPath());
			}
		} catch (Exception e) {
			log.error("Could not load config file: " + e);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException ioe) {
				}
			}
		}
	}

	/**
	 * Load labels.
	 * 
	 * @param memento the memento
	 */
	static private void loadLabels(XMLMemento memento) {
		Map<String, Boolean> labelsMap = LabelsManager.getInstance()
				.getLabelsMap();

		final IMemento[] labelsMemento = memento.getChildren(TAG_LABELS);
		if (labelsMemento != null && labelsMemento.length > 0) {
			for (final IMemento labelMemento : labelsMemento[0]
					.getChildren(TAG_LABEL)) {
				final String label = labelMemento.getString(ATTRIB_NAME);
				String isIncludedAsString = labelMemento
						.getString(ATTRIB_IS_INCLUDED);

				if (!StringUtils.isEmpty(label)) {
					if (StringUtils.isEmpty(isIncludedAsString)) {
						isIncludedAsString = Boolean.TRUE.toString();
					}
					labelsMap.put(label, Boolean
							.parseBoolean(isIncludedAsString));
				}
			}
		}
	}

	/**
	 * Load label jobs.
	 * 
	 * @param memento the memento
	 */
	static private void loadLabelJobs(XMLMemento memento) {
		MultiValueMap labelJobsMap = LabelsManager.getInstance()
				.getLabelJobsMap();
		MultiValueMap jobLabelsMap = LabelsManager.getInstance()
				.getJobLabelsMap();

		final IMemento[] labelJobsMemento = memento.getChildren(TAG_LABEL_JOBS);
		if (labelJobsMemento != null && labelJobsMemento.length > 0) {
			for (final IMemento labelJobMemento : labelJobsMemento[0]
					.getChildren(TAG_LABEL_JOB)) {
				final String label = labelJobMemento.getString(ATTRIB_NAME);
				final String jobIdAsString = labelJobMemento
						.getString(ATTRIB_JOB_ID);

				if (!StringUtils.isEmpty(label)
						&& !StringUtils.isEmpty(jobIdAsString)) {
					JobId jobId = JobId.createJobId(jobIdAsString);
					if (jobId != null) {
						labelJobsMap.put(label, jobId);
						jobLabelsMap.put(jobId, label);
					}
				}
			}
		}
	}

	/**
	 * Persist.
	 */
	static public void persist() {
		FileWriter writer = null;
		try {
			final XMLMemento memento = XMLMemento.createWriteRoot(TAG_ROOT);

			persistLabels(memento);
			persistLabelJobs(memento);

			writer = new FileWriter(LabelsPersistenceHelper.getUserLabelFile());
			memento.save(writer);
		} catch (Exception e) {
			log.error("Could not persist labels", e);
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException ioe) {
				}
			}
		}
	}

	/**
	 * Persist labels.
	 * 
	 * @param memento the memento
	 */
	static private void persistLabels(XMLMemento memento) {
		final IMemento labelsMemento = memento.createChild(TAG_LABELS);

		Map<String, Boolean> labelsMap = LabelsManager.getInstance()
				.getLabelsMap();

		Set<String> labelKeySet = labelsMap.keySet();
		for (final String label : labelKeySet) {
			final IMemento labelMemento = labelsMemento.createChild(TAG_LABEL);
			labelMemento.putString(ATTRIB_NAME, label);
			labelMemento.putString(ATTRIB_IS_INCLUDED, labelsMap.get(label)
					.toString());
		}
	}

	/**
	 * Persist label jobs.
	 * 
	 * @param memento the memento
	 */
	static private void persistLabelJobs(XMLMemento memento) {
		final IMemento labelJobsMemento = memento.createChild(TAG_LABEL_JOBS);

		MultiValueMap labelJobsMap = LabelsManager.getInstance()
				.getLabelJobsMap();

		Set<String> labelKeySet = labelJobsMap.keySet();
		for (final String label : labelKeySet) {
			Set<JobId> jobIdSet = (Set<JobId>) labelJobsMap.get(label);

			for (JobId jobId : jobIdSet) {
				final IMemento labelMemento = labelJobsMemento
						.createChild(TAG_LABEL_JOB);
				labelMemento.putString(ATTRIB_NAME, label);
				labelMemento.putString(ATTRIB_JOB_ID, jobId.getJobIdAsString());
			}
		}
	}

	/**
	 * Gets the label file.
	 * 
	 * @return the label file
	 */
	static private File getUserLabelFile() {
		return LopbPlugin.getDefault().getStateLocation().append(
				LABELS_CONFIG_FILE).toFile();
	}

	/**
	 * Gets the default label file.
	 * 
	 * @return the default label file
	 */
	private static File getDefaultLabelFile() {
		try {
			final URL url = FileLocator.find(LopbPlugin.getDefault()
					.getBundle(), new Path("/config/default-lopb-labels.xml"),
					null);
			if (url != null) {
				return new File(FileLocator.toFileURL(url).getPath());
			}
		} catch (IOException ioe) {
			log.error(ioe);
		}
		return null;
	}
}
