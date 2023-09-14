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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * A Report implementation that aggregates the data from multiple csv source files to a single destination file.
 */
public class AggregateReport implements Report {

	private static final String SOURCE_EXTENSION = "csv";

	private File sourceDir;
	private File destination;
	private String headerLine;
	private final Map<JobKey, JobStats> aggregateRecords = new HashMap<JobKey, JobStats>();

	public static void main(String args[]) throws IOException {
		if (args.length < 2) {
			System.err.println("Usage:");
			System.err.println(AggregateReport.class.getName() + " <destination file> <source dir>");
			return;
		}
		final AggregateReport report = new AggregateReport();
		report.setDestination(new File(args[0]));
		report.setSourceDir(new File(args[1]));
		report.run();
	}

	public void run() throws IOException {
		// make sure all required report parameters are set
		if (sourceDir == null) {
			throw new IllegalArgumentException("No source directory specified");
		}
		if (!sourceDir.exists()) {
			throw new IllegalArgumentException("Source directory does not exist: " + sourceDir.getAbsolutePath());
		}
		if (!sourceDir.isDirectory()) {
			throw new IllegalArgumentException("Source directory is not a directory: " + sourceDir.getAbsolutePath());
		}
		if (destination == null) {
			throw new IllegalArgumentException("No destination specified");
		}

		// get the files to extract data from
		final String[] sourceExtensions = { SOURCE_EXTENSION };
		final Collection<?> sourceFiles = FileUtils.listFiles(sourceDir, sourceExtensions, true);
		if (sourceFiles.isEmpty()) {
			throw new IllegalArgumentException("No source files found");
		}
		final Collection<File> filteredFiles = filterSourceFiles(sourceFiles);
		if (filteredFiles.isEmpty()) {
			throw new IllegalArgumentException("No filtered files found");
		}

		// write the report file
		headerLine = null;
		final PrintWriter writer = new PrintWriter(destination);
		try {
			// generate aggregates from all source files, and writes out the report header
			for (final File filteredFile : filteredFiles) {
				processSource(filteredFile, writer);
			}

			// write out all aggregated report results
			for (final Entry<JobKey, JobStats> aggregate : aggregateRecords.entrySet()) {
				final String aggregateLine = buildAggregateLine(aggregate.getKey(), aggregate.getValue());
				writer.println(aggregateLine);
			}
		} finally {
			writer.close();
		}
	}

	private Collection<File> filterSourceFiles(Collection<?> sourceFiles) {
		final List<File> filteredFiles = new ArrayList<File>();
		File lastJobStatsFile = null;
		for (final Object sourceObj : sourceFiles) {
			final File sourceFile = (File) sourceObj;
			final String filename = sourceFile.getName();
			if (filename.endsWith("_init.csv") || filename.endsWith("_reset.csv") || filename.endsWith("_shutdown.csv")) {
				if (lastJobStatsFile != null) {
					filteredFiles.add(lastJobStatsFile);
					lastJobStatsFile = null;
				}
			} else if (filename.endsWith("_jobstats.csv")) {
				lastJobStatsFile = sourceFile;
			}
		}
		// make sure to include the last file if no shutdown or reset occurred
		if (lastJobStatsFile != null) {
			filteredFiles.add(lastJobStatsFile);
		}
		return filteredFiles;
	}

	private void processSource(File sourceFile, PrintWriter writer) throws IOException {
		final String sourceFileName = sourceFile.getName();
		final BufferedReader reader = new BufferedReader(new FileReader(sourceFile));
		try {
			final String sourceHeader = reader.readLine();
			if (headerLine == null) {
				headerLine = sourceHeader;
				writer.println("HOSTNAME,DATE," + headerLine);
			} else if (!headerLine.equalsIgnoreCase(sourceHeader)) {
				throw new IOException("Source header does not match: " + sourceHeader + " (Expected: " + headerLine
						+ ")");
			}

			final String[] hdt = extractHostnameDate(sourceFileName);
			final String hostname = hdt[0];
			final String date = hdt[1];

			String nextLine;
			while ((nextLine = reader.readLine()) != null) {
				if (!"".equals(nextLine.trim())) {
					final String[] lineParts = splitCsvLine(nextLine);
					if (lineParts.length < 10) {
						throw new IllegalArgumentException("Invalid source line format in file " + sourceFileName
								+ ": " + nextLine);
					}

					final JobKey key = extractJobKey(hostname, date, lineParts);
					final JobStats lineStats = extractJobStats(lineParts);

					final JobStats aggregateStats = aggregateRecords.get(key);
					if (aggregateStats == null) {
						aggregateRecords.put(key, lineStats);
					} else {
						aggregateStats.increment(lineStats);
					}
				}
			}
		} catch (final Exception e) {
			throw new IOException("Error processing file: " + sourceFile.getAbsolutePath(), e);
		} finally {
			reader.close();
		}
	}

	private String[] splitCsvLine(String nextLine) {
		final List<String> splitValues = new ArrayList<String>();
		final char[] nextLineChars = new char[nextLine.length()];
		nextLine.getChars(0, nextLine.length(), nextLineChars, 0);
		boolean inQuotes = false;
		StringBuilder nextValue = new StringBuilder();
		for (final char nextChar : nextLineChars) {
			if (nextChar == '"') {
				inQuotes = !inQuotes;
				nextValue.append(nextChar);
			} else if (nextChar == ',') {
				if (inQuotes) {
					nextValue.append(nextChar);
				} else {
					final String decodedValue = StringEscapeUtils.unescapeCsv(nextValue.toString());
					splitValues.add(decodedValue);
					nextValue = new StringBuilder();
				}

			} else {
				nextValue.append(nextChar);
			}
		}
		if (nextValue.length() > 0) {
			final String decodedValue = StringEscapeUtils.unescapeCsv(nextValue.toString());
			splitValues.add(decodedValue);
		}
		return splitValues.toArray(new String[splitValues.size()]);
	}

	/*
	 * TODO: do this with regular expressions
	 */
	private String[] extractHostnameDate(final String filename) {
		final int firstUnderscore = filename.indexOf("_");
		if (firstUnderscore < 0) {
			throw new RuntimeException("Cannot extract hostname from: " + filename);
		}
		final String hostname = filename.substring(0, firstUnderscore);

		final int secondUnderscore = filename.indexOf("_", firstUnderscore + 1);
		if (secondUnderscore < 0) {
			throw new RuntimeException("Cannot extract hostname from: " + filename);
		}
		final String date = filename.substring(firstUnderscore + 1, secondUnderscore);

		return new String[] { hostname, date };
	}

	private JobKey extractJobKey(String hostname, String date, String[] lineParts) {
		final JobKey key = new JobKey();
		key.setHostname(hostname);
		key.setDate(date);
		key.setClassName(lineParts[0]);
		key.setDescription(lineParts[1]);
		key.setSystemJob(lineParts[8]);
		key.setBlockingJob(lineParts[9]);
		return key;
	}

	private JobStats extractJobStats(String[] lineParts) {
		final JobStats stats = new JobStats();
		stats.getAliases().add(lineParts[2]);
		final String[] labels = StringUtils.split(lineParts[3]);
		stats.getLabels().addAll(Arrays.asList(labels));
		stats.setRunCount(Integer.parseInt(lineParts[4]));
		stats.setErrorCount(Integer.parseInt(lineParts[5]));
		stats.setTotalTime(Double.parseDouble(lineParts[6]));
		return stats;
	}

	private String buildAggregateLine(JobKey key, JobStats stats) {
		final StringBuilder line = new StringBuilder();
		line.append(key.getHostname());
		line.append(",").append(key.getDate());
		line.append(",").append(key.getClassName());
		line.append(",").append(key.getDescription());
		line.append(",\"").append(StringUtils.join(stats.getAliases(), ",")).append("\"");
		line.append(",\"").append(StringUtils.join(stats.getLabels(), ",")).append("\"");
		final int runCount = stats.getRunCount();
		line.append(",").append(runCount);
		line.append(",").append(stats.getErrorCount());
		final double totalTime = stats.getTotalTime();
		line.append(",").append(totalTime);
		line.append(",").append(runCount == 0 ? 0 : totalTime / runCount);
		line.append(",").append(key.getSystemJob());
		line.append(",").append(key.getBlockingJob());
		return line.toString();
	}

	public File getSourceDir() {
		return sourceDir;
	}

	public void setSourceDir(File sourceDir) {
		this.sourceDir = sourceDir;
	}

	public File getDestination() {
		return destination;
	}

	public void setDestination(File destination) {
		this.destination = destination;
	}

	/**
	 * Holds the information that uniquely identifies an aggregated job record that will be exported.
	 */
	private static class JobKey {
		private String hostname;
		private String date;
		private String className;
		private String description;
		private String systemJob;
		private String blockingJob;

		public String getHostname() {
			return hostname;
		}

		public void setHostname(String hostname) {
			this.hostname = hostname;
		}

		public String getDate() {
			return date;
		}

		public void setDate(String date) {
			this.date = date;
		}

		public String getClassName() {
			return className;
		}

		public void setClassName(String className) {
			this.className = className;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public String getSystemJob() {
			return systemJob;
		}

		public void setSystemJob(String isSystemJob) {
			this.systemJob = isSystemJob;
		}

		public String getBlockingJob() {
			return blockingJob;
		}

		public void setBlockingJob(String isBlockingJob) {
			this.blockingJob = isBlockingJob;
		}

		@Override
		public boolean equals(Object obj) {
			return EqualsBuilder.reflectionEquals(this, obj);
		}

		@Override
		public int hashCode() {
			return HashCodeBuilder.reflectionHashCode(this);
		}

	}

	/**
	 * Holds time and run count statistics for an inbound job entry or the outbound aggregate.
	 * 
	 */
	private static class JobStats {
		private final Set<String> aliases = new HashSet<String>();
		private final Set<String> labels = new HashSet<String>();
		private int runCount;
		private int errorCount;
		private double totalTime;

		public void increment(JobStats stats) {
			this.aliases.addAll(stats.getAliases());
			this.labels.addAll(stats.getLabels());
			this.runCount += stats.getRunCount();
			this.errorCount += stats.getErrorCount();
			this.totalTime += stats.getTotalTime();
		}

		public Set<String> getAliases() {
			return aliases;
		}

		public Set<String> getLabels() {
			return labels;
		}

		public int getRunCount() {
			return runCount;
		}

		public void setRunCount(int numRuns) {
			this.runCount = numRuns;
		}

		public int getErrorCount() {
			return errorCount;
		}

		public void setErrorCount(int numErrors) {
			this.errorCount = numErrors;
		}

		public double getTotalTime() {
			return totalTime;
		}

		public void setTotalTime(double totalTimeSecs) {
			this.totalTime = totalTimeSecs;
		}

	}
}
