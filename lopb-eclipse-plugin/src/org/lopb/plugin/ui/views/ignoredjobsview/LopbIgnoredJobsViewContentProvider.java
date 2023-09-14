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
package org.lopb.plugin.ui.views.ignoredjobsview;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;
import org.lopb.plugin.platform.jobs.IgnoredJobsChangeListener;
import org.lopb.plugin.platform.jobs.IgnoredJobsChangedEvent;
import org.lopb.plugin.platform.jobs.JobId;
import org.lopb.plugin.platform.jobs.JobStatsManager;

// TODO: Auto-generated Javadoc
/**
 * The Class LopbViewContentProvider.
 */
public class LopbIgnoredJobsViewContentProvider implements IStructuredContentProvider, IgnoredJobsChangeListener {

	/** The Constant log. */
	private static final Log log = LogFactory.getLog(LopbIgnoredJobsViewContentProvider.class);

	/** The lopb view content provider instance count. */
	private static final AtomicInteger lopbViewContentProviderInstanceCount = new AtomicInteger(1);

	/** The id. */
	private final String id;

	/** The viewer. */
	private TableViewer viewer;

	/**
	 * Instantiates a new lopb view content provider.
	 */
	public LopbIgnoredJobsViewContentProvider() {
		this.id = nextId();
	}

	/**
	 * Next id.
	 * 
	 * @return the string
	 */
	private static String nextId() {
		return "LopbIgnoredJobsViewContentProvider" + lopbViewContentProviderInstanceCount.incrementAndGet();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose() {
		log.debug("dispose");
		JobStatsManager.getInstance().removeIgnoredJobsChangeListener(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object,
	 * java.lang.Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.viewer = (TableViewer) viewer;
		if (!JobStatsManager.getInstance().hasIgnoredJobsChangeListener(this)) {
			JobStatsManager.getInstance().addIgnoredJobsChangeListener(this);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object parent) {
		final Collection<JobId> jobIds = JobStatsManager.getInstance().getIgnoredJobIds();
		final Object[] elements = new Object[jobIds.size()];
		int i = 0;
		for (final JobId jobId : jobIds) {
			elements[i++] = jobId;
		}
		return elements;
	}

	public void ignoredJobsChanged(IgnoredJobsChangedEvent event) {
		// updating the View must happen inside the UI thread or will get
		// SWTException
		final Runnable runnable = new ViewUpdater(event);
		if (Display.getCurrent() == null) {
			Display.getDefault().asyncExec(runnable);
		} else {
			runnable.run();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.lopb.plugin.platform.jobs.JobStatsChangeListener#getId()
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * The Class ViewUpdater.
	 */
	private class ViewUpdater implements Runnable {

		/** The event. */
		private final IgnoredJobsChangedEvent event;

		/**
		 * Instantiates a new view updater.
		 * 
		 * @param event
		 *            the event
		 */
		public ViewUpdater(IgnoredJobsChangedEvent event) {
			this.event = event;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
			log.debug("updating view");
			viewer.getTable().setRedraw(false);
			try {
				if (event.isIgnored()) {
					viewer.add(event.getJobId());
				}
			} finally {
				viewer.getTable().setRedraw(true);
			}
		}
	}
}
