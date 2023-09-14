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
package org.lopb.plugin.reports;

import java.io.IOException;

/**
 * Implementations of this interface generate a report for data specified via
 * implementation-specific fields.
 * 
 */
public interface Report {

	/**
	 * Generates the report.
	 * 
	 * @throws IOException
	 *             thrown if an IO exception occurs
	 */
	void run() throws IOException;

}
