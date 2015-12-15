package process;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import selector.ProcessSelector;

public class FTPProcess {
	private final ProcessSelector processSelector;
	//线程池
	private final ExecutorService threadPool;
	
	//将每个ftp请求封装为一个 work，交由线程池处理
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
	
	public FTPProcess() throws IOException
	{
		processSelector = new ProcessSelector();
		threadPool = Executors.newFixedThreadPool(5);
	}
	
	public boolean register(SocketChannel socketChannel,int ops)
	{
		try {
			processSelector.register(socketChannel, ops);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
		
	}
}
