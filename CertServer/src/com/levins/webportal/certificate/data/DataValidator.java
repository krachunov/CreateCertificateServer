package com.levins.webportal.certificate.data;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataValidator {
	/**
	 * http://www.mkyong.com/regular-expressions/how-to-validate-email-address-
	 * with-regular-expression/
	 */
	private static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern
			.compile(
					"^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$",
					Pattern.CASE_INSENSITIVE);

	/**
	 * 
	 * @param emailStr
	 * @return true if mail is valid or false if isn't
	 */
	public static boolean validateMail(String emailStr) {
		Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
		return matcher.find();
	}

	public static boolean chekFileExist(String fileName) {
		File file = new File(fileName);
		if (file.exists() && !file.isDirectory()) {
			return true;
		}
		return false;
	}

	public String removeWhitespace(String string) {
		Pattern trimmer = Pattern.compile("^\\s+|\\s+$");
		Matcher m = trimmer.matcher(string);
		StringBuffer out = new StringBuffer();
		while (m.find())
			m.appendReplacement(out, "");
		m.appendTail(out);
		return out.toString();
	}
}
