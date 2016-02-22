package com.levins.webportal.certificate.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import com.levins.webportal.certificate.connection.FromInsisData;
import com.levins.webportal.certificate.data.CertificateInfo;
import com.levins.webportal.certificate.data.UserToken;

class CertificateCreateThread extends Thread {
	private int CLIENT_REQUEST_TIMEOUT = 15 * 60 * 1000; // 15 min.
	private static final String COMMA_DELIMITER = ";";

	private Socket connection;
	private DataInputStream in;
	private DataOutputStream out;
	private CreateNewBatFile batGenerator;
	private FromInsisData connectionToInsis;

	public CertificateCreateThread(Socket connection) throws IOException {
		this.connection = connection;
		this.connection.setSoTimeout(CLIENT_REQUEST_TIMEOUT);
		in = new DataInputStream(connection.getInputStream());
		out = new DataOutputStream(connection.getOutputStream());
		batGenerator = new CreateNewBatFile();
	}

	public void run() {
		connectionToInsis = createConnection();

		try {
			out.writeUTF(CreateCertServer.GREETING_MESSAGE_TO_CLIENT);
			out.flush();
			String result = null;
			while (!isInterrupted()) {
				String input = in.readUTF();

				boolean hasUserExist = hasUserExist(input);
				if (hasUserExist) {
					// if record exist get them
					String machRecord = createMachRecordString(connectionToInsis, input);
					result = machRecord;
				} else {
					CertificateInfo certificate = batGenerator.generateCert(input);

					result = certificate.toString();
					writeInDataBase(connectionToInsis, result);
				}
				out.writeUTF(result);
				out.flush();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			printSystemExitMessage();

		}
	}

	/**
	 * The method make select where SECURITY_ID and EGN has exist and return
	 * current record If have more of one record, get first
	 * 
	 * @param input
	 * @return
	 * @throws SQLException
	 */
	private String createMachRecordString(FromInsisData connectionToInsis,
			String input) throws SQLException {
		String[] currentInfo = input.split(";");
		int egn = -1; // the index of possition on egn value
		if (currentInfo.length < 6) {
			egn = 4;
		} else {
			egn = UserToken.EGN;
		}
		List<String> list = connectionToInsis.searchFromDataBase(
				currentInfo[UserToken.USERPORTAL], currentInfo[egn]);
		String result = list.get(0);
		return result;
	}

	private void printSystemExitMessage() {
		String systemMessageWhenConnectionLost = String.format(
				"%s : Connection lost  : %s:%s\n", new Date(), connection
						.getInetAddress().getHostAddress(), connection
						.getPort());
		System.out.println(systemMessageWhenConnectionLost);
	}

	/**
	 * Check whether the user was ever created u
	 * 
	 * @param currentInfo
	 *            - array from String with spited element
	 * @return true if user exist or false is not
	 * @throws SQLException
	 */
	private boolean hasUserExist(String input) throws SQLException {
		String[] currentInfo = input.replace("\"", "").split(";");
		String searchingSecurityId = currentInfo[UserToken.USERPORTAL];
		String searchingEgn = null;
		if (currentInfo.length < 6) {
			searchingEgn = currentInfo[4];
			return connectionToInsis.hasRecordExistsOnDataBase(searchingSecurityId, searchingEgn);
		} else {
			searchingEgn = currentInfo[UserToken.EGN];
			return connectionToInsis.hasRecordExistsOnDataBase(
					searchingSecurityId, searchingEgn);
		}

	}

	// TODO remove hard code
	private FromInsisData createConnection() {
		String host = "172.20.10.8";
		String port = "1521";
		String dataBaseName = "INSISDB";
		String user = "insis";
		String pass = "change2015";

		FromInsisData conn = new FromInsisData(host, port, dataBaseName, user,
				pass);
		return conn;
	}

	public static void writeInDataBase(FromInsisData connection,
			String infoToDataBase) {
		String[] currentRecord = infoToDataBase.split(COMMA_DELIMITER);

		String securityID = currentRecord[UserToken.USERPORTAL];
		String firstName = currentRecord[UserToken.FIRSTNAME];
		String lastName = currentRecord[UserToken.LASTNAME];
		String email = currentRecord[UserToken.MAIL];
		String password = currentRecord[UserToken.PASSWORD];
		String pathToCertificateFile = currentRecord[UserToken.PATHTOCERT];
		String egn = currentRecord[UserToken.EGN];

		try {
			// If record exist update other info
			if (connection.hasRecordExistsOnDataBase(egn, securityID)) {

				connection.updateInToDB(egn, securityID,
						FromInsisData.NAME_FIELD, firstName + " " + lastName);
				connection.updateInToDB(egn, securityID,
						FromInsisData.USEREMAIL, email);
				connection.updateInToDB(egn, securityID, FromInsisData.PATH,
						pathToCertificateFile);
				connection.updateInToDB(egn, securityID,
						FromInsisData.CERT_PASS, password);
				connection.updateInToDB(egn, securityID,
						FromInsisData.CERT_USER, securityID);
			} else {
				// insert new info
				connection.insertInToDB(securityID, firstName, lastName, email,
						password, pathToCertificateFile, egn);
			}
		} catch (SQLException e) {
			System.out
					.println("Problem with update or insert into data base CreateCertServer.clas line:197");
			e.printStackTrace();
		}

	}
}