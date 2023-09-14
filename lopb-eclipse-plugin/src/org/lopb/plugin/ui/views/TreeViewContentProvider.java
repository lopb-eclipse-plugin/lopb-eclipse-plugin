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

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.lopb.plugin.labels.LabelsManager;

// TODO: Auto-generated Javadoc
/**
 * The Class TreeViewContentProvider.
 */
public class TreeViewContentProvider implements ITreeContentProvider {

	/**
	 * Gets the children of the specified object.
	 * 
	 * @param parent the parent object
	 * 
	 * @return Object[]
	 */
	public Object[] getChildren(Object parent) {
		// Return the files and subdirectories in this directory
		return new Object[0];
	}

	/**
	 * Gets the parent of the specified object.
	 * 
	 * @param child the child
	 * 
	 * @return Object
	 */
	public Object getParent(Object child) {
		// Return this file's parent file
		return null;
	}

	/**
	 * Returns whether the passed object has children.
	 * 
	 * @param parent the parent
	 * 
	 * @return boolean
	 */
	public boolean hasChildren(Object parent) {
		// Return whether the parent has children
		return false;
	}

	/**
	 * Gets the root element(s) of the tree.
	 * 
	 * @param arg0 the input data
	 * 
	 * @return Object[]
	 */
	public Object[] getElements(Object arg0) {
		// These are the root elements of the tree
		// We don't care what arg0 is, because we just want all
		// the root nodes in the file system
		return LabelsManager.getInstance().getAllLabels().toArray();
	}

	/**
	 * Disposes any created resources.
	 */
	public void dispose() {
		// Nothing to dispose
	}

	/**
	 * Called when the input changes.
	 * 
	 * @param arg1 the old input
	 * @param arg2 the new input
	 * @param viewer the viewer
	 */
	public void inputChanged(Viewer viewer, Object arg1, Object arg2) {
		// do nothing
	}
}
