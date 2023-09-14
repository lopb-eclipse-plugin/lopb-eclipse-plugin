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


// TODO: Auto-generated Javadoc
/**
 * The Class TimeUtil.
 */
public class TimeUtil {

	/**
	 * Gets the elapsed milliseconds.
	 * 
	 * @param instant the instant
	 * 
	 * @return the elapsed milliseconds
	 */
	public static long getElapsedMilliseconds(final long instant) {
		return TimeUtil.getElapsedMilliseconds(now(), instant);
	}

	/**
	 * Gets the elapsed milliseconds.
	 * 
	 * @param currentInstant the current instant
	 * @param previousInstant the previous instant
	 * 
	 * @return the elapsed milliseconds
	 */
	public static long getElapsedMilliseconds(final long currentInstant, final long previousInstant) {
		return (currentInstant - previousInstant);
	}

	/**
	 * Now.
	 * 
	 * @return the long
	 */
	public static long now() {
		return System.currentTimeMillis();
	}
}
