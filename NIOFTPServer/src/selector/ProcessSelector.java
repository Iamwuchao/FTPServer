package selector;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.util.Iterator;

public class ProcessSelector extends FTPSelector{
	
	public ProcessSelector() throws IOException {
		super();
	}
	
	public void stop() throws IOException{
		super.stop();
	}



	@Override
	public Iterator<SelectionKey> select() {
		// TODO Auto-generated method stub
		return null;
	}
}
