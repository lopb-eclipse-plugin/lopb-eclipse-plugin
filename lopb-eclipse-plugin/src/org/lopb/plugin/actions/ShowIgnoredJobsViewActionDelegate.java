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
package org.lopb.plugin.actions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.lopb.plugin.ui.views.ignoredjobsview.LopbIgnoredJobsView;

// TODO: Auto-generated Javadoc
/**
 * The Class ExportAsCsvViewActionDelegate.
 */
public class ShowIgnoredJobsViewActionDelegate implements IViewActionDelegate {

	/** The Constant log. */
	private static final Log log = LogFactory.getLog(ShowIgnoredJobsViewActionDelegate.class);

	/** The view. */
	private IViewPart view;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IViewActionDelegate#init(org.eclipse.ui.IViewPart)
	 */
	public void init(IViewPart view) {
		this.view = view;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#dispose()
	 */
	/**
	 * Dispose.
	 */
	public void dispose() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action) {
		log.debug("execute");

		if (this.view != null) {
			final IViewSite viewSite = this.view.getViewSite();
			if (viewSite != null) {
				final IWorkbenchPage page = this.view.getViewSite().getPage();
				if (page != null) {
					try {
						page.showView(LopbIgnoredJobsView.id);
					} catch (PartInitException e) {
						log.error("Failed to open LopbIgnoredJobsView", e);
					}
				}
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction,
	 *      org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {

	}

}
