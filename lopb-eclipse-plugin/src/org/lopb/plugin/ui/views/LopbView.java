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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.lopb.plugin.labels.LabelsChangeListener;
import org.lopb.plugin.labels.LabelsManager;
import org.lopb.plugin.platform.jobs.JobId;
import org.lopb.plugin.platform.jobs.JobStats;
import org.lopb.plugin.platform.jobs.JobStatsAggregatedData;
import org.lopb.plugin.platform.jobs.JobStatsManager;
import org.lopb.plugin.ui.preferences.LopbPreferenceChangeManager;
import org.lopb.plugin.util.LopbTextFormatter;

// TODO: Auto-generated Javadoc
/**
 * The Class LopbView.
 */
public class LopbView extends ViewPart {

	// logger
	/** The Constant log. */
	private static final Log log = LogFactory.getLog(LopbView.class);

	// private constants
	/** The Constant CONSOLE_AUTO_FLUSH. */
	private static final boolean CONSOLE_AUTO_FLUSH = true;

	/** The Constant MENU_DELETE_LABEL. */
	private static final String MENU_DELETE_LABEL = "Delete Label";

	/** The Constant MENU_ADD_NEW_LABEL. */
	private static final String MENU_ADD_NEW_LABEL = "New Label";

	/** The Constant MENU_SHOW_JOBS. */
	private static final String MENU_SHOW_JOBS = "Show Jobs";

	/** The Constant MENU_SHOW_JOBS_ANY. */
	private static final String MENU_SHOW_JOBS_ANY = "With Any Selected Labels";

	/** The Constant MENU_SHOW_JOBS_ALL. */
	private static final String MENU_SHOW_JOBS_ALL = "With All Selected Labels";

	/** The Constant NEW_LABEL_TEXT. */
	private static final String NEW_LABEL_TEXT = "<New Label>";

	/** The Constant UPDATE_INTERVAL. */
	private static final int UPDATE_INTERVAL = 1000;

	// public constants
	/** The Constant id. */
	public static final String id = "org.lopb.plugin.ui.views.LopbView";

	/** The table viewer. */
	private TableViewer tableViewer;

	/** The table viewer column. */
	// private TableViewerColumn tableViewerColumn;
	/** The table columns. */
	private TableColumn[] tableColumns;

	// splitter
	/** The sash. */
	private Sash sash;

	// tree viewer
	/** The tree viewer. */
	private CheckboxTreeViewer treeViewer;

	/** The lopb status label. */
	private Label lopbStatusLabel;

	/**
	 * The constructor.
	 */
	public LopbView() {
		log.debug("constructor");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets .Composite)
	 */
	@Override
	public void createPartControl(final Composite parent) {
		log.debug("createPartControl");

		// set form layout
		parent.setLayout(new FormLayout());

		// create sash object here - when we will set layout data for label
		// viewer, we will attach sash to its right and then later, to left of
		// table viewer.
		sash = new Sash(parent, SWT.VERTICAL);

		// setup views

		// hide status label for this first release
		// setupLopbStatusLabel(parent);

		setupTreeViewer(parent);
		setupSplitter(parent);
		setupTableView(parent);

		parent.pack();
	}

	/**
	 * Disable job id editor.
	 */
	public void disableJobIdEditor() {
		final CellEditor[] editors = this.tableViewer.getCellEditors();
		if (editors != null) {
			editors[LopbViewColumn.JOB_ID_COLUMN_INDEX] = null;
		}
	}

