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

import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.lopb.plugin.LopbPlugin;
import org.lopb.plugin.platform.jobs.JobStatsAggregatedData;
import org.lopb.plugin.platform.jobs.JobStatsManager;
import org.lopb.plugin.ui.views.LopbView;
import org.lopb.plugin.util.LopbTextFormatter;

// TODO: Auto-generated Javadoc
/**
 * The Class LopbStatus.
 */
public class LopbStatus extends Composite {

	/** The Constant log. */
	private static final Log log = LogFactory.getLog(LopbStatus.class);

	/** The update interval. */
	private final int updateInterval = 1000;

	/** The LOP b_ statu s_ hig h_ lo w_ threshold. */
	private final float LOPB_STATUS_HIGH_LOW_THRESHOLD = 0.3f;

	/** The gc image. */
	private final Image gcImage;

	/** The text col. */
	private final Color bgCol;

	/** The time waited low col. */
	private final Color timeWaitedLowCol;

	/** The time waited high col. */
	private final Color timeWaitedHighCol;

	/** The top left col. */
	private final Color topLeftCol;

	/** The bottom right col. */
	private final Color bottomRightCol;

	/** The sep col. */
	private final Color sepCol;

	/** The text col. */
	private final Color textCol;

	/** The canvas. */
	private final Canvas canvas;

	/** The img bounds. */
	private final Rectangle imgBounds;

	/** The window. */
	private final IWorkbenchWindow window;

	/** The status text. */
	private String statusText;

	/** The fraction of session time. */
	private float fractionOfSessionTime;

	/** The timer. */
	private final Runnable timer = new Runnable() {
		public void run() {
			if (!isDisposed()) {
				updateStats();
				getDisplay().timerExec(updateInterval, this);
			}
		}
	};

