package main;

import java.io.IOException;

import Server.FtpServer;

public class Main { 

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		FtpServer server;
		try {
			server = new FtpServer();
			server.start();
			System.out.println("FTPServer start");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
