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

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.lopb.plugin.labels.LabelsManager;
import org.lopb.plugin.platform.jobs.JobStats;
import org.lopb.plugin.ui.preferences.LopbPreferenceChangeManager;

// TODO: Auto-generated Javadoc
/**
 * The Class LopbViewerFilter.
 */
public class LopbViewerFilter extends ViewerFilter {

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers
     *      .Viewer, java.lang.Object, java.lang.Object)
     */
    @Override
    public boolean select(Viewer viewer, Object parentElement, Object element) {
        JobStats jobStats = (JobStats) element;

        if (!LabelsManager.getInstance().isJobIncluded(jobStats.getJobId())) {
            // if job does not qualify for checked labels - exclude it
            return false;
        }

        if (!LopbPreferenceChangeManager.getInstance().isSystemJobsIncluded()) {
            // if system jobs should not be included - check if job is system
            // job
            if (jobStats.isSystemJob()) {
                // as this is system job - exclude it
                return false;
            }
        }

        return true;
    }
}
