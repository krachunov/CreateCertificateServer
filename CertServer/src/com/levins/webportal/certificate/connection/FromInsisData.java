package com.levins.webportal.certificate.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.levins.webportal.certificate.data.DataValidator;
import com.levins.webportal.certificate.data.DateCreator;
import com.levins.webportal.certificate.data.ErrorLog;

public class FromInsisData {
	private String insisHost;
	private String insisPort;
	private String dataBaseName;
	private String insisUser;
	private String insisPass;
	private List<String> errorLog;

	public static final String NAME_FIELD = "NAME";
	public static final String USEREMAIL = "USEREMAIL";
	public static final String EGN = "EGN";
	public static final String SECURITY_ID = "SECURITY_ID";
	public static final String PATH = "PATH";
	public static final String CERT_PASS = "CERT_PASS";
	public static final String CERT_USER = "CERT_USER";
	public static final String FILE_HEADER = "user;name;mail;EGN;date";

	public FromInsisData(String host, String port, String dataBaseName,
			String user, String pass) {
		this.insisHost = host;
		this.insisPort = port;
		this.dataBaseName = dataBaseName;
		this.insisUser = user;
		this.insisPass = pass;
		this.errorLog = new ArrayList<String>();
	}

	public List<String> selectWebPortalUserFromDataBase(String findingName)
			throws SQLException {

		String queryPortal = "Select pp.name, pp.egn, ps.user_email, ps.security_id from p_people pp, p_staff ps where pp.man_id=ps.man_id and ps.security_id like ?";
		Connection conn = createConnectionToServer();

		PreparedStatement preStatement = conn.prepareStatement(queryPortal);
		preStatement.setString(1, findingName);

		ResultSet result = preStatement.executeQuery();

		List<String> allRecordsFromServer = new ArrayList<String>();
		dataProcessingFromInsis(result, allRecordsFromServer);
		return allRecordsFromServer;
	}

