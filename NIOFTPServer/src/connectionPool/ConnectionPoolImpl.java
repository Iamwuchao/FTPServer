package connectionPool;

import java.sql.Connection;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


public enum ConnectionPoolImpl implements ConnectionPool{
	CONNECTIONSPOOL;
	private int MIN_CONNECTION;
	private int MAX_CONNECTION;
	private int ADD_COUNT;
	private volatile boolean stop;
	private  DelayQueue<DelayKey> delayQueue;
	private  ConcurrentLinkedQueue<Connection> connectionQueue;
	private AtomicInteger count;
	private ConnectionFactory confactory; 
	private  ExecutorService checkThread;
	
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
	
	class CheckRunnable implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(!stop&&!delayQueue.isEmpty()){
				DelayKey key = delayQueue.poll();
				connectionQueue.remove(key.connection);
			}
		}
		
	}
	
	
	ConnectionPoolImpl(){
		Init();
	}
	
	@Override
	public Connection getConnection() {
		// TODO Auto-generated method stub
		
		return connectionQueue.poll();
	}

	@Override
	public void giveBackConnection(Connection connection) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		stop = false;
		delayQueue.clear();
		connectionQueue.clear();
		checkThread.shutdownNow();
	}
	
	private void Init() {
		// TODO Auto-generated method stub
		delayQueue = new DelayQueue<DelayKey>();
		connectionQueue = new ConcurrentLinkedQueue<Connection>();
		confactory = new ConnectionFactoryImp("","","");
		checkThread = Executors.newSingleThreadExecutor();
		MIN_CONNECTION = SqlInfo.getMinSize();
		MAX_CONNECTION = SqlInfo.getMaxSize();
		ADD_COUNT = SqlInfo.getAddCount();
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub
		stop=false;
		for(int i=0;i < MIN_CONNECTION;i++){
			Connection con = confactory.getConnection(this);
			delayQueue.add(new DelayKey(con));
		}
		checkThread.execute(new CheckRunnable());
	}

	public int getMIN_CONNECTION() {
		return MIN_CONNECTION;
	}

	public void setMIN_CONNECTION(int mIN_CONNECTION) {
		MIN_CONNECTION = mIN_CONNECTION;
	}

	public int getMAX_CONNECTION() {
		return MAX_CONNECTION;
	}

	public void setMAX_CONNECTION(int mAX_CONNECTION) {
		MAX_CONNECTION = mAX_CONNECTION;
	}

	public int getADD_COUNT() {
		return ADD_COUNT;
	}

	public void setADD_COUNT(int aDD_COUNT) {
		ADD_COUNT = aDD_COUNT;
	}

	@Override
	public boolean isClosed() {
		// TODO Auto-generated method stub
		return stop;
	}
	
	
}
