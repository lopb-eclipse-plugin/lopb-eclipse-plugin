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
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.lopb.plugin.backport.HandlerUtil;
import org.lopb.plugin.ui.views.LopbView;

// TODO: Auto-generated Javadoc
/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * 
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class ShowLopbViewHandler extends AbstractHandler {

	/** The Constant log. */
	private static final Log log = LogFactory.getLog(ShowLopbViewHandler.class);

	/**
	 * The constructor.
	 */
	public ShowLopbViewHandler() {
	}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 * 
	 * @param event the event
	 * 
	 * @return the object
	 * 
	 * @throws ExecutionException the execution exception
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		log.debug("execute");

		final IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		if (window == null) {
			return null;
		}

		final IWorkbenchPage page = window.getActivePage();
		if (page == null) {
			return null;
		}

		try {
			page.showView(LopbView.id);
		} catch (PartInitException e) {
			log.error("Failed to open LopbView", e);
		}
		return null;
	}
}
