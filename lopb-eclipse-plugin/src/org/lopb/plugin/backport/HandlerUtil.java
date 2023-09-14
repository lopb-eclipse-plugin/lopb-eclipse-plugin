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
package org.lopb.plugin.backport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.lopb.plugin.LopbPlugin;

// TODO: Auto-generated Javadoc
/**
 * The Class HandlerUtil.
 */
public class HandlerUtil {

	private static final Log log = LogFactory.getLog(HandlerUtil.class);

	/**
	 * Gets the active shell.
	 * 
	 * @param event
	 *            the event
	 * 
	 * @return the active shell
	 */
	public static Shell getActiveShell(ExecutionEvent event) {
		return Display.getCurrent().getActiveShell();
	}

	/**
	 * Gets the active workbench window checked.
	 * 
	 * @param event
	 *            the event
	 * 
	 * @return the active workbench window checked
	 */
	public static IWorkbenchWindow getActiveWorkbenchWindowChecked(ExecutionEvent event) {
		return getActiveWorkbenchWindow();
	}

	/**
	 * Gets the active workbench window.
	 * 
	 * @return the active workbench window
	 */
	public static IWorkbenchWindow getActiveWorkbenchWindow() {
		final IWorkbench workbench = LopbPlugin.getDefault().getWorkbench();
		if (workbench != null) {
			return workbench.getActiveWorkbenchWindow();
		} else {
			return null;
		}
	}

	/**
	 * Gets the active part.
	 * 
	 * @param event
	 *            the event
	 * 
	 * @return the active part
	 */
	public static IWorkbenchPart getActivePart(ExecutionEvent event) {
		final IWorkbenchPage workbenchPage = getActivePage();
		if (workbenchPage != null) {
			return workbenchPage.getActivePart();
		} else {
			return null;
		}
	}

	public static IWorkbenchPage getActivePage() {
		final IWorkbenchWindow workbenchWindow = getActiveWorkbenchWindow();
		if (workbenchWindow != null) {
			return workbenchWindow.getActivePage();
		} else {
			return null;
		}
	}

	public static ISelection getActiveSelection() {
		final IWorkbenchPage page = getActivePage();
		if (page != null) {
			return page.getSelection();
		} else {
			return null;
		}
	}

	public static List<IResource> getActiveResources() {
		try {
			final List<IResource> resources = new ArrayList<IResource>();
			final ISelection selection = getActiveSelection();
			if (selection != null && !selection.isEmpty()) {
				if (selection instanceof IStructuredSelection) {
					final Iterator<?> iter = ((IStructuredSelection) selection).iterator();
					while (iter.hasNext()) {
						final Object element = iter.next();
						final IResource resource = getResource(element);
						if (resource != null) {
							resources.add(resource);
						}
					}
				}
			}
			return Collections.unmodifiableList(resources);
		} catch (Exception e) {
			log.error(e);
			return Collections.EMPTY_LIST;
		}
	}

	private static IResource getResource(Object element) {
		if (element instanceof IResource) {
			return (IResource) element;
		}
		if (element instanceof IAdaptable) {
			return (IResource) ((IAdaptable) element).getAdapter(IResource.class);
		}
		return null;
	}

}
