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

import java.util.Comparator;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.TableColumn;
import org.lopb.plugin.platform.jobs.JobStats;

// TODO: Auto-generated Javadoc
/**
 * The Class LopbViewerSorter.
 */
public class LopbViewerSorter extends ViewerSorter {

	/**
	 * The Class SortInfo.
	 */
	private class SortInfo {
		
		/** The column index. */
		int columnIndex;
		
		/** The comparator. */
		Comparator<JobStats> comparator;
		
		/** The descending. */
		boolean descending;
	}

	/** The viewer. */
	private final TableViewer viewer;
	
	/** The sort infos. */
	private final SortInfo[] sortInfos;

	/**
	 * Instantiates a new lopb viewer sorter.
	 * 
	 * @param viewer the viewer
	 * @param tableColumns the table columns
	 * @param lopbViewColumns the lopb view columns
	 */
	public LopbViewerSorter(final TableViewer viewer, final TableColumn[] tableColumns,
			final LopbViewColumn[] lopbViewColumns) {
		this.viewer = viewer;

		this.sortInfos = new SortInfo[tableColumns.length];
		for (int i = 0; i < lopbViewColumns.length; i++) {
			sortInfos[i] = new SortInfo();
			sortInfos[i].columnIndex = i;
			sortInfos[i].comparator = lopbViewColumns[i].getComparator();
			sortInfos[i].descending = false;
			addSelectionListener(tableColumns[i], sortInfos[i]);
		}
	}

	/**
	 * Adds the selection listener.
	 * 
	 * @param tableColumn the table column
	 * @param sortInfo the sort info
	 */
	private void addSelectionListener(final TableColumn tableColumn, final SortInfo sortInfo) {
		tableColumn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				sortUsing(sortInfo);
			}
		});
	}

	/**
	 * Sort using.
	 * 
	 * @param sortInfo the sort info
	 */
	private void sortUsing(final SortInfo sortInfo) {
		if (sortInfo == sortInfos[0]) {
			sortInfo.descending = !sortInfo.descending;
		} else {
			for (int i = 0; i < sortInfos.length; i++) {
				if (sortInfo == sortInfos[i]) {
					System.arraycopy(sortInfos, 0, sortInfos, 1, i);
					sortInfos[0] = sortInfo;
					sortInfo.descending = false;
					break;
				}
			}
		}
		viewer.refresh();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ViewerComparator#compare(org.eclipse.jface.viewers.Viewer,
	 *      java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(Viewer viewer, Object row1, Object row2) {
		for (int i = 0; i < sortInfos.length; i++) {
			final int result = sortInfos[i].comparator.compare((JobStats) row1, (JobStats) row2);
			if (result != 0) {
				if (sortInfos[i].descending) {
					return -result;
				} else {
					return result;
				}
			}
		}
		return 0;
	}

}
