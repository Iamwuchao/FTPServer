package selector;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.util.Iterator;

/*
 * 负责接收连接请求
 */
public class AcceptSelector extends FTPSelector {
	//private final ProcessSelector processSelector;
	
	public AcceptSelector() throws IOException {
		super();
	}

	public Iterator<SelectionKey> select() {
		// TODO Auto-generated method stub
		int readynums=0;
		try {
				readynums = selector.selectNow();
		} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
		if(readynums==0) return null;
		Iterator<SelectionKey> it = selector.selectedKeys().iterator();
		return it;
	}
	
	
	/*
	 * 关闭selector 关闭processSelector
	 * @see selector.FTPSelector#stop()
	 */
	public void stop() throws IOException{
		super.stop();
	}

}
