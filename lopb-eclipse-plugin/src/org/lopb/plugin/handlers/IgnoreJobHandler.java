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
package org.lopb.plugin.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.ui.IWorkbenchPart;
import org.lopb.plugin.backport.HandlerUtil;
import org.lopb.plugin.platform.jobs.JobStats;
import org.lopb.plugin.platform.jobs.JobStatsManager;
import org.lopb.plugin.ui.views.LopbView;

// TODO: Auto-generated Javadoc
/**
 * The Class IgnoreJobHandler.
 */
public class IgnoreJobHandler extends AbstractHandler {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final IWorkbenchPart part = HandlerUtil.getActivePart(event);
		if (!(part instanceof LopbView)) {
			return null;
		}
		ignoreElement((LopbView) part);
		return null;
	}

	/**
	 * Ignore element.
	 * 
	 * @param view the view
	 */
	private void ignoreElement(LopbView view) {
		final TableViewer viewer = view.getTableViewer();
		final IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
		if (!selection.isEmpty()) {
			final JobStats jobStats = (JobStats) selection.getFirstElement();
			JobStatsManager.getInstance().ignoreJob(jobStats.getJobId());
			JobStatsManager.getInstance().refreshStats();
		}
	}

}
