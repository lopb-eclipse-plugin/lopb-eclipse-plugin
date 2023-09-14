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

import org.eclipse.jface.action.ControlContribution;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbenchWindow;

// TODO: Auto-generated Javadoc
/**
 * Add workbench specific information to a standard control contribution. Allows
 * the client derivatives to access the IWorkbenchWindow hosting the control as
 * well as the side of the workbench that the control is currently being
 * displayed on.
 * 
 * @since 3.3
 */
public abstract class InternalControlContribution extends ControlContribution {
	
	/** The wbw. */
	private IWorkbenchWindow wbw;
	
	/** The cur side. */
	private int curSide;

	/**
	 * The Constructor.
	 * 
	 * @param id the id
	 */
	protected InternalControlContribution(String id) {
		super(id);
	}

	/**
	 * Instantiates a new internal control contribution.
	 */
	public InternalControlContribution() {
		this("unknown ID"); //$NON-NLS-1$
	}

	/**
	 * Gets the workbench window.
	 * 
	 * @return Returns the wbw.
	 */
	public IWorkbenchWindow getWorkbenchWindow() {
		return wbw;
	}

	/**
	 * Sets the workbench window.
	 * 
	 * @param wbw The wbw to set.
	 */
	/* package */void setWorkbenchWindow(IWorkbenchWindow wbw) {
		this.wbw = wbw;
	}

	/**
	 * Gets the cur side.
	 * 
	 * @return Returns the curSide.
	 */
	public int getCurSide() {
		return curSide;
	}

	/**
	 * Sets the cur side.
	 * 
	 * @param curSide The curSide to set.
	 */
	/* package */void setCurSide(int curSide) {
		this.curSide = curSide;
	}

	/**
	 * Gets the orientation.
	 * 
	 * @return the orientation
	 */
	public int getOrientation() {
		if (getCurSide() == SWT.LEFT || getCurSide() == SWT.RIGHT)
			return SWT.VERTICAL;

		return SWT.HORIZONTAL;
	}
}
