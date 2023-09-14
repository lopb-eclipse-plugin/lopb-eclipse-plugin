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
package org.lopb.plugin.util;

import java.text.DecimalFormat;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This utility provides ThreadLocal cached Format instances to eliminate the need for synchronized formatter access or
 * expensive over-instantiation.
 * 
 */
public final class FormatUtil {

	private static final Log log = LogFactory.getLog(FormatUtil.class);

	/** The ThreadLocal cache of DecimalFormat instances */
	private static final Map<String, ThreadLocal<DecimalFormat>> decimalFormats = new ConcurrentHashMap<String, ThreadLocal<DecimalFormat>>();

	private FormatUtil() {
		// no instantiation
	}

	/**
	 * Create or retrieve a thread-safe instance of DecimalFormat for the given format pattern.
	 * 
	 * @param format
	 *            the format pattern
	 * @return a DecimalFormat instance
	 */
	public static final DecimalFormat getDecimalFormat(final String format) {
		// retrieve the formatter
		ThreadLocal<DecimalFormat> formatter = decimalFormats.get(format);
		if (formatter == null) {
			// if it doesn't exist, create it. Don't worry about synchronizing to block multiple new instances
			formatter = new ThreadLocal<DecimalFormat>() {
				@Override
				protected DecimalFormat initialValue() {
					return new DecimalFormat(format);
				}
			};
			decimalFormats.put(format, formatter);
		}

		// retrieve the thread local formatter instance
		return formatter.get();
	}

}
