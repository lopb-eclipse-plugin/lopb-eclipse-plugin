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
package org.lopb.plugin.ui.views;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;
import org.lopb.plugin.platform.jobs.JobStatsChangeListener;
import org.lopb.plugin.platform.jobs.JobStatsChangedEvent;
import org.lopb.plugin.platform.jobs.JobStatsManager;

// TODO: Auto-generated Javadoc
/**
 * The Class LopbViewContentProvider.
 */
public class LopbViewContentProvider implements IStructuredContentProvider, JobStatsChangeListener {

	/** The Constant log. */
	private static final Log log = LogFactory.getLog(LopbViewContentProvider.class);

	/** The lopb view content provider instance count. */
	private static final AtomicInteger lopbViewContentProviderInstanceCount = new AtomicInteger(1);

	/** The id. */
	private final String id;

	/** The viewer. */
	private TableViewer viewer;

	/**
	 * Instantiates a new lopb view content provider.
	 */
	public LopbViewContentProvider() {
		this.id = nextId();
	}

	/**
	 * Next id.
	 * 
	 * @return the string
	 */
	private static String nextId() {
		return "LopbViewContentProvider" + lopbViewContentProviderInstanceCount.incrementAndGet();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose() {
		log.debug("dispose");
		JobStatsManager.getInstance().removeJobStatsChangeListener(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object,
	 * java.lang.Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.viewer = (TableViewer) viewer;
		if (!JobStatsManager.getInstance().hasJobStatsChangeListener(this)) {
			JobStatsManager.getInstance().addJobStatsChangeListener(this);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object parent) {
		return JobStatsManager.getInstance().getAllJobStats();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.lopb.plugin.platform.jobs.JobStatsChangeListener#jobStatsChanged(org.lopb.plugin.platform.jobs.
	 * JobStatsChangedEvent)
	 */
	public void jobStatsChanged(JobStatsChangedEvent event) {
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
	 * @see org.lopb.plugin.platform.jobs.JobStatsChangeListener#jobStatsReset()
	 */
	public void jobStatsReset() {
		// updating the View must happen inside the UI thread or will get
		// SWTException
		final Runnable runnable = new ViewResetter();
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
		private final JobStatsChangedEvent event;

		/**
		 * Instantiates a new view updater.
		 * 
		 * @param event
		 *            the event
		 */
		public ViewUpdater(JobStatsChangedEvent event) {
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
				if (event.isNew()) {
					viewer.add(event.getJobStats());
				} else {
					viewer.update(event.getJobStats(), null);
				}
			} finally {
				viewer.getTable().setRedraw(true);
			}
		}
	}

	/**
	 * The Class ViewResetter.
	 */
	private class ViewResetter implements Runnable {

		/**
		 * Instantiates a new view resetter.
		 */
		public ViewResetter() {
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
			log.debug("resetting view");
			viewer.getTable().setRedraw(false);
			try {
				viewer.getTable().removeAll();
			} finally {
				viewer.getTable().setRedraw(true);
			}
		}
	}
}
