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

import org.lopb.plugin.platform.jobs.JobStatsAggregatedData;

// TODO: Auto-generated Javadoc
/**
 * The Class LopbTextFormatter.
 */
public class LopbTextFormatter {

	/** The Constant instance. */
	private static final LopbTextFormatter instance = new LopbTextFormatter();

	/**
	 * Instantiates a new lopb text formatter.
	 */
	private LopbTextFormatter() {

	}

	/**
	 * Gets the single instance of LopbTextFormatter.
	 * 
	 * @return single instance of LopbTextFormatter
	 */
	public static final LopbTextFormatter getInstance() {
		return instance;
	}

	/**
	 * Format status text.
	 * 
	 * @param jobStatsData
	 *            the job stats data
	 * 
	 * @return the string
	 */
	public String formatStatusText(final JobStatsAggregatedData jobStatsData) {
		final StringBuilder s = new StringBuilder(40);
		s.append("     ");
		formatStatusText(s, jobStatsData);
		s.append("    ");
		return s.toString();
	}

	/**
	 * Format tool tip text.
	 * 
	 * @param jobStatsData
	 *            the job stats data
	 * 
	 * @return the string
	 */
	public String formatHeaderText(final JobStatsAggregatedData jobStatsData) {
		final StringBuilder s = new StringBuilder(40);
		s.append("Lopb timers: Session = ");
		millisecondsToHHMMSS(s, jobStatsData.getSessionDurationMilliseconds());
		s.append(", Jobs = ");
		formatStatusText(s, jobStatsData);
		return s.toString();
	}

	/**
	 * Format status text.
	 * 
	 * @param s
	 *            the s
	 * @param jobStatsData
	 *            the job stats data
	 */
	private void formatStatusText(final StringBuilder s, final JobStatsAggregatedData jobStatsData) {
		millisecondsToHHMMSS(s, jobStatsData.getTotalMillisecondsAllJobs());
		s.append("(").append(formatPercentageOfSession(jobStatsData.getPercentageOfSession())).append("%) ");
	}

	/**
	 * Milliseconds to hhmmss.
	 * 
	 * @param buffer
	 *            the buffer
	 * @param ms
	 *            the ms
	 */
	private void millisecondsToHHMMSS(final StringBuilder buffer, final long ms) {
		final long time = ms / 1000;
		final long seconds = time % 60;
		final long minutes = (time % 3600) / 60;
		final long hours = time / 3600;

		buffer.append(formatTimerPart(hours)).append("h ");
		buffer.append(formatTimerPart(minutes)).append("m ");
		buffer.append(formatTimerPart(seconds)).append("s ");

	}

	/**
	 * Format percentage of session.
	 * 
	 * @param percentageOfSession
	 *            the percentage of session
	 * 
	 * @return the string
	 */
	private String formatPercentageOfSession(final float percentageOfSession) {
		final DecimalFormat pctFormat = FormatUtil.getDecimalFormat("#0");
		return pctFormat.format(percentageOfSession);
	}

	/**
	 * Format timer part.
	 * 
	 * @param timerPart
	 *            the timer part
	 * 
	 * @return the string
	 */
	private String formatTimerPart(final long timerPart) {
		final DecimalFormat timerPartFormat = FormatUtil.getDecimalFormat("#0");
		return timerPartFormat.format(timerPart);
	}

}
