package connectionPool;

import java.sql.Connection;

public class ConnectionFactoryImp implements ConnectionFactory{
	private final String url;
	private final String userName;
	private final String password;
	
	ConnectionFactoryImp(String url,String userName,String password){
		this.url = url;
		this.password = password;
		this.userName = userName;
	}
	
	@Override
	public Connection getConnection(ConnectionPool pool) {
		// TODO Auto-generated method stub
		return null;
	}

}
