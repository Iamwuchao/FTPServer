package process;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import security.FtpContext;
import selector.ProcessSelector;

public class FTPProcess implements Runnable{
	private final ProcessSelector processSelector;
	
	//线程池
	private final ExecutorService threadPool;
	private final static int MAX_Thread = 5;
	//状态锁
	private final Object statusLock;
	
	//是否已经关闭
	private volatile boolean isClosed = true;
	
	//将每个ftp请求封装为一个 work，交由线程池处理
	class Work implements Runnable{
		SocketChannel socketChannel;
		FtpContext context;
		protected Work(SocketChannel channel,FtpContext context){
			this.socketChannel = channel;
			this.context = context;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			
		}
	}
	
	public FTPProcess() throws IOException
	{
		processSelector = new ProcessSelector();
		threadPool = Executors.newFixedThreadPool(MAX_Thread);
		statusLock = new Object();
		
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

	private void process(Iterator<SelectionKey> it)
	{
		while(it.hasNext())
		{
			
		}
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(!isClosed)
		{
			synchronized(statusLock){
				if(!isClosed)
				{
					Iterator<SelectionKey> it = processSelector.select();
					process(it);
				}
			}
		}
	}
}
