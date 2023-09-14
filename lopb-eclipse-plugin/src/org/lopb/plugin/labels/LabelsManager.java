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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.collections.map.MultiValueMap;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.commons.lang.text.StrMatcher;
import org.apache.commons.lang.text.StrTokenizer;
import org.lopb.plugin.platform.jobs.JobId;
import org.lopb.plugin.util.Manager;

// TODO: Auto-generated Javadoc
/**
 * The Class LabelsManager.
 */
public class LabelsManager implements Manager {

	/** The Constant ALLOWED_LABEL_SEPERATORS. */
	static final private char[] ALLOWED_LABEL_SEPERATORS = new char[] { ',', ':', ';' };

	/** The Constant instance. */
	static final private LabelsManager instance = new LabelsManager();

	/** The listeners. */
	private final List<LabelsChangeListener> listeners = new CopyOnWriteArrayList<LabelsChangeListener>();

	/** The labels map. */
	private final Map<String, Boolean> labelsMap = new TreeMap<String, Boolean>();

	/** The label jobs map. */
	private final MultiValueMap labelJobsMap = MultiValueMap.decorate(new HashMap<String, Set<JobId>>(), HashSet.class);

	/** The job labels map. */
	private final MultiValueMap jobLabelsMap = MultiValueMap.decorate(new HashMap<JobId, Set<String>>(), HashSet.class);

	/** The show any labels. */
	private boolean showAnyLabels = true;

	/**
	 * Gets the single instance of LabelsManager.
	 * 
	 * @return single instance of LabelsManager
	 */
	static public LabelsManager getInstance() {
		return instance;
	}

