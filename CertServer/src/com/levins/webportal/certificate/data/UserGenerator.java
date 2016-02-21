package com.levins.webportal.certificate.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class UserGenerator {

	public List<String> createListOfUserFromString(String string)
			throws FileNotFoundException, IOException {
		List<String> allUsersFromFile = new ArrayList<String>();
		String regexToSplitInfo = "\\n";
		String[] currentLine = string.split(regexToSplitInfo);
		for (String line : currentLine) {
			allUsersFromFile.add(line);
		}

		return allUsersFromFile;
	}

	public List<String> createListOfUserFromFile(File file)
			throws FileNotFoundException, IOException {
		List<String> allUsersFromFile = new ArrayList<String>();
		String currentLine;
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
			while ((currentLine = br.readLine()) != null) {
				allUsersFromFile.add(currentLine);
			}
		} finally {
			br.close();
		}
		return allUsersFromFile;
	}

	/**
	 * Ask to enter all info about new certificate
	 * 
	 * @return All info about new cert
	 */
	public String createNewUser() {

		@SuppressWarnings("resource")
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter the user name");
		String userNameAndPassword = sc.nextLine();
		System.out.println("Enter the first name");
		String firstName = sc.nextLine();
		System.out.println("Enter the last name");
		String lastName = sc.nextLine();
		System.out.println("Enter the e-mail");
		String mail = sc.nextLine();
		CertificateInfo newUser = new CertificateInfo(userNameAndPassword, firstName,
				lastName, mail);

		return newUser.toString();

	}

}
