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
package org.lopb.plugin.labels;

// TODO: Auto-generated Javadoc
/**
 * The listener interface for receiving labelsChange events.
 * The class that is interested in processing a labelsChange
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addLabelsChangeListener<code> method. When
 * the labelsChange event occurs, that object's appropriate
 * method is invoked.
 * 
 * @see LabelsChangeEvent
 */
public interface LabelsChangeListener {
	
	/**
	 * Handle labels changed event.
	 */
	public void handleLabelsChangedEvent();
}