	/**
	 * Instantiates a new lopb status.
	 * 
	 * @param window
	 *            the window
	 * @param parent
	 *            the parent
	 */
	public LopbStatus(final IWorkbenchWindow window, final Composite parent, final boolean isVisible) {
		super(parent, SWT.NONE);
		this.window = window;
		this.canvas = new Canvas(this, SWT.NONE);

		final URL url = FileLocator.find(LopbPlugin.getDefault().getBundle(), new Path("icons/lopb16.gif"), null);
		ImageDescriptor imageDesc = ImageDescriptor.createFromURL(url);
		this.gcImage = imageDesc.createImage();
		if (gcImage != null) {
			this.imgBounds = gcImage.getBounds();
		} else {
			this.imgBounds = new Rectangle(0, 0, 12, 12);
		}

		// color setup
		final Display display = getDisplay();
		timeWaitedLowCol = new Color(display, 255, 190, 125); // light orange,
		// needs to be
		// dispose when
		// dispose() is
		// called
		timeWaitedHighCol = new Color(display, 255, 70, 70); // medium red,
		// needs to be
		// dispose when
		// dispose() is
		// called
		bgCol = display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);
		sepCol = topLeftCol = display.getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW);
		bottomRightCol = display.getSystemColor(SWT.COLOR_WIDGET_HIGHLIGHT_SHADOW);
		textCol = display.getSystemColor(SWT.COLOR_INFO_FOREGROUND);

		createContextMenu();

		final Listener listener = new Listener() {

			public void handleEvent(Event event) {
				switch (event.type) {
				case SWT.Dispose:
					doDispose();
					break;
				case SWT.Resize:
					Rectangle rect = getClientArea();
					canvas.setBounds(rect.width - imgBounds.width - 1, 1, imgBounds.width, rect.height - 2);
					break;
				case SWT.Paint:
					if (event.widget == LopbStatus.this) {
						paintComposite(event.gc);
					} else if (event.widget == canvas) {
						paintIcon(event.gc);
					}
					break;
				// case SWT.MouseEnter:
				// LopbStatus.this.updateTooltip = true;
				// updateToolTip();
				// break;
				// case SWT.MouseExit:
				// if (event.widget == LopbStatus.this) {
				// LopbStatus.this.updateTooltip = false;
				// }
				// break;
				}
			}

		};
		addListener(SWT.Dispose, listener);
		addListener(SWT.Paint, listener);
		addListener(SWT.Resize, listener);
		canvas.addListener(SWT.Dispose, listener);
		canvas.addListener(SWT.Paint, listener);
		canvas.addListener(SWT.Resize, listener);

		updateStats();

		display.asyncExec(new Runnable() {
			public void run() {
				if (!isDisposed()) {
					getDisplay().timerExec(updateInterval, timer);
				}
			}
		});

		if (isVisible) {
			show();
		} else {			
			hide();
		}
		
		parent.setToolTipText(null);
	}

	/**
	 * Do dispose.
	 */
	private void doDispose() {
		if (gcImage != null) {
			gcImage.dispose();
		}
		if (timeWaitedLowCol != null) {
			timeWaitedLowCol.dispose();
		}
		if (timeWaitedHighCol != null) {
			timeWaitedHighCol.dispose();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.swt.widgets.Composite#computeSize(int, int, boolean)
	 */
	public Point computeSize(int wHint, int hHint, boolean changed) {
		final GC gc = new GC(this);
		final Point p = gc.textExtent("000h 000m 000s (00%)");
		int height = imgBounds.height;
		// choose the largest of
		// - Text height + margins
		// - Image height + margins
		// - Default Trim heightin
		height = Math.max(height, p.y) + 4;
		height = Math.max(TrimUtil.TRIM_DEFAULT_HEIGHT, height);
		gc.dispose();
		return new Point(p.x + 15, height);
	}

	/**
	 * Creates the context menu.
	 */
	private void createContextMenu() {
		final MenuManager menuMgr = new MenuManager();
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager menuMgr) {
				menuMgr.add(new ShowLopbViewAction());
			}
		});
		final Menu menu = menuMgr.createContextMenu(this);
		setMenu(menu);
	}

	/**
	 * Paint composite.
	 * 
	 * @param gc
	 *            the gc
	 */
	private void paintComposite(GC gc) {
		final Rectangle rect = getClientArea();
		final int x = rect.x;
		final int y = rect.y;
		final int w = rect.width;
		final int h = rect.height;
		final int bw = imgBounds.width + 1; // button width
		final int dx = x + w - bw - 2; // divider x
		final int sw = w - bw - 3; // status width
		final int uw = (int) (sw * this.fractionOfSessionTime);
		final int ux = x + 1 + uw; // used mem right edge

		gc.setBackground(bgCol);
		gc.fillRectangle(rect);
		gc.setForeground(sepCol);
		gc.drawLine(dx, y, dx, y + h);
		gc.drawLine(ux, y, ux, y + h);
		gc.setForeground(topLeftCol);
		gc.drawLine(x, y, x + w, y);
		gc.drawLine(x, y, x, y + h);
		gc.setForeground(bottomRightCol);
		gc.drawLine(x + w - 1, y, x + w - 1, y + h);
		gc.drawLine(x, y + h - 1, x + w, y + h - 1);

		if (isBelowThreshold()) {
			gc.setBackground(timeWaitedLowCol);
		} else {
			gc.setBackground(timeWaitedHighCol);
		}
		gc.fillRectangle(x + 1, y + 1, uw, h - 2);

		final String s = this.statusText;
		final Point p = gc.textExtent(s);
		final int sx = (rect.width - 15 - p.x) / 2 + rect.x + 1;
		final int sy = (rect.height - 2 - p.y) / 2 + rect.y + 1;
		gc.setForeground(textCol);
		gc.drawString(s, sx, sy, true);
	}

	/**
	 * Paint icon.
	 * 
	 * @param gc
	 *            the gc
	 */
	private void paintIcon(GC gc) {
		final Rectangle rect = canvas.getClientArea();
		if (gcImage != null) {
			int by = (rect.height - imgBounds.height) / 2 + rect.y; // button y
			gc.drawImage(gcImage, rect.x, by);
		}
	}

	/**
	 * Update stats.
	 */
	private void updateStats() {
		// log.debug("updateStats");
		final JobStatsAggregatedData jobStatsAggregatedData = JobStatsManager.getInstance().getJobStatsAggregatedData();
		updateStatusText(jobStatsAggregatedData);
	}

	/**
	 * Update status text.
	 * 
	 * @param jobStatsData
	 *            the job stats data
	 */
	private void updateStatusText(final JobStatsAggregatedData jobStatsData) {
		this.statusText = LopbTextFormatter.getInstance().formatStatusText(jobStatsData);
		this.fractionOfSessionTime = Math.max(0.05f, jobStatsData.getFractionOfSession());
		redraw();
	}

	/**
	 * The Class ShowLopbViewAction.
	 */
	class ShowLopbViewAction extends Action {

		/**
		 * Instantiates a new show lopb view action.
		 */
		public ShowLopbViewAction() {
			super("View Lopb Report");
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.action.Action#run()
		 */
		public void run() {
			final IWorkbenchPage page = window.getActivePage();
			if (page != null) {
				try {
					page.showView(LopbView.id);
					JobStatsManager.getInstance().refreshStats();
				} catch (PartInitException e) {
					log.error("Failed to open LopbView", e);
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.swt.widgets.Widget#dispose()
	 */
	@Override
	public void dispose() {
		log.debug("dispose");
		super.dispose();
	}

	/**
	 * Checks if is below threshold.
	 * 
	 * @return true, if is below threshold
	 */
	private boolean isBelowThreshold() {
		return (this.fractionOfSessionTime < LOPB_STATUS_HIGH_LOW_THRESHOLD);
	}

	public void show() {
		if (!isDisposed()) {
			setVisible(true);
		}
	}

	public void hide() {
		if (!isDisposed()) {
			setVisible(false);
		}
	}

}
