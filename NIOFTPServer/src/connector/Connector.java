package connector;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;

import selector.AcceptSelector;
import selector.ProcessSelector;

public class Connector {
	private final ServerSocketChannel serverCommandChannel;
	private final AcceptSelector acceptSelector;
	private final ProcessSelector processSelector;
	public Connector() throws IOException
	{
		serverCommandChannel =  ServerSocketChannel.open();
		serverCommandChannel.bind(new InetSocketAddress(21));
		processSelector = new ProcessSelector();
		acceptSelector = new AcceptSelector(processSelector);
		acceptSelector.register(serverCommandChannel, SelectionKey.OP_ACCEPT);
	}
	
	public void start(){
		acceptSelector.start();
	}
	
	public void stop() throws IOException{
		acceptSelector.stop();
		serverCommandChannel.close();
	}
}