	/**
	 * Inits the.
	 */
	public void init() {
		LabelsPersistenceHelper.load();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.lopb.plugin.util.Manager#shutdown()
	 */
	public void shutdown() {
		// do nothing
	}

	/**
	 * Adds the label to job.
	 * 
	 * @param label
	 *            the label
	 * @param jobId
	 *            the job id
	 */
	public void addLabelToJob(String label, JobId jobId) {
		if (StringUtils.isEmpty(label) || jobId == null) {
			return;
		}

		label = formatLabel(label);

		labelJobsMap.put(label, jobId);
		jobLabelsMap.put(jobId, label);

		// set new label as included
		if (!labelsMap.containsKey(label)) {
			labelsMap.put(label, Boolean.TRUE);
		}

		notifyLabelsChangeEvent();
		LabelsPersistenceHelper.persist();
	}

	/**
	 * Sets the labels to job.
	 * 
	 * @param labels
	 *            the labels
	 * @param jobId
	 *            the job id
	 */
	public void setLabelsToJob(String labels, JobId jobId) {
		if (labels == null || jobId == null) {
			return;
		}

		labels = formatLabel(labels);

		final StrTokenizer tokenizer = new StrTokenizer(labels, StrMatcher
				.charSetMatcher(LabelsManager.ALLOWED_LABEL_SEPERATORS));

		final String[] labelArr = tokenizer.getTokenArray();

		// clean up
		final Set<String> jobLabelsSet = (Set<String>) jobLabelsMap.get(jobId);

		if (jobLabelsSet != null) {
			for (String label : jobLabelsSet) {
				if (!StringUtils.isEmpty(label)) {
					label = formatLabel(label);

					// remove this jobId set for old label
					labelJobsMap.remove(label, jobId);
				}
			}
		}

		// remove all labels for this jobId
		jobLabelsMap.remove(jobId);

		// add new labels now
		for (String label : labelArr) {
			if (!StringUtils.isEmpty(label)) {
				label = formatLabel(label);

				labelJobsMap.put(label, jobId);
				jobLabelsMap.put(jobId, label);

				// set new label as included
				if (!labelsMap.containsKey(label)) {
					labelsMap.put(label, Boolean.TRUE);
				}
			}
		}

		notifyLabelsChangeEvent();
		LabelsPersistenceHelper.persist();
	}

	/**
	 * Removes the label from job.
	 * 
	 * @param label
	 *            the label
	 * @param jobId
	 *            the job id
	 */
	public void removeLabelFromJob(String label, JobId jobId) {
		if (StringUtils.isEmpty(label) || jobId == null) {
			return;
		}

		label = formatLabel(label);

		labelJobsMap.remove(label, jobId);
		jobLabelsMap.remove(jobId, label);

		notifyLabelsChangeEvent();
		LabelsPersistenceHelper.persist();
	}

	/**
	 * Adds the new label.
	 * 
	 * @param label
	 *            the label
	 */
	public void addNewLabel(String label) {
		if (StringUtils.isEmpty(label)) {
			return;
		}

		label = formatLabel(label);

		// set new label as included
		if (!labelsMap.containsKey(label)) {
			labelsMap.put(label, Boolean.TRUE);
		}

		notifyLabelsChangeEvent();
		LabelsPersistenceHelper.persist();
	}

	/**
	 * Include label.
	 * 
	 * @param label
	 *            the label
	 */
	public void includeLabel(String label) {
		if (StringUtils.isEmpty(label)) {
			return;
		}

		label = formatLabel(label);

		labelsMap.put(label, Boolean.TRUE);

		notifyLabelsChangeEvent();
		LabelsPersistenceHelper.persist();
	}

	/**
	 * Exclude label.
	 * 
	 * @param label
	 *            the label
	 */
	public void excludeLabel(String label) {
		if (StringUtils.isEmpty(label)) {
			return;
		}

		label = formatLabel(label);

		labelsMap.put(label, Boolean.FALSE);

		notifyLabelsChangeEvent();
		LabelsPersistenceHelper.persist();
	}

	/**
	 * Checks if is job included.
	 * 
	 * @param jobId
	 *            the job id
	 * 
	 * @return true, if is job included
	 */
	public boolean isJobIncluded(JobId jobId) {
		final Set<String> labelsSet = (Set<String>) jobLabelsMap.get(jobId);

		if (showAnyLabels) {
			if (labelsSet != null) {
				for (final String label : labelsSet) {
					if (labelsMap.containsKey(label)) {
						if (labelsMap.get(label) == Boolean.TRUE) {
							return true;
						}
					}
				}
				return false;
			}

			return true;
		} else {
			if (labelsSet != null) {
				for (final String label : labelsSet) {
					if (labelsMap.containsKey(label)) {
						if (labelsMap.get(label) == Boolean.FALSE) {
							return false;
						}
					}
				}
				return true;
			}

			return false;
		}
	}

	/**
	 * Adds the labels change listener.
	 * 
	 * @param listener
	 *            the listener
	 */
	public void addLabelsChangeListener(LabelsChangeListener listener) {
		listeners.add(listener);
	}

	/**
	 * Removes the labels change listener.
	 * 
	 * @param listener
	 *            the listener to remove
	 */
	public void removeLabelsChangeListener(LabelsChangeListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Gets the included labels.
	 * 
	 * @return the included labels
	 */
	public List<String> getIncludedLabels() {
		final List<String> includedLabelsList = new ArrayList<String>();
		for (final String label : labelsMap.keySet()) {
			if (labelsMap.get(label) == Boolean.TRUE) {
				includedLabelsList.add(label);
			}
		}
		return includedLabelsList;
	}

	/**
	 * Checks if is label included.
	 * 
	 * @param label
	 *            the label
	 * 
	 * @return true, if is label included
	 */
	public boolean isLabelIncluded(String label) {
		if (labelsMap.containsKey(label)) {
			return (labelsMap.get(label) == Boolean.TRUE);
		}

		return true;
	}

	/**
	 * Gets the all labels.
	 * 
	 * @return the all labels
	 */
	public Set<String> getAllLabels() {
		return labelsMap.keySet();
	}

	/**
	 * Gets the labels as string.
	 * 
	 * @param jobId
	 *            the job id
	 * 
	 * @return the labels as string
	 */
	public String getLabelsAsString(JobId jobId) {
		final Set<String> labelsSet = (Set<String>) jobLabelsMap.get(jobId);
		if (labelsSet != null) {
			return StringUtils.join(labelsSet, ", ");
		}
		return "";
	}

	/**
	 * Rename label.
	 * 
	 * @param oldLabel
	 *            the old label
	 * @param newLabel
	 *            the new label
	 */
	public void renameLabel(String oldLabel, String newLabel) {
		if (StringUtils.isEmpty(oldLabel) || StringUtils.isEmpty(newLabel)) {
			return;
		}

		oldLabel = formatLabel(oldLabel);
		newLabel = formatLabel(newLabel);

		final Boolean isIncluded = labelsMap.get(oldLabel);
		if (isIncluded != null) {
			labelsMap.remove(oldLabel);
			labelsMap.put(newLabel, isIncluded);
		}

		Set<JobId> jobIdSet = (Set<JobId>) labelJobsMap.get(oldLabel);
		if (jobIdSet != null) {
			labelJobsMap.remove(oldLabel);
			labelJobsMap.putAll(newLabel, jobIdSet);
		}

		jobIdSet = (Set<JobId>) labelJobsMap.get(newLabel);
		if (jobIdSet != null) {
			for (final JobId jobId : jobIdSet) {
				if (jobLabelsMap.containsKey(jobId)) {
					jobLabelsMap.remove(jobId, oldLabel);
					jobLabelsMap.put(jobId, newLabel);
				}
			}
		}

		notifyLabelsChangeEvent();
		LabelsPersistenceHelper.persist();
	}

	/**
	 * Removes the label.
	 * 
	 * @param Label
	 *            the label
	 */
	public void removeLabel(String Label) {
		if (StringUtils.isEmpty(Label)) {
			return;
		}

		Label = formatLabel(Label);

		labelsMap.remove(Label);

		final Set<JobId> jobIdSet = (Set<JobId>) labelJobsMap.get(Label);
		if (jobIdSet != null) {
			for (final JobId jobId : jobIdSet) {
				jobLabelsMap.remove(jobId, Label);
			}
		}

		labelJobsMap.remove(Label);

		notifyLabelsChangeEvent();
		LabelsPersistenceHelper.persist();
	}

	/**
	 * Gets the labels map.
	 * 
	 * @return the labels map
	 */
	public Map<String, Boolean> getLabelsMap() {
		return labelsMap;
	}

	/**
	 * Gets the label jobs map.
	 * 
	 * @return the label jobs map
	 */
	public MultiValueMap getLabelJobsMap() {
		return labelJobsMap;
	}

	/**
	 * Gets the job labels map.
	 * 
	 * @return the job labels map
	 */
	public MultiValueMap getJobLabelsMap() {
		return jobLabelsMap;
	}

	/**
	 * Format label.
	 * 
	 * @param label
	 *            the label
	 * 
	 * @return the string
	 */
	public String formatLabel(String label) {
		return WordUtils.capitalize(label.trim());
	}

	/**
	 * Checks if is show any labels.
	 * 
	 * @return true, if is show any labels
	 */
	public boolean isShowAnyLabels() {
		return showAnyLabels;
	}

	/**
	 * Sets the show any labels.
	 * 
	 * @param showAnyLabels
	 *            the new show any labels
	 */
	public void setShowAnyLabels(boolean showAnyLabels) {
		this.showAnyLabels = showAnyLabels;
	}

	/**
	 * Notify labels change event.
	 */
	private void notifyLabelsChangeEvent() {
		for (final LabelsChangeListener listener : listeners) {
			listener.handleLabelsChangedEvent();
		}
	}
}