	public List<String> getResultFromInsis(FromInsisData insis,
			String searchingUser) {
		List<String> resultFromDataBase = null;

		try {
			if (insis.hasRecordExistsOnINSIS(searchingUser)) {
				resultFromDataBase = insis
						.selectWebPortalUserFromDataBase(searchingUser);
			} else {
				String errorMessage = ("There is now such user");
				System.err.println(errorMessage);
			}

		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		return resultFromDataBase;
	}

	/**
	 * SELECT * FROM LEV_USERS_PORTAL p where p.egn like '%s' and p.security_id
	 * like '%s
	 * 
	 * @param userName
	 *            - first param
	 * @param egn
	 *            second param
	 * @return list with result
	 * @throws SQLException
	 */
	public List<String> searchFromDataBase(String userName, String egn)
			throws SQLException {
		String queryPortal = "SELECT * FROM LEV_USERS_PORTAL where EGN like ? and SECURITY_ID like ?";
		Connection conn = createConnectionToServer();

		PreparedStatement preStatement = conn.prepareStatement(queryPortal);
		preStatement.setString(1, egn);
		preStatement.setString(2, userName);
		ResultSet result = preStatement.executeQuery();

		List<String> allRecordsFromServer = dataProcessingCrateList(result);

		return allRecordsFromServer;
	}

	/**
	 * 
	 * @param securityID
	 *            - where clause
	 * @param egn
	 *            - where clause
	 * @param columnName
	 *            - column who wants to update
	 * @param value
	 *            - value who wants to update
	 * @return
	 */
	public boolean updateInToDB(String securityID, String egn,
			String columnName, String value) {
		String queryUP = String
				.format("UPDATE LEV_USERS_PORTAL SET %s='%s' WHERE SECURITY_ID=? and EGN=?",
						columnName, value);
		Connection conn = null;
		try {
			conn = createConnectionToServer();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = conn.prepareStatement(queryUP);
			preparedStatement.setString(1, securityID);
			preparedStatement.setString(2, egn);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		try {
			preparedStatement.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Method who insert value into LEV_USERS_PORTAL table
	 * 
	 * @param user
	 * @param firstName
	 * @param lastName
	 * @param mail
	 * @param password
	 * @param path
	 * @param egn
	 * @return
	 */
	public boolean insertInToDB(String user, String firstName, String lastName,
			String mail, String password, String path, String egn) {
		String fullName = firstName + " " + lastName;
		String fileType = ".pfx";
		String certificateFileName = user + fileType;
		String queryUP = "INSERT INTO LEV_USERS_PORTAL (NAME,EGN,USEREMAIL,SECURITY_ID,PATH,CERT_USER,CERT_PASS) VALUES (?,?,?,?,?,?,?)";

		Connection conn = null;
		try {
			conn = createConnectionToServer();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = conn.prepareStatement(queryUP);
			preparedStatement.setString(1, fullName);
			preparedStatement.setString(2, egn);
			preparedStatement.setString(3, mail);
			preparedStatement.setString(4, user);
			preparedStatement.setString(5, path);
			preparedStatement.setString(6, certificateFileName);
			preparedStatement.setString(7, password);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		try {
			preparedStatement.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Create connection to INSIS IS
	 * 
	 * @return Object type Connection
	 * @throws SQLException
	 */
	private Connection createConnectionToServer() throws SQLException {
		// URL of Oracle database server
		String url = String.format("jdbc:oracle:thin:@%s:%s:%s",
				getInsisHost(), getInsisPort(), dataBaseName);

		// properties for creating connection to Oracle database
		Properties props = new Properties();
		props.setProperty("user", getInsisUser());
		props.setProperty("password", getInsisPassword());
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		// creating connection to Oracle database using JDBC
		Connection conn = DriverManager.getConnection(url, props);
		return conn;
	}

	/**
	 * This method processing data incoming from LEV_USERS_PORTAL
	 * 
	 * @param result
	 * @return
	 * @throws SQLException
	 */
	private List<String> dataProcessingCrateList(ResultSet result)
			throws SQLException {
		List<String> listWithUsers = new ArrayList<String>();
		while (result.next()) {
			String userName = result.getString("SECURITY_ID");
			String name = result.getString("NAME");
			String pass = result.getString("CERT_PASS");
			String mail = result.getString("USEREMAIL");
			String path = result.getString("PATH");
			String egn = result.getString("EGN");

			DataValidator validator = new DataValidator();
			if (mail != null) {
				mail = validator.removeWhitespace(mail);
			}

			if (userName == null || name == null || mail == null
					|| !DataValidator.validateMail(mail) || egn == null) {
				DateCreator dateCreate = new DateCreator();
				final String timeAndDateOfError = dateCreate
						.createdDate("dd_MM_yyyy':'HH:mm:");
				String errorRecord = String.format("%s;%s;%s;%s;%s", userName,
						name, mail, egn, timeAndDateOfError);
				errorLog.add(errorRecord);
				ErrorLog logger = new ErrorLog();
				logger.createLog(ErrorLog.SKIPPED_USERS_LOG_FILE_NAME,
						FILE_HEADER, errorRecord);
				continue;
			} else {
				String nameWithRemoveWhitespace = validator
						.removeWhitespace(name);
				String splitedName = splitCamelCase(nameWithRemoveWhitespace);
				String spliteEnglishdName = convertToEng(splitedName);
				String regexSplitedName = "\\s+";
				String[] splitFirstLastName = spliteEnglishdName
						.split(regexSplitedName);
				String firstName = splitFirstLastName[0];
				String secondName = splitFirstLastName[1];
				String newRecord = String.format("%s;%s;%s;%s;%s;%s;%s",
						userName, firstName, secondName, mail, pass, path, egn);
				listWithUsers.add(newRecord);
			}

		}

		return listWithUsers;
	}

	/**
	 * This method use incoming information from INSIS Tables
	 * 
	 * Get result from SQL query and return string with format:
	 * userName;fistName;lastName;mail;password;path;egn
	 * 
	 * @param result
	 * @param listWithUsers
	 * @throws SQLException
	 */
	private void dataProcessingFromInsis(ResultSet result,
			List<String> listWithUsers) throws SQLException {
		while (result.next()) {

			String userName = result.getString("SECURITY_ID");
			String name = result.getString("NAME");
			String mail = result.getString("USER_EMAIL");
			String egn = result.getString("EGN");

			DataValidator validator = new DataValidator();
			if (mail != null) {
				mail = validator.removeWhitespace(mail);
			}

			if (userName == null || name == null || mail == null
					|| !DataValidator.validateMail(mail) || egn == null) {
				DateCreator dateCreate = new DateCreator();
				final String timeAndDateOfError = dateCreate
						.createdDate("dd_MM_yyyy':'HH:mm:");
				;
				String errorRecords = String.format("%s;%s;%s;%s;%s", userName,
						name, mail, egn, timeAndDateOfError);
				errorLog.add(errorRecords);

				ErrorLog logger = new ErrorLog();
				logger.createLog(ErrorLog.SKIPPED_USERS_LOG_FILE_NAME,
						FILE_HEADER, errorRecords);

				System.out.println("Skiped user: " + userName);
				continue;
			} else {
				String nameWithRemoveWhitespace = validator
						.removeWhitespace(name);
				String nameEng = convertToEng(nameWithRemoveWhitespace);
				String[] splitFirstLastName = nameEng.split("\\s+");
				String firstName = splitFirstLastName[0];
				String secondName = splitFirstLastName[1];
				String emptyCells = "";
				String newRecord = String.format("%s;%s;%s;%s;%s;%s;%s",
						userName, firstName, secondName, mail, egn, emptyCells,
						emptyCells);
				listWithUsers.add(newRecord);
			}
		}
	}

	/**
	 * This method checks if mail is valid
	 * 
	 * @param emailStr
	 *            - mail who want to check
	 * @return - true if mail is valid or false the otherwise
	 */

	/**
	 * Check if record exist into LEV_USERS_PORTAL table and return true if
	 * exist or false if isn't
	 * 
	 * @param searchingSecurityId
	 * @param searchingEgn
	 * @return - true or false
	 * @throws SQLException
	 */
	public boolean hasRecordExistsOnDataBase(String searchingSecurityId,
			String searchingEgn) throws SQLException {

		String queryPortal = String
				.format("SELECT (CASE WHEN EXISTS (SELECT * FROM LEV_USERS_PORTAL WHERE SECURITY_ID = '%s'and EGN = '%s') THEN '1' ELSE '0' end) from DUAL",
						searchingSecurityId, searchingEgn);

		Connection conn = createConnectionToServer();
		PreparedStatement preStatement = conn.prepareStatement(queryPortal);
		ResultSet result = preStatement.executeQuery();
		boolean exists = false;
		if (result.next()) {

			exists = result.getBoolean(1);
		}
		return exists;
	}

	/**
	 * Check if record exist into LEV_USERS_PORTAL table and return true if
	 * exist or false if isn't
	 * 
	 * @param searchingSecurityId
	 * @return
	 * @throws SQLException
	 */
	public boolean hasRecordExistsOnINSIS(String searchingSecurityId)
			throws SQLException {

		String queryPortal = String
				.format("SELECT (CASE WHEN EXISTS (Select pp.name, pp.egn, ps.user_email, ps.security_id from p_people pp, p_staff ps where pp.man_id=ps.man_id and ps.security_id like '%s') THEN '1' ELSE '0' end) from DUAL",
						searchingSecurityId);

		Connection conn = createConnectionToServer();
		PreparedStatement preStatement = conn.prepareStatement(queryPortal);
		ResultSet result = preStatement.executeQuery();
		boolean exists = false;
		if (result.next()) {

			exists = result.getBoolean(1);
		}
		return exists;
	}

	/**
	 * Convert cyrillic String in to English. If has some unrecognized symbol
	 * get the same symbol and put it in same place
	 * 
	 * @return - converted String
	 */
	private static String convertToEng(String input) {
		char[] wordByLetter = input.toCharArray();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < wordByLetter.length; i++) {
			char tempLetter = wordByLetter[i];
			sb.append(convertBulgarianLetterToEnglish(tempLetter));
		}
		return sb.toString();
	}

	private static String convertBulgarianLetterToEnglish(char latter) {
		switch (latter) {
		case 'А':
			return "A";
		case 'а':
			return "a";
		case 'Б':
			return "B";
		case 'б':
			return "b";
		case 'В':
			return "V";
		case 'в':
			return "v";
		case 'Г':
			return "G";
		case 'г':
			return "g";
		case 'Д':
			return "D";
		case 'д':
			return "d";
		case 'Е':
			return "E";
		case 'е':
			return "e";
		case 'Ж':
			return "ZH";
		case 'ж':
			return "zh";
		case 'З':
			return "Z";
		case 'з':
			return "z";
		case 'И':
			return "I";
		case 'и':
			return "i";
		case 'Й':
			return "J";
		case 'й':
			return "j";
		case 'К':
			return "K";
		case 'к':
			return "k";
		case 'Л':
			return "L";
		case 'л':
			return "l";
		case 'М':
			return "M";
		case 'м':
			return "m";
		case 'Н':
			return "N";
		case 'н':
			return "n";
		case 'О':
			return "O";
		case 'о':
			return "o";
		case 'П':
			return "P";
		case 'п':
			return "p";
		case 'Р':
			return "R";
		case 'р':
			return "r";
		case 'С':
			return "S";
		case 'с':
			return "s";
		case 'Т':
			return "T";
		case 'т':
			return "t";
		case 'У':
			return "U";
		case 'у':
			return "u";
		case 'Ф':
			return "F";
		case 'ф':
			return "f";
		case 'Х':
			return "H";
		case 'х':
			return "h";
		case 'Ц':
			return "TZ";
		case 'ц':
			return "tz";
		case 'Ч':
			return "CH";
		case 'ч':
			return "ch";
		case 'Ш':
			return "SH";
		case 'ш':
			return "sh";
		case 'Щ':
			return "SHT";
		case 'щ':
			return "sht";
		case 'Ъ':
			return "A";
		case 'ъ':
			return "a";
		case 'Ь':
			return "Y";
		case 'ь':
			return "y";
		case 'Ю':
			return "YU";
		case 'ю':
			return "yu";
		case 'Я':
			return "YA";
		case 'я':
			return "ya";
		case ' ':
			return " ";
		default:
			break;
		}
		return String.valueOf(latter);
	}

	/**
	 * if glued to one another name them into major principle of small letters
	 * 
	 * @param stringToSplit
	 * @return
	 */
	static String splitCamelCase(String stringToSplit) {
		return stringToSplit.replaceAll(String.format("%s|%s|%s",
				"(?<=[A-Z])(?=[A-Z][a-z])", "(?<=[^A-Z])(?=[A-Z])",
				"(?<=[A-Za-z])(?=[^A-Za-z])"), " ");
	}

	public String getInsisHost() {
		return insisHost;
	}

	public void setInsisHost(String insisHost) {
		this.insisHost = insisHost;
	}

	public String getInsisPort() {
		return insisPort;
	}

	public void setInsisPort(String insisPort) {
		this.insisPort = insisPort;
	}

	public String getDataBaseName() {
		return dataBaseName;
	}

	public void setDataBaseName(String dataBaseName) {
		this.dataBaseName = dataBaseName;
	}

	public String getInsisUser() {
		return insisUser;
	}

	public void setInsisUser(String insisUser) {
		this.insisUser = insisUser;
	}

	public String getInsisPassword() {
		return insisPass;
	}

	public void setInsisPass(String insisPass) {
		this.insisPass = insisPass;
	}

}