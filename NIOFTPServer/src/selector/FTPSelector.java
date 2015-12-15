package selector;

import java.io.IOException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;

/*
 * FTP服务器 selector抽象类
 */

public abstract class FTPSelector{
	protected Selector selector;
	protected volatile boolean isClose;
	
	//状态锁 用于控制isClose
	protected final Object statusLock;
	
	
	public FTPSelector() throws IOException{
		selector = Selector.open();
		statusLock = new Object();
		isClose = false;
	}
	
	public void stop() throws IOException
	{	
		synchronized(statusLock){
			isClose = true;
			selector.close();
		}
	}
	
	public boolean register(SelectableChannel channel,int ops) throws IOException{
		if(channel == null){
			throw new NullPointerException();
		}
		if(channel.isBlocking()){
			channel.configureBlocking(true);
		}
		synchronized(statusLock){
			if(isClose) return false;
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
	
	public abstract Iterator<SelectionKey> select();
}
