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
package org.lopb.plugin.ui.views.ignoredjobsview;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.lopb.plugin.platform.jobs.JobId;
import org.lopb.plugin.platform.jobs.JobStatsManager;
import org.lopb.plugin.ui.views.LopbViewColumn;

/**
 * The Class LopbIgnoredJobsView.
 */
public class LopbIgnoredJobsView extends ViewPart {

	// logger
	/** The Constant log. */
	private static final Log log = LogFactory.getLog(LopbIgnoredJobsView.class);

	// public constants
	/** The Constant id. */
	public static final String id = "org.lopb.plugin.ui.views.ignoredjobsview.LopbIgnoredJobsView";

	/** The table viewer. */
	private TableViewer tableViewer;

	/** The table viewer column. */
	// private TableViewerColumn tableViewerColumn;
	/** The table columns. */
	private TableColumn[] tableColumns;

	/**
	 * The constructor.
	 */
	public LopbIgnoredJobsView() {
		log.debug("constructor");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets
	 *      .Composite)
	 */
	@Override
	public void createPartControl(final Composite parent) {
		log.debug("createPartControl");

		// set form layout
		parent.setLayout(new FillLayout());
		setupTableView(parent);
		parent.pack();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.ViewPart#init(org.eclipse.ui.IViewSite)
	 */
	@Override
	public void init(IViewSite site) throws PartInitException {
		log.debug("init");
		super.init(site);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		log.debug("setFocus");
		this.tableViewer.getControl().setFocus();
	}

	/**
	 * Gets the table viewer.
	 * 
	 * @return the table viewer
	 */
	public TableViewer getTableViewer() {
		return this.tableViewer;
	}

	/**
	 * Sets the up table view.
	 * 
	 * @param parent
	 *            the new up table view
	 */
	private void setupTableView(Composite parent) {
		int style = SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.HIDE_SELECTION;

		Table table = new Table(parent, style);

		this.tableViewer = new TableViewer(table);

		this.tableColumns = initTableColumns(this.tableViewer.getTable());

		// this.tableViewer.setSorter(new LopbViewerSorter(this.tableViewer,
		// this.tableColumns, LopbViewColumn
		// .getColumns()));
		// this.tableViewer.addFilter(new LopbViewerFilter());

		this.tableViewer.setContentProvider(new LopbIgnoredJobsViewContentProvider());
		this.tableViewer.setLabelProvider(new LopbIgnoredJobsViewLabelProvider());
		this.tableViewer.setInput(getViewSite());

		getViewSite().setSelectionProvider(this.tableViewer);

		initTableMenu(parent, tableViewer.getTable());
	}

	/*
	 * @see http://dev.eclipse.org/newslists/news.eclipse.platform.swt/msg11989.html
	 */
	/**
	 * Inits the table menu.
	 * 
	 * @param parent
	 *            the parent
	 * @param table
	 *            the table
	 */
	private void initTableMenu(Composite parent, final Table table) {
		final Menu pop = new Menu(parent.getShell(), SWT.POP_UP);
		final MenuItem item = new MenuItem(pop, SWT.PUSH);
		item.setText("Un-ignore job");
		item.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {

			}

			public void widgetSelected(SelectionEvent e) {
				Object o = e.getSource();

				if (o instanceof MenuItem) {
					final TableItem[] ti = table.getSelection();
					if (ti != null && ti.length > 0) {
						log.debug("selected: " + ti.length);
						final JobId jobId = (JobId) ti[0].getData();
						JobStatsManager.getInstance().unIgnoreJob(jobId);
						table.remove(table.getSelectionIndex());
					} else {
						log.debug("selected none");
					}
				}
			}
		});
		table.setMenu(pop);
	}

	/**
	 * Inits the table columns.
	 * 
	 * @param table
	 *            the table
	 * 
	 * @return the table column[]
	 */
	private TableColumn[] initTableColumns(Table table) {
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		final LopbIgnoredJobsViewColumn[] columns = LopbIgnoredJobsViewColumn.getColumns();
		final String[] columnNames = new String[columns.length];

		final TableColumn[] tableColumns = new TableColumn[columns.length];
		for (int i = 0; i < tableColumns.length; i++) {
			tableColumns[i] = new TableColumn(table, columns[i].getAlignment(), i);
			tableColumns[i].setText(columns[i].getName());
			tableColumns[i].setWidth(columns[i].getWidth());
			columnNames[i] = columns[i].getName();
		}

		this.tableViewer.setColumnProperties(columnNames);

		return tableColumns;
	}

	/**
	 * Inits the tree columns.
	 * 
	 * @param tree
	 *            the tree
	 * 
	 * @return the tree column[]
	 */
	private TreeColumn[] initTreeColumns(Tree tree) {
		final TreeColumn[] treeColumns = new TreeColumn[1];
		treeColumns[0] = new TreeColumn(tree, SWT.LEFT | SWT.FULL_SELECTION);
		treeColumns[0].setText(LopbViewColumn.LABELS_TEXT);

		Point preferredSize = tree.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		Point preferredSizeVerticalBar = tree.getVerticalBar().getSize();
		int width = preferredSize.x + preferredSizeVerticalBar.x;

		treeColumns[0].setWidth(width);

		return treeColumns;
	}

}