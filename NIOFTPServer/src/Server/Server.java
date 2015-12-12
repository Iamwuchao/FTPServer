package Server;

import java.io.IOException;
import connector.Connector;

public class Server {
	private Connector connector;
	public Server() throws IOException{
		connector = new Connector();
	}
	public void start() throws IOException
	{
		connector.start();
	}
	
	public void stop() throws IOException
	{
		connector.stop();
	}
	
	public void reload(){
		
	}
	
	public void addModel()
	{
		
	}
}
