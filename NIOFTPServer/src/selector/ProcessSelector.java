package selector;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProcessSelector extends FTPSelector{
	//线程池
	private final ExecutorService threadPool;
	public ProcessSelector() throws IOException {
		super();
		threadPool = Executors.newFixedThreadPool(5);
	}

	class Work implements Runnable{
		SelectionKey key;
		protected Work(SelectionKey key){
			this.key = key;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			
		}
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void process(Iterator it) {
		// TODO Auto-generated method stub
		
	}
	
	public void stop() throws IOException{
		super.stop();
		threadPool.shutdown();
	}
}
