package connector;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import process.FTPProcess;
import selector.AcceptSelector;

public class FtpConnector implements Runnable{
	private final ServerSocketChannel serverCommandChannel;
	private final AcceptSelector acceptSelector;
	private final FTPProcess ftpProcess;
	private volatile boolean isClosed;
	//状态锁 用于控制isClose
	protected final Object statusLock;
	
	private FtpConnector() throws IOException
	{
		serverCommandChannel =  ServerSocketChannel.open();
		serverCommandChannel.bind(new InetSocketAddress(21));
		acceptSelector = new AcceptSelector();
		acceptSelector.register(serverCommandChannel, SelectionKey.OP_ACCEPT);
		ftpProcess = new FTPProcess();
		isClosed = false;
		statusLock = new Object();
	}
	
	public static FtpConnector open() throws IOException{
		return new FtpConnector();
	}
	
	public void start(){
		Thread thread = new Thread(this);
		thread.start();
	}
	
	public void stop() throws IOException{
		acceptSelector.stop();
		serverCommandChannel.close();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(!isClosed)
		{
			synchronized(statusLock){
				if(!isClosed)
				{
					Iterator<SelectionKey> it = acceptSelector.select();
					process(it);
				}
			}
		}
	}
	
	/*
	 * 使用线程池处理 ready的Channel
	 * @see selector.FTPSelector#process(java.util.Iterator)
	 */
	protected void process(Iterator<SelectionKey> it){
		while(it.hasNext())
		{
			SelectionKey key = it.next(); 
			if(key.isAcceptable())
			{
				ftpProcess.register((SocketChannel) key.channel(),SelectionKey.OP_READ);
			}
		}
	}
}
