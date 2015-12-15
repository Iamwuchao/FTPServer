package Server;

import java.io.IOException;
import connector.FtpConnector;

public class FtpServer {
	private FtpConnector connector;
	
	public FtpServer() throws IOException{
		connector = FtpConnector.open();
	}
	
	//启动ftp服务器
	public void start() throws IOException
	{
		connector.start();
	}
	
	//关闭ftp服务器
	public void stop() throws IOException
	{
		connector.stop();
	}
	
	//重启ftp服务器
	public void reStart(){
		
	}
	
	//添加服务模块
	public void addServiceModel()
	{
		
	}
}
