package com.levins.webportal.certificate.server;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

import com.levins.webportal.certificate.data.CertificateInfo;
import com.levins.webportal.certificate.data.DateCreator;
import com.levins.webportal.certificate.data.UserToken;

public class CreateNewBatFile {

	// W00000001_01;firstName;lastName;password;mail;pathToCurrentCertificateFile
	private static final String PATH = "C:\\distr\\cert\\";
	private static final String BAT_FILE_NAME = "newCertificate.bat";
	/**
	 * COMMAND_BAT_FILE - content userName;password;firstName;lastName
	 */
	private static String COMMAND_BAT_FILE = "call generateClientCertificate %s %d \"%s %s\" lev-ins ssl4Ever!";

	/**
	 * 
	 * @param inputInfo
	 *            - creates a file that is created a new certificate file, and
	 *            then move each of them into folder of the current day
	 * @throws IOException
	 */
	public CertificateInfo generateCert(String inputInfo) throws IOException {
		String[] currentInfo = inputInfo.replace("\"", "").split(";");
		String userName = null;
		String firstName = null;
		String lastName = null;
		String email = null;
		String egnValue = null;
		// TODO need to fix
		if (currentInfo.length < 6) {
			userName = currentInfo[UserToken.USERPORTAL];
			firstName = currentInfo[UserToken.FIRSTNAME];
			lastName = currentInfo[UserToken.LASTNAME];
			email = currentInfo[UserToken.MAIL];
			egnValue = currentInfo[4];
		} else {
			userName = currentInfo[UserToken.USERPORTAL];
			firstName = currentInfo[UserToken.FIRSTNAME];
			lastName = currentInfo[UserToken.LASTNAME];
			email = currentInfo[UserToken.MAIL];
			egnValue = currentInfo[UserToken.EGN];
		}

		int password = generatePassword();

		String contentBatFile = String.format(COMMAND_BAT_FILE, userName,
				password, firstName, lastName);
		String absolutePathToBatFile = String.format(PATH + BAT_FILE_NAME);
		File outputFile = new File(absolutePathToBatFile);

		writeNewFile(contentBatFile, outputFile);
		runBatFile(absolutePathToBatFile);
		try {
			// wait a few seconds to create the file
			// TODO - try with less second
			Thread.sleep(4000); // 1000 milliseconds is one second.
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
		}

		String currentCertificatFileDestination = moveCertFileIntoTodayFolder(userName);
		CertificateInfo newUserCert = new CertificateInfo(userName, firstName,
				lastName, email, String.valueOf(password),
				currentCertificatFileDestination, egnValue);

		return newUserCert;
	}

	private int generatePassword() {
		int bound = 20000;
		int minimumValue = 1000;
		return Math.abs((new Random().nextInt(bound) + minimumValue));
	}

	private void runBatFile(String fileToRun) throws IOException {
		try {
			Runtime.getRuntime().exec(fileToRun);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("run option: done");
	}

	/**
	 * 
	 * @param toSave
	 *            - String that need to save
	 * @param file
	 *            - the new created file
	 * @throws IOException
	 */
	private void writeNewFile(String toSave, File file) throws IOException {
		PrintWriter bufferWrite = new PrintWriter(new FileWriter(file));
		bufferWrite.println(toSave);
		bufferWrite.close();
	}

	/**
	 * This method moved file into folder with name current day and return
	 * String with destination
	 * 
	 * @param certName
	 * @throws IOException
	 * @return the new path location
	 */
	public static String moveCertFileIntoTodayFolder(String certName)
			throws IOException {
		DateCreator dateCreate = new DateCreator();
		String newPathLocation = PATH + dateCreate.createdDate() + "\\";
		new File(newPathLocation).mkdirs();
		String fileExtension = ".pfx";
		String fileName = certName + fileExtension;
		File fileToMove = new File(PATH + fileName);
		fileToMove.renameTo(new File(newPathLocation + fileToMove.getName()));
		fileToMove.delete();
		System.out.println("Move option done");
		return dateCreate.createdDate() + "\\";
	}

}
