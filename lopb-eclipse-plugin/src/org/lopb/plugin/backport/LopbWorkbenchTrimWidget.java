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

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.menus.AbstractWorkbenchTrimWidget;
import org.lopb.plugin.ui.contributions.trim.LopbStatus;
import org.lopb.plugin.ui.preferences.LopbPreferenceChangeManager;

// TODO: Auto-generated Javadoc
/**
 * The Class LopbWorkbenchTrimWidget.
 * 
 * @see http://help.eclipse.org/help32/index.jsp?topic=/org.eclipse.platform.doc.user/whatsNew/platform_whatsnew.html
 * @author alex
 */
public class LopbWorkbenchTrimWidget extends AbstractWorkbenchTrimWidget {

	/** The composite. */
	private LopbStatus lopbStatus;
	private static LopbWorkbenchTrimWidget instance;

	public LopbWorkbenchTrimWidget() {
		instance = this;
	}

	public static LopbWorkbenchTrimWidget getInstance() {
		return instance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.menus.AbstractWorkbenchTrimWidget#init(org.eclipse.ui.IWorkbenchWindow)
	 */
	public void init(IWorkbenchWindow workbenchWindow) {
		super.init(workbenchWindow);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.menus.AbstractTrimWidget#dispose()
	 */
	public void dispose() {
		if (lopbStatus != null && !lopbStatus.isDisposed()) {
			lopbStatus.dispose();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.menus.AbstractTrimWidget#fill(org.eclipse.swt.widgets.Composite,
	 *      int, int)
	 */
	public void fill(Composite parent, int oldSide, int newSide) {		
		final boolean enabled = LopbPreferenceChangeManager.getInstance().isLopbStatusEnabled();
		this.lopbStatus = new LopbStatus(getWorkbenchWindow(), parent, enabled);
	}

	public void setVisible(final boolean b) {
		if (lopbStatus != null && !lopbStatus.isDisposed()) {
			if (b) {
				lopbStatus.show();
			} else {
				lopbStatus.hide();
			}			
			lopbStatus.getParent().redraw();
		}
	}
}
