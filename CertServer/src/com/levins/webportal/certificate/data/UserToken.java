package com.levins.webportal.certificate.data;

public enum UserToken {
	USERPORTAL_VALUE(0),
	FIRSTNAME_VALUE(1),
	LASTNAME_VALUE(2), 
	MAIL_VALUE(3),
	PASSWORD_VALUE(4),
	PATHTOCERT_VALUE(5),
	EGN_VALUE(6);
	
	private int index;

	private UserToken(int index) {
		this.index = index;
	}

	public int getIndex() {
		return index;
	}

	/**
	 * Return index on current column 
	 */
	public static int USERPORTAL = USERPORTAL_VALUE.getIndex();
	public static int FIRSTNAME = FIRSTNAME_VALUE.getIndex();
	public static int LASTNAME = LASTNAME_VALUE.getIndex();
	public static int MAIL = MAIL_VALUE.getIndex();
	public static int PASSWORD = PASSWORD_VALUE.getIndex();
	public static int PATHTOCERT = PATHTOCERT_VALUE.getIndex();
	public static int EGN = EGN_VALUE.getIndex();

}
