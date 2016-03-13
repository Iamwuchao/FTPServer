package connectionPool;

import java.sql.Connection;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


public enum ConnectionPoolImpl implements ConnectionPool{
	CONNECTIONSPOOL;
	
	class DelayKey implements Delayed{
		private long time;
		private Connection connection;
		DelayKey(Connection con){
			time = System.currentTimeMillis()+SqlInfo.getTimeOut();
			this.connection = con;
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
	
	private  DelayQueue<DelayKey> delayQueue;
	private  ConcurrentLinkedQueue<Connection> connectionQueue;
	private AtomicInteger count;
	private ConnectionFactory confactory; 
	ConnectionPoolImpl(){
		delayQueue = new DelayQueue<DelayKey>();
		connectionQueue = new ConcurrentLinkedQueue<Connection>();
		confactory = new ConnectionFactoryImp("","","");
		final int min = SqlInfo.getMinSize();
		for(int i=0;i < min;i++){
			Connection con = confactory.getConnection(this);
			delayQueue.add(new DelayKey(con));
		}
	}
	
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
