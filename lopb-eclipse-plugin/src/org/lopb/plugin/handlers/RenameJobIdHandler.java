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
import org.lopb.plugin.ui.views.LopbView;

// TODO: Auto-generated Javadoc
/**
 * The Class RenameJobIdHandler.
 */
public class RenameJobIdHandler extends AbstractHandler {

	/** The Constant COLUMN_TO_EDIT. */
	private static final int COLUMN_TO_EDIT = 1;

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
		editElement((LopbView) part);
		return null;
	}

	/**
	 * Edits the element.
	 * 
	 * @param view the view
	 */
	private void editElement(LopbView view) {
		final TableViewer viewer = view.getTableViewer();
		final IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
		if (!selection.isEmpty()) {
			viewer.editElement(selection.getFirstElement(), COLUMN_TO_EDIT);
		}
	}

}
