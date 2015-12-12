package main;

import java.io.IOException;

import Server.Server;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Server server;
		try {
			server = new Server();
			server.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
