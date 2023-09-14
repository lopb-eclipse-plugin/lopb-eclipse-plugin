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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

// TODO: Auto-generated Javadoc
/**
 * The Class TreeViewLabelProvider.
 */
public class TreeViewLabelProvider implements ILabelProvider {

	// The listeners
	/** The listeners. */
	private List listeners;

	/**
	 * Constructs a FileTreeLabelProvider.
	 */
	public TreeViewLabelProvider() {
		// Create the list to hold the listeners
		listeners = new ArrayList();
	}

	/**
	 * Gets the image to display for a node in the tree.
	 * 
	 * @param arg0 the node
	 * 
	 * @return Image
	 */
	public Image getImage(Object arg0) {
		return null;
	}

	/**
	 * Gets the text to display for a node in the tree.
	 * 
	 * @param arg0 the node
	 * 
	 * @return String
	 */
	public String getText(Object arg0) {
		// Check the case settings before returning the text
		return (String) arg0;
	}

	/**
	 * Adds a listener to this label provider.
	 * 
	 * @param arg0 the listener
	 */
	public void addListener(ILabelProviderListener arg0) {
		listeners.add(arg0);
	}

	/**
	 * Called when this LabelProvider is being disposed.
	 */
	public void dispose() {
	}

	/**
	 * Returns whether changes to the specified property on the specified
	 * element would affect the label for the element.
	 * 
	 * @param arg0 the element
	 * @param arg1 the property
	 * 
	 * @return boolean
	 */
	public boolean isLabelProperty(Object arg0, String arg1) {
		return false;
	}

	/**
	 * Removes the listener.
	 * 
	 * @param arg0 the listener to remove
	 */
	public void removeListener(ILabelProviderListener arg0) {
		listeners.remove(arg0);
	}
}
