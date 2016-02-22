package com.levins.webportal.certificate.data;

public class CertificateInfo {
	private String userName;
	private String firstName;
	private String lastName;
	private String password;
	private String email;
	private String pathToCertificateFile;
	private String egn;

	/**
	 * 
	 * @param userName
	 * @param firsName
	 * @param lastName
	 * @param email
	 */
	public CertificateInfo(String userName, String firsName, String lastName,
			String email) {
		this.userName = userName;
		this.firstName = firsName;
		this.lastName = lastName;
		this.email = email;
	}

	/**
	 * 
	 * @param userName
	 * @param firsName
	 * @param lastName
	 * @param email
	 * @param password
	 * @param path
	 * @param egn
	 */
	public CertificateInfo(String userName, String firsName, String lastName,String email,String password,  String path, String egn) {
		this.userName = userName;
		this.firstName = firsName;
		this.lastName = lastName;
		this.password = password;
		this.email = email;
		this.pathToCertificateFile = path;
		this.egn = egn;
	}

	public String getUserName() {
		return userName;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	// Return password like String to save into file
	public String getPassword() {
		return String.valueOf(password);
	}

	public String getEmail() {
		return email;
	}

	public String getPathToCertificateFile() {
		return pathToCertificateFile;
	}

	public void setPathToCertificateFile(String pathToCertificateFile) {
		this.pathToCertificateFile = pathToCertificateFile;
	}

	public String getEgn() {
		return egn;
	}

	public void setEgn(String egn) {
		this.egn = egn;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String toString() {
		return String.format("%s;%s;%s;%s;%s;%s;%s", userName, firstName,
				lastName, email, password, pathToCertificateFile, egn);
	}


	@Override
	public boolean equals(Object other) {
		if (!(other instanceof CertificateInfo)) {
			return false;
		}
		CertificateInfo that = (CertificateInfo) other;
		return this.getUserName().equals(that.getUserName())
				&& this.getFirstName().equals(that.getFirstName())
				&& this.getLastName().equals(that.getLastName());
	}

	@Override
	public int hashCode() {
		int hashCode = 1;
		hashCode = hashCode * 37 + this.getUserName().hashCode();
		hashCode = hashCode * 37 + this.getFirstName().hashCode();
		hashCode = hashCode * 37 + this.getLastName().hashCode();
		return hashCode;
	}
}
