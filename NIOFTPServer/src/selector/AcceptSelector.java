package selector;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.util.Iterator;

public class AcceptSelector extends FTPSelector {
	private final ProcessSelector processSelector;
	
	public AcceptSelector(ProcessSelector processSelector) throws IOException {
		super();
		// TODO Auto-generated constructor stub
		this.processSelector = processSelector;
	}

	
	class Work implements Runnable{
		Iterator it;
		protected Work(Iterator it){
			this.it = it;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(it.hasNext()){
				SelectionKey key = (SelectionKey) it.next();
				if(key.isAcceptable()){
					try {
						processSelector.register(key.channel(),SelectionKey.OP_READ);
					} catch (ClosedChannelException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		int readynums=0;
		while(!isClose){
			try {
				readynums = selector.selectNow();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(readynums==0) continue;
			Iterator it = selector.selectedKeys().iterator();
			process(it);
			readynums=0;
		}
	}
	
	public void start(){
		Thread thread = new Thread(this);
		thread.start();
	}
	
	protected void process(Iterator it){
		Work work = new Work(it);
		threadPool.execute(work);
	}
}
