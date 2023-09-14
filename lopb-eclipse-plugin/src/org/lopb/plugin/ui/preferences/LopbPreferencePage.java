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
package org.lopb.plugin.ui.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.lopb.plugin.LopbPlugin;

// TODO: Auto-generated Javadoc
/**
 * The Class LopbPreferencePage.
 */
public class LopbPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	/**
	 * Instantiates a new lopb preference page.
	 */
	public LopbPreferencePage() {
		super(GRID);
		setPreferenceStore(LopbPlugin.getDefault().getPreferenceStore());
		setDescription("Preferences for the Lack of Progress Bar plugin.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#createFieldEditors()
	 */
	public void createFieldEditors() {
		addField(new BooleanFieldEditor(LopbPreferenceConstants.LOPB_JOBS_INCLUDE_SYSTEM_JOBS, "Include system jobs",
				getFieldEditorParent()));
		addField(new BooleanFieldEditor(LopbPreferenceConstants.LOPB_ENABLE_JOB_RENAMING, "Enable job renaming",
				getFieldEditorParent()));
		addField(new IntegerFieldEditor(LopbPreferenceConstants.LOPB_EXPORT_SNAPSHOT_PERIOD,
				"Save job stats periodically (interval in minutes; 0 to disable)", getFieldEditorParent(), 3));
		addField(new DirectoryFieldEditor(LopbPreferenceConstants.LOPB_CSV_OUTPUT_BASE_DIR,
				"Output directory for exporting CSV files", getFieldEditorParent()));
		addField(new BooleanFieldEditor(LopbPreferenceConstants.LOPB_SHOW_STATUS,
				"Show Lopb status (Caveat: this control is still experimental!)", getFieldEditorParent()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}

}