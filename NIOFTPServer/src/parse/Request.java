package parse;

public class Request {
	private final String command;
	private final String requestSource;
	public Request(String command,String requestSource)
	{
		this.command = command;
		this.requestSource = requestSource;
	}
	
	public String getRequestSource() {
		return requestSource;
	}

	public String getCommand() {
		return command;
	}	
}
