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
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.lopb.plugin.export.ExportManager;
import org.lopb.plugin.ui.preferences.LopbPreferenceChangeManager;

// TODO: Auto-generated Javadoc
/**
 * The Class ExportAsCsvViewActionDelegate.
 */
public class ExportAsCsvViewActionDelegate implements IViewActionDelegate {

	/** The Constant log. */
	private static final Log log = LogFactory.getLog(ExportAsCsvViewActionDelegate.class);

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
		final String outputDir = LopbPreferenceChangeManager.getInstance().getCsvOutputBaseDir();
		ExportManager.getInstance().export(outputDir);
		final Shell parentShell = view.getViewSite().getShell();
		final Dialog dialog = new MessageDialog(parentShell, "Success", null, "CSV files written to " + outputDir,
				MessageDialog.INFORMATION, new String[] { "Ok" }, 0);

		dialog.open();
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
