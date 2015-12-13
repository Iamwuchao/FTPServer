package selector;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.util.Iterator;

/*
 * 负责接收连接请求
 */
public class AcceptSelector extends FTPSelector {
	private final ProcessSelector processSelector;
	
	public AcceptSelector(ProcessSelector processSelector) throws IOException {
		super();
		// TODO Auto-generated constructor stub
		this.processSelector = processSelector;
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
	
	/*
	 * 关闭selector 关闭processSelector
	 * @see selector.FTPSelector#stop()
	 */
	public void stop() throws IOException{
		processSelector.stop();
		super.stop();
	}
	
	
	/*
	 * 使用线程池处理 ready的Channel
	 * @see selector.FTPSelector#process(java.util.Iterator)
	 */
	protected void process(Iterator it){
		while(it.hasNext())
		{
			SelectionKey key = (SelectionKey)it.next(); 
			
		}
	}
}
