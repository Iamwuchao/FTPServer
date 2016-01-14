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
		serverCommandChannel.configureBlocking(false);
		acceptSelector = new AcceptSelector();
		acceptSelector.register(serverCommandChannel, SelectionKey.OP_ACCEPT);
		ftpProcess = new FTPProcess();
		isClosed = false;
		statusLock = new Object();
		System.out.println("FTP Connector init success");
	}
	
	public static FtpConnector open() throws IOException{
		return new FtpConnector();
	}
	
	public void start(){
		Thread thread = new Thread(this);
		thread.start();
		ftpProcess.start();
		System.out.println("FTP Connector start");
	}
	
	public void stop() throws IOException{
		isClosed = true;
		acceptSelector.stop();
		serverCommandChannel.close();
		ftpProcess.stop();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(!isClosed)
		{
			synchronized(statusLock){
				System.out.println("connector " + acceptSelector.getSize());
				if(!isClosed)
				{
					Iterator<SelectionKey> it = acceptSelector.select();
					if(it!=null)
					{	
						System.out.println("accept");
						process(it);
					}
				}
				try {
				//	System.out.println("process");
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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
				ServerSocketChannel serverSocket =  (ServerSocketChannel) key.channel();
				SocketChannel socketChannel=null;
				try {
					socketChannel = serverSocket.accept();
					socketChannel.socket().setSoTimeout(1000);
					System.out.println(socketChannel.getRemoteAddress().toString());
					ftpProcess.register(socketChannel,SelectionKey.OP_READ|SelectionKey.OP_WRITE);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
