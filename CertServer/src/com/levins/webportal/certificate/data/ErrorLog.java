package com.levins.webportal.certificate.data;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;


public class ErrorLog {
	private static final String NEW_LINE_SEPARATOR = "\n";
	public static final String ERROR_LOG_FILE_NAME = "errorLog.log";
	public static final String SKIPPED_USERS_LOG_FILE_NAME = "skippedUserLog.log";

	public Writer createLogFile(Exception e, String errorLogFileName) {
		Writer writer = null;
		try {
			writer = new FileWriter(errorLogFileName, true);
		} catch (IOException e1) {
			e1.printStackTrace(new PrintWriter(new BufferedWriter(writer), true));
		}
		e.printStackTrace(new PrintWriter(new BufferedWriter(writer), true));
		return writer;
	}

	// TODO
	public void createLog(String fileName, String header,
			String skippedUser) {

		String fileHeader = header;
		FileWriter fileWriter = null;

		try {
			if (!DataValidator.chekFileExist(fileName)) {
				fileWriter = addHeader(fileName, fileHeader);
			} else {
				fileWriter = new FileWriter(fileName, true);
			}
			addNewRecordInFile(fileWriter, skippedUser);

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fileWriter.flush();
				fileWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private FileWriter addHeader(String fileName, String FILE_HEADER)
			throws IOException {
		FileWriter fileWriter;
		fileWriter = new FileWriter(fileName);
		fileWriter.append(FILE_HEADER.toString());
		fileWriter.append(NEW_LINE_SEPARATOR);
		return fileWriter;
	}

	private void addNewRecordInFile(FileWriter fileWriter, String currentRecord)
			throws IOException {
		fileWriter.append(currentRecord);
		fileWriter.append(NEW_LINE_SEPARATOR);
	}
}
