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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.TreeItem;
import org.lopb.plugin.aliases.JobIdAlias;
import org.lopb.plugin.aliases.JobIdAliasManager;
import org.lopb.plugin.labels.LabelsManager;
import org.lopb.plugin.platform.jobs.JobId;
import org.lopb.plugin.platform.jobs.JobStats;


// TODO: Auto-generated Javadoc
/**
 * A Helper for creating CellEditor objects.
 * 
 * @see http://www.eclipse.org/articles/Article-Table-viewer/table_viewer.html
 */
public class CellEditorHelper {

	/** The Constant log. */
	private static final Log log = LogFactory.getLog(CellEditorHelper.class);

	/**
	 * Sets the table view column editors.
	 * 
	 * @param tableViewer the new table view column editors
	 */
	public static void setTableViewColumnEditors(TableViewer tableViewer) {

		// Create the cell editors
		CellEditor[] editors = new CellEditor[LopbViewColumn.getColumns().length];

		editors[LopbViewColumn.JOB_ID_COLUMN_INDEX] = new TextCellEditor(
				tableViewer.getTable());
		editors[LopbViewColumn.LABELS_COLUMN_INDEX] = new TextCellEditor(
				tableViewer.getTable());

		// Assign the cell editors to the viewer
		tableViewer.setCellEditors(editors);

		// Set the cell modifier for the viewer
		tableViewer.setCellModifier(new TableViewerCellModifier(tableViewer));
	}

	/**
	 * Sets the tree view column editors.
	 * 
	 * @param treeViewer the new tree view column editors
	 */
	public static void setTreeViewColumnEditors(TreeViewer treeViewer) {

		// Create the cell editors
		CellEditor[] editors = new CellEditor[1];

		TextCellEditor textCellEditor = new TextCellEditor(treeViewer.getTree());
		editors[0] = textCellEditor;

		// Assign the cell editors to the viewer
		treeViewer.setCellEditors(editors);

		// Set the cell modifier for the viewer
		treeViewer.setCellModifier(new TreeViewerCellModifier(treeViewer));
	}

	/**
	 * The Class TableViewerCellModifier.
	 */
	static private class TableViewerCellModifier implements ICellModifier {

		/** The table viewer. */
		private TableViewer tableViewer;

		/**
		 * Instantiates a new table viewer cell modifier.
		 * 
		 * @param tableViewer the table viewer
		 */
		public TableViewerCellModifier(TableViewer tableViewer) {
			super();
			this.tableViewer = tableViewer;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.ICellModifier#canModify(java.lang.Object,
		 *      java.lang.String)
		 */
		public boolean canModify(Object element, String property) {
			return true;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.ICellModifier#getValue(java.lang.Object,
		 *      java.lang.String)
		 */
		public Object getValue(Object element, String property) {
			final LopbViewColumn[] lopbViewColumns = LopbViewColumn
					.getColumns();

			final String jobIdColumnName = lopbViewColumns[LopbViewColumn.JOB_ID_COLUMN_INDEX]
					.getName();
			final String labelColumnName = lopbViewColumns[LopbViewColumn.LABELS_COLUMN_INDEX]
					.getName();

			if (jobIdColumnName.equalsIgnoreCase(property)) {
				final JobId jobId = ((JobStats) element).getJobId();
				return JobIdAliasManager.getInstance().getJobIdAliasAsString(
						jobId);
			} else if (labelColumnName.equalsIgnoreCase(property)) {
				return LabelsManager.getInstance().getLabelsAsString(
						((JobStats) element).getJobId());
			}

			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.ICellModifier#modify(java.lang.Object,
		 *      java.lang.String, java.lang.Object)
		 */
		public void modify(Object element, String property, Object value) {
			final LopbViewColumn[] lopbViewColumns = LopbViewColumn
					.getColumns();

			final String jobIdColumnName = lopbViewColumns[LopbViewColumn.JOB_ID_COLUMN_INDEX]
					.getName();
			final String labelColumnName = lopbViewColumns[LopbViewColumn.LABELS_COLUMN_INDEX]
					.getName();

			if (jobIdColumnName.equalsIgnoreCase(property)) {
				if (value != null) {
					final JobStats jobStats = (JobStats) ((TableItem) element)
							.getData();
					final String jobIdAliasAsString = (String) value;

					if (jobIdAliasAsString.length() > 0) {
						log.debug("changing alias to: " + value);
						final JobId jobId = jobStats.getJobId();
						final JobIdAlias jobIdAlias = JobIdAlias
								.createJobIdAlias(jobIdAliasAsString);
						JobIdAliasManager.getInstance().setJobIdAlias(jobId,
								jobIdAlias);
						tableViewer.refresh();
					} else {
						log.debug("restoring alias to jobId " + value);
						final JobId jobId = jobStats.getJobId();
						JobIdAliasManager.getInstance().removeJobIdAlias(jobId);
						tableViewer.refresh();
					}
				}
			} else if (labelColumnName.equalsIgnoreCase(property)) {
				if (value == null) {
					value = "";
				}

				final JobStats jobStats = (JobStats) ((TableItem) element)
						.getData();
				LabelsManager.getInstance().setLabelsToJob((String) value,
						jobStats.getJobId());
				tableViewer.refresh();
			}
		}
	}

	/**
	 * The Class TreeViewerCellModifier.
	 */
	static private class TreeViewerCellModifier implements ICellModifier {

		/** The tree viewer. */
		private TreeViewer treeViewer;

		/**
		 * Instantiates a new tree viewer cell modifier.
		 * 
		 * @param treeViewer the tree viewer
		 */
		public TreeViewerCellModifier(TreeViewer treeViewer) {
			super();

			this.treeViewer = treeViewer;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.ICellModifier#canModify(java.lang.Object,
		 *      java.lang.String)
		 */
		public boolean canModify(Object element, String property) {
			return true;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.ICellModifier#getValue(java.lang.Object,
		 *      java.lang.String)
		 */
		public Object getValue(Object element, String property) {
			return element.toString();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.ICellModifier#modify(java.lang.Object,
		 *      java.lang.String, java.lang.Object)
		 */
		public void modify(Object element, String property, Object value) {
			String oldValue = (String) ((TreeItem) element).getData();

			if (!StringUtils.isEmpty((String) value)) {
				LabelsManager.getInstance().renameLabel(oldValue,
						(String) value);
			}

			treeViewer.refresh();
		}
	}
}
