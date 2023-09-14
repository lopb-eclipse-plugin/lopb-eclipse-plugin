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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.lopb.plugin.backport.HandlerUtil;
import org.lopb.plugin.export.ExportManager;
import org.lopb.plugin.ui.preferences.LopbPreferenceChangeManager;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * 
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class ExportAsCsvFileHandler extends AbstractHandler {

	/** The Constant log. */
	private static final Log log = LogFactory.getLog(ExportAsCsvFileHandler.class);

	/**
	 * The constructor.
	 */
	public ExportAsCsvFileHandler() {
	}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 * 
	 * @param event
	 *            the event
	 * 
	 * @return the object
	 * 
	 * @throws ExecutionException
	 *             the execution exception
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		log.debug("execute");

		final String outputDir = LopbPreferenceChangeManager.getInstance().getCsvOutputBaseDir();
		ExportManager.getInstance().export(outputDir);
		final Shell parentShell = HandlerUtil.getActiveShell(event);
		final Dialog dialog = new MessageDialog(parentShell, "Success", null, "CSV files written to " + outputDir,
				MessageDialog.INFORMATION, new String[] { "Ok" }, 0);

		dialog.open();

		return null;
	}
}
