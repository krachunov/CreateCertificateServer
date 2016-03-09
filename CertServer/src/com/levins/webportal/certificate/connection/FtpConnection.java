package com.levins.webportal.certificate.connection;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class FtpConnection {
	private String host;
	private String port;
	private String user;
	private String password;

	public FtpConnection(String host, String port, String user, String password) {
		this.host = host;
		this.port = port;
		this.user = user;
		this.password = password;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void uploadToFtp(String... uploadFile) {
		JSch jsch = new JSch();
		Session session = null;
		try {
			session = jsch.getSession("root", "192.168.12.124", 22);
			session.setConfig("StrictHostKeyChecking", "no");
			session.setPassword("l3v1ns");
			session.connect();

			Channel channel = session.openChannel("sftp");
			channel.connect();
			ChannelSftp sftpChannel = (ChannelSftp) channel;
			sftpChannel.put("d:\\Book1.csv",
					"/srv/drupal/sites/default/files/webportal/Book1.csv");
			sftpChannel.exit();
			session.disconnect();
		} catch (JSchException e) {
			e.printStackTrace();
		} catch (SftpException e) {
			e.printStackTrace();
		}
	}

}
