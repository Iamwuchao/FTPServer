package selector;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
 * FTP服务器 selector抽象类
 */

public abstract class FTPSelector implements Runnable{
	protected Selector selector;
	protected volatile boolean isClose;
	protected final Object statusLock;
	protected final Object selectorLock;
	protected final ExecutorService threadPool;
	public FTPSelector() throws IOException{
		selector = Selector.open();
		statusLock = new Object();
		selectorLock = new Object();
		threadPool = Executors.newFixedThreadPool(5);
		isClose = false;
	}
	
	public void stop() throws IOException
	{	if(isClose) return;
		synchronized(statusLock){
			isClose = true;
		}
		synchronized(selectorLock){
			selector.close();
		}
	}
	
	public boolean register(SelectableChannel channel,int ops) throws ClosedChannelException{
		if(channel == null){
			throw new NullPointerException();
		}
		if(isClose) return false;
		synchronized(selectorLock){
			if(selector.isOpen())
			{
				channel.register(selector, ops);
				return true;
			}
			else
			{
				return false;
			}
		}
	}
	
	protected abstract void process(Iterator it);
}
