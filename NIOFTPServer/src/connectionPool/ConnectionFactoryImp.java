package connectionPool;

import java.sql.Connection;

public class ConnectionFactoryImp implements ConnectionFactory{
	ConnectionPool connectionPool;
	
	@Override
	public Connection getConnection() {
		// TODO Auto-generated method stub
		return connectionPool.getConnection();
	}

	@Override
	public void destory() {
		// TODO Auto-generated method stub
		connectionPool.close();
	}

}