	/**
	 * Enable job id editor.
	 */
	public void enableJobIdEditor() {
		final CellEditor[] editors = this.tableViewer.getCellEditors();
		if (editors != null) {
			editors[LopbViewColumn.JOB_ID_COLUMN_INDEX] = new TextCellEditor(tableViewer.getTable());
		}
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

	// private methods

	/**
	 * Sets the up lopb status label.
	 * 
	 * @param parent
	 *            the new up lopb status label
	 */
	private void setupLopbStatusLabel(final Composite parent) {
		this.lopbStatusLabel = new Label(parent, SWT.LEFT);

		final FormData formData = new FormData();
		formData.left = new FormAttachment(0, 4);
		formData.right = new FormAttachment(100, 0);
		formData.top = new FormAttachment(0, 4);
		formData.bottom = new FormAttachment(0, 20);
		lopbStatusLabel.setLayoutData(formData);

		/** The timer. */
		parent.getDisplay().asyncExec(new Runnable() {
			public void run() {
				if (!parent.isDisposed()) {
					updateStats();
					parent.getDisplay().timerExec(LopbView.UPDATE_INTERVAL, this);
				}
			}
		});
	}

	/**
	 * Sets the up tree viewer.
	 * 
	 * @param parent
	 *            the new up tree viewer
	 */
	private void setupTreeViewer(final Composite parent) {
		final int style = SWT.SINGLE | SWT.BORDER | SWT.CHECK | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION
				| SWT.HIDE_SELECTION;

		final Tree tree = new Tree(parent, style);
		tree.setHeaderVisible(true);

		this.treeViewer = new CheckboxTreeViewer(tree);
		this.treeViewer.setColumnProperties(new String[] { LopbViewColumn.LABELS_TEXT });

		this.treeViewer.setContentProvider(new TreeViewContentProvider());
		this.treeViewer.setLabelProvider(new TreeViewLabelProvider());

		// pass a non-null that will be ignored
		this.treeViewer.setInput("root");

		LabelsManager.getInstance().addLabelsChangeListener(new LabelsChangeListener() {

			public void handleLabelsChangedEvent() {
				if (tree.isDisposed()) {
					LabelsManager.getInstance().removeLabelsChangeListener(this);
				} else {
					refreshTreeView();
					tableViewer.refresh();
				}
			}
		});

		// When user checks a checkbox in the tree, include jobs with this label
		// in table view
		this.treeViewer.addCheckStateListener(new ICheckStateListener() {
			public void checkStateChanged(CheckStateChangedEvent event) {
				if (event.getChecked()) {
					LabelsManager.getInstance().includeLabel((String) event.getElement());
				} else {
					LabelsManager.getInstance().excludeLabel((String) event.getElement());
				}
			}
		});

		final TreeColumn[] treeColumns = initTreeColumns(this.treeViewer.getTree());
		CellEditorHelper.setTreeViewColumnEditors(treeViewer);

		createTreeViewMenu(parent, tree);

		final FormData formData = new FormData();
		formData.left = new FormAttachment(0, 0);
		formData.right = new FormAttachment(sash, 0);
		formData.top = new FormAttachment(lopbStatusLabel, 0);
		formData.bottom = new FormAttachment(100, 0);
		tree.setLayoutData(formData);

		refreshTreeView();
	}

	/**
	 * Sets the up splitter.
	 * 
	 * @param parent
	 *            the new up splitter
	 */
	private void setupSplitter(final Composite parent) {
		final int limit = 20, percent = 20;
		final FormData formData = new FormData();

		formData.left = new FormAttachment(percent, 0);
		formData.top = new FormAttachment(lopbStatusLabel, 0);
		formData.bottom = new FormAttachment(100, 0);

		sash.setLayoutData(formData);
		sash.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				final Rectangle sashRect = sash.getBounds();
				final Rectangle shellRect = parent.getClientArea();
				final int right = shellRect.width - sashRect.width - limit;
				e.x = Math.max(Math.min(e.x, right), limit);
				if (e.x != sashRect.x) {
					formData.left = new FormAttachment(0, e.x);
					parent.layout();
				}
			}
		});
	}

	/**
	 * Sets the up table view.
	 * 
	 * @param parent
	 *            the new up table view
	 */
	private void setupTableView(Composite parent) {
		final int style = SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION
				| SWT.HIDE_SELECTION;

		final Table table = new Table(parent, style);

		this.tableViewer = new TableViewer(table);

		this.tableColumns = initTableColumns(this.tableViewer.getTable());
		CellEditorHelper.setTableViewColumnEditors(this.tableViewer);

		this.tableViewer.setSorter(new LopbViewerSorter(this.tableViewer, this.tableColumns, LopbViewColumn
				.getColumns()));
		this.tableViewer.addFilter(new LopbViewerFilter());

		this.tableViewer.setContentProvider(new LopbViewContentProvider());
		this.tableViewer.setLabelProvider(new LopbViewLabelProvider());
		this.tableViewer.setInput(getViewSite());

		if (!LopbPreferenceChangeManager.getInstance().isJobRenamingEnabled()) {
			final CellEditor[] editors = this.tableViewer.getCellEditors();
			editors[LopbViewColumn.JOB_ID_COLUMN_INDEX] = null;
		}

		getViewSite().setSelectionProvider(this.tableViewer);

		final FormData formData = new FormData();
		formData.left = new FormAttachment(sash, 0);
		formData.right = new FormAttachment(100, 0);
		formData.top = new FormAttachment(lopbStatusLabel, 0);
		formData.bottom = new FormAttachment(100, 0);
		tableViewer.getControl().setLayoutData(formData);

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
		item.setText("Ignore job");
		item.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {

			}

			public void widgetSelected(SelectionEvent e) {
				final Object o = e.getSource();
				if (o instanceof MenuItem) {
					ignoreJob((MenuItem) o);
				}
			}

			private void ignoreJob(MenuItem item) {
				final TableItem[] ti = table.getSelection();
				if (ti != null && ti.length > 0) {
					log.debug("selected: " + ti.length);
					final JobId jobId = ((JobStats) ti[0].getData()).getJobId();
					JobStatsManager.getInstance().ignoreJob(jobId);
					table.remove(table.getSelectionIndex());
				} else {
					log.debug("selected none");
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

		final LopbViewColumn[] lopbViewColumns = LopbViewColumn.getColumns();
		final String[] lopbViewColumnNames = new String[lopbViewColumns.length];

		final TableColumn[] tableColumns = new TableColumn[lopbViewColumns.length];
		for (int i = 0; i < tableColumns.length; i++) {
			tableColumns[i] = new TableColumn(table, lopbViewColumns[i].getAlignment(), i);
			tableColumns[i].setText(lopbViewColumns[i].getName());
			tableColumns[i].setWidth(lopbViewColumns[i].getWidth());

			lopbViewColumnNames[i] = lopbViewColumns[i].getName();
		}

		this.tableViewer.setColumnProperties(lopbViewColumnNames);

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

		final Point preferredSize = tree.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		final Point preferredSizeVerticalBar = tree.getVerticalBar().getSize();
		final int width = preferredSize.x + preferredSizeVerticalBar.x;

		treeColumns[0].setWidth(width);

		return treeColumns;
	}

	/**
	 * Creates the tree view menu.
	 * 
	 * @param parent
	 *            the parent
	 * @param tree
	 *            the tree
	 */
	private void createTreeViewMenu(final Composite parent, final Tree tree) {
		final Menu treeMenu = new Menu(parent.getShell(), SWT.POP_UP);

		// add new label menu item
		final MenuItem addLabelMenuItem = new MenuItem(treeMenu, SWT.NONE);
		addLabelMenuItem.setText(LopbView.MENU_ADD_NEW_LABEL);

		addLabelMenuItem.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
				processWidgetSelected();
			}

			public void widgetSelected(SelectionEvent e) {
				processWidgetSelected();
			}

			private void processWidgetSelected() {
				LabelsManager.getInstance().addNewLabel(LopbView.NEW_LABEL_TEXT);
			}
		});

		new MenuItem(treeMenu, SWT.SEPARATOR);

		// delete label menu item
		final MenuItem deleteLabelMenuItem = new MenuItem(treeMenu, SWT.NONE);
		deleteLabelMenuItem.setText(LopbView.MENU_DELETE_LABEL);

		deleteLabelMenuItem.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
				processWidgetSelected();
			}

			public void widgetSelected(SelectionEvent e) {
				processWidgetSelected();
			}

			private void processWidgetSelected() {
				final TreeItem[] treeItems = tree.getSelection();
				if (treeItems.length > 0) {
					final String label = treeItems[0].getText();
					LabelsManager.getInstance().removeLabel(label);
				}
			}
		});

		new MenuItem(treeMenu, SWT.SEPARATOR);

		// show menu
		final MenuItem showJobsMenuItem = new MenuItem(treeMenu, SWT.CASCADE);
		showJobsMenuItem.setText(LopbView.MENU_SHOW_JOBS);

		final Menu showJobsMenuPopup = new Menu(treeMenu);
		showJobsMenuItem.setMenu(showJobsMenuPopup);

		final MenuItem showAnyJobsMenuItem = new MenuItem(showJobsMenuPopup, SWT.CHECK);
		showAnyJobsMenuItem.setText(LopbView.MENU_SHOW_JOBS_ANY);
		showAnyJobsMenuItem.setSelection(LabelsManager.getInstance().isShowAnyLabels());

		final MenuItem showAllJobsMenuItem = new MenuItem(showJobsMenuPopup, SWT.CHECK);
		showAllJobsMenuItem.setText(LopbView.MENU_SHOW_JOBS_ALL);
		showAllJobsMenuItem.setSelection(!LabelsManager.getInstance().isShowAnyLabels());

		showAnyJobsMenuItem.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
				processWidgetSelected();
			}

			public void widgetSelected(SelectionEvent e) {
				processWidgetSelected();
			}

			private void processWidgetSelected() {
				LabelsManager.getInstance().setShowAnyLabels(true);
				showAnyJobsMenuItem.setSelection(LabelsManager.getInstance().isShowAnyLabels());
				showAllJobsMenuItem.setSelection(!LabelsManager.getInstance().isShowAnyLabels());
				tableViewer.refresh();
			}
		});

		showAllJobsMenuItem.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
				processWidgetSelected();
			}

			public void widgetSelected(SelectionEvent e) {
				processWidgetSelected();
			}

			private void processWidgetSelected() {
				LabelsManager.getInstance().setShowAnyLabels(false);
				showAnyJobsMenuItem.setSelection(LabelsManager.getInstance().isShowAnyLabels());
				showAllJobsMenuItem.setSelection(!LabelsManager.getInstance().isShowAnyLabels());
				tableViewer.refresh();
			}
		});

		new MenuItem(treeMenu, SWT.SEPARATOR);

		tree.addListener(SWT.MenuDetect, new Listener() {
			public void handleEvent(Event event) {
				final TreeItem[] treeItems = tree.getSelection();
				deleteLabelMenuItem.setEnabled((treeItems.length > 0));

				tree.setMenu(treeMenu);
			}
		});

		/*
		 * IMPORTANT: Dispose the menus (only the current menu, set with setMenu(), will be automatically disposed)
		 */
		tree.addListener(SWT.Dispose, new Listener() {
			public void handleEvent(Event event) {
				treeMenu.dispose();
			}
		});
	}

	/**
	 * Refresh tree view.
	 */
	private void refreshTreeView() {
		treeViewer.refresh();

		final Tree tree = this.treeViewer.getTree();

		// set checked state for included labels
		final TreeItem[] treeItems = tree.getItems();
		for (final TreeItem treeItem : treeItems) {
			final boolean isIncluded = LabelsManager.getInstance().isLabelIncluded(treeItem.getText());
			treeItem.setChecked(isIncluded);
		}

		// select first item in tree
		if (treeItems.length > 0) {
			final TreeItem[] selectedTreeItems = tree.getSelection();

			// make sure we select first item only when no other item was
			// already selected before
			if (selectedTreeItems.length == 0) {
				tree.setSelection(treeItems[0]);
			}
		}

	}

	/**
	 * Update stats.
	 */
	private void updateStats() {
		final JobStatsAggregatedData jobStatsAggregatedData = JobStatsManager.getInstance().getJobStatsAggregatedData();
		this.lopbStatusLabel.setText(LopbTextFormatter.getInstance().formatHeaderText(jobStatsAggregatedData));
	}
}