/*
 *   This software is distributed under the terms of the FSF
 *   Gnu Lesser General Public License (see lgpl.txt).
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.tools.common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

/**
 * This class contains helper methods for generators.
 * 
 * @author (Fei) John Chen
 */
public class GeneratorHelper {
	public static int NOCHANGE_EXISTING_FILE = -1;
	public static int CREATE_NEW_FILE = 0;
	public static int UPDATE_EXISTING_FILE = 1;
	public static int UNDETERMINED = 999;

	public static List loadToStringListFromResource(String resourceFileName)
			throws Exception {
		List lines = new ArrayList();
		InputStream is = null;
		BufferedReader br = null;
		try {
			String filename = resourceFileName;
			if (!resourceFileName.startsWith("/"))
				filename = "/" + resourceFileName;
			is = GeneratorHelper.class.getResourceAsStream(filename);
			if (is != null) {
				br = new BufferedReader(new InputStreamReader(is, "utf-8"));
				while (true) {
					String line = br.readLine();
					if (line == null)
						break;
					lines.add(line);
				}
			} else {
				throw new Exception("No resource file with name "
						+ resourceFileName + " is found.");
			}
		} catch (Exception ex) {
			throw ex;
		} finally {
			try {
				if (is != null)
					is.close();
			} catch (Exception ex) {
			}
			try {
				if (br != null)
					br.close();
			} catch (Exception ex) {
			}
		}
		return lines;
	}

	public static String loadToStringFromResource(String resourceFileName)
			throws Exception {
		List lines = loadToStringListFromResource(resourceFileName);
		if (lines == null)
			return (String) null;
		String linebreak = System.getProperty("line.separator", "\r\n");
		StringBuffer sb = new StringBuffer();
		Iterator it = lines.iterator();
		while (it.hasNext()) {
			sb.append(it.next()).append(linebreak);
		}
		return sb.toString();
	}

	public static List loadToStringListFromFile(String fullFileName)
			throws Exception {
		List lines = new ArrayList();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					fullFileName), "utf-8"));
			while (true) {
				String line = br.readLine();
				if (line == null)
					break;
				lines.add(line);
			}
		} catch (Exception ex) {
			throw ex;
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (Exception ex) {
			}
		}
		return lines;
	}

	public static String loadToStringFromFile(String fullFileName)
			throws Exception {
		List lines = loadToStringListFromFile(fullFileName);
		if (lines == null)
			return (String) null;
		String linebreak = System.getProperty("line.separator", "\r\n");
		StringBuffer sb = new StringBuffer();
		Iterator it = lines.iterator();
		while (it.hasNext()) {
			sb.append(it.next()).append(linebreak);
		}
		return sb.toString();
	}

	public static int outputTo(String content, String rootPath,
			String relativePathToFile, String fileName, boolean overwrite)
			throws Exception {
		List l = new ArrayList();
		l.add(content);
		return outputTo(l, rootPath, relativePathToFile, fileName, overwrite);
	}

	public static int outputTo(String content, String fullPathToOutputFile, boolean overwrite)
			throws Exception {
		List l = new ArrayList();
		l.add(content);
		return outputTo(l, fullPathToOutputFile, overwrite);
	}

	public static int outputTo(List content, String rootPath,
			String relativePathToFile, String fileName, boolean overwrite)
			throws Exception {
		File file = new File(rootPath);
		if (!file.exists())
			throw new IllegalArgumentException("Path \"" + rootPath
					+ "\" does not exist.");

		String fullFileName = "";
		if (relativePathToFile == null || "".equals(relativePathToFile)) {
			fullFileName = rootPath + File.separatorChar + fileName;
		} else {
			fullFileName = rootPath + File.separatorChar + relativePathToFile
					+ File.separatorChar + fileName;
		}

		int status = UNDETERMINED;
		file = new File(fullFileName);
		if (file.exists()) {
			status = NOCHANGE_EXISTING_FILE;
			if (overwrite) {
				writeToFile(content, fullFileName);
				status = UPDATE_EXISTING_FILE;
			}
		} else {
			prepareSubPath(rootPath, relativePathToFile);
			writeToFile(content, fullFileName);
			status = CREATE_NEW_FILE;
		}
		return status;
	}

	public static int outputTo(List content, String fullPathToOutputFile, boolean overwrite)
			throws Exception {
		String fullFileName = fullPathToOutputFile;

		int status = UNDETERMINED;
		File file = new File(fullFileName);
		if (file.exists()) {
			status = NOCHANGE_EXISTING_FILE;
			if (overwrite) {
				writeToFile(content, fullFileName);
				status = UPDATE_EXISTING_FILE;
			}
		} else {
			file.getParentFile().mkdirs();
			writeToFile(content, fullFileName);
			status = CREATE_NEW_FILE;
		}
		return status;
	}

	private static void prepareSubPath(String outputMainPath, String subPath) {
		if (subPath == null || "".equals(subPath)) return;
		String path = outputMainPath;
		StringTokenizer st = new StringTokenizer(subPath, File.separator);
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			path += File.separatorChar + token;
			File dir = new File(path);
			if (!dir.exists())
				dir.mkdir();
		}
	}

	public static void writeToFile(String content, String fileFullName)
			throws Exception {
		List l = new ArrayList();
		l.add(content);
		writeToFile(l, fileFullName);
	}

	public static void writeToFile(List content, String fileFullName)
			throws Exception {
		PrintWriter out = null;
		try {
			out = new PrintWriter(new BufferedWriter(new FileWriter(
					fileFullName)));
			Iterator it = content.iterator();
			while (it.hasNext()) {
				out.print(it.next());
			}
		} catch (Exception ex) {
			throw ex;
		} finally {
			try {
				if (out != null)
					out.close();
			} catch (Exception ex) {
			}
		}
	}
}