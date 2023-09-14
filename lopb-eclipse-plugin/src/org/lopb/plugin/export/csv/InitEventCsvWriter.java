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
package org.lopb.plugin.export.csv;

import java.util.Date;

/**
 * The Class InitEventCsvWriter.
 */
public class InitEventCsvWriter extends EventCsvWriter {

	/**
	 * Instantiates a new inits the event csv writer.
	 * 
	 * @param baseDir
	 *            the base dir
	 * @param now
	 *            the now
	 */
	public InitEventCsvWriter(final String baseDir, final Date now) {
		super(baseDir, now, "init");
	}
}
