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
package org.lopb.plugin.ui.contributions.trim;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.lopb.plugin.backport.HandlerUtil;
import org.lopb.plugin.backport.WorkbenchWindowControlContribution;

// TODO: Auto-generated Javadoc
/**
 * The Class LopbWorkbenchWindowControlContribution.
 */
public class LopbWorkbenchWindowControlContribution extends
		WorkbenchWindowControlContribution {

	/** The Constant log. */
	private static final Log log = LogFactory
			.getLog(LopbWorkbenchWindowControlContribution.class);

	/** The lopb status. */
	private LopbStatus lopbStatus;

	/**
	 * Instantiates a new lopb workbench window control contribution.
	 * 
	 * @param arg the arg
	 */
	public LopbWorkbenchWindowControlContribution(String arg) {
		super(arg);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.action.ControlContribution#createControl(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createControl(Composite parent) {
		log.debug("createControl");
		if (lopbStatus == null) {
			lopbStatus = new LopbStatus(HandlerUtil.getActiveWorkbenchWindow(),
					parent, true);
		}
		return lopbStatus;
	}
}
