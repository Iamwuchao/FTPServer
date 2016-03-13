package connectionPool;

import java.sql.Connection;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

class DelayKey implements Delayed{
	private long time;
	private Connection connection;
	DelayKey(){
		time = System.currentTimeMillis()+SqlInfo.getTimeOut();
	}
	@Override
	public int compareTo(Delayed arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getDelay(TimeUnit arg0) {
		// TODO Auto-generated method stub
		long delay = time - System.currentTimeMillis();
		return delay;
	}
	
}
public enum ConnectionPoolImpl implements ConnectionPool{
	CONNECTIONSPOOL;
	
	private  DelayQueue<DelayKey> dealyQueue;
	private  ConcurrentLinkedQueue<Connection> connectionQueue;
	@Override
	public Connection getConnection() {
		// TODO Auto-generated method stub
		
		return null;
	}

	@Override
	public void giveBackConnection(Connection connection) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void Init() {
		// TODO Auto-generated method stub
		
	}
	
}
