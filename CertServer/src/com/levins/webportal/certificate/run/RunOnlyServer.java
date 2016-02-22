package com.levins.webportal.certificate.run;

import com.levins.webportal.certificate.server.CreateCertServer;

public class RunOnlyServer {

	public static void main(String[] args) {
		CreateCertServer server = new CreateCertServer();
		server.start();
	}

}
