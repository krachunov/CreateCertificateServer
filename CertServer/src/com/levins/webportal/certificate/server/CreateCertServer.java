package com.levins.webportal.certificate.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.levins.webportal.certificate.data.DateCreator;
import com.levins.webportal.certificate.data.ErrorLog;

public class CreateCertServer extends Thread {

	private final static String FILE_TO_LOAD_SETTINGS = "serverLog.log";
	public static final int LISTENING_PORT = 3333;
	final static String STAR_SERVER_MESSAGE = "Server started listening on TCP port ";
	final static String GREETING_MESSAGE_TO_CLIENT = "You are connected to server.\n";

	@Override
	public void run() {
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(LISTENING_PORT);
			System.out.println(STAR_SERVER_MESSAGE + LISTENING_PORT);
			while (true) {

				Socket socket = serverSocket.accept();

				createLogFile(socket);

				CertificateCreateThread certificateCreateClientThread = new CertificateCreateThread(
						socket);
				certificateCreateClientThread.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				serverSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void createLogFile(Socket socket) {
		ErrorLog log = new ErrorLog();
		String header = "IP;Date";
		DateCreator dateCreate = new DateCreator();
		String connectionDate = dateCreate.createdDateAndTime();
		String skippedUser = socket.getRemoteSocketAddress() + ";"
				+ connectionDate;
		log.createLog(FILE_TO_LOAD_SETTINGS, header, skippedUser);
	}

}
