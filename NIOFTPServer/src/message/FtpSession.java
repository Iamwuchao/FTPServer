package message;

import java.nio.file.Path;

import security.User;

/*
 * 用于记录用户访问的上下文
 */
public class FtpSession {
	private Path rootPath;
	private Path currentPath;
	private User user;
	private Request request;
	private Response response;
	private final Object responseLock = new Object();
	private final Object requestLock = new Object();
	public FtpSession(){
		rootPath = null;
		user = null;
	}
	public FtpSession(Path rootPath,User user)
	{
		if(rootPath == null)
		{
			throw new NullPointerException("rootPath is null");
		}
		if(user == null)
		{
			throw new NullPointerException("user is null");
		}
		this.rootPath = rootPath;
		this.currentPath = rootPath;
		this.user = user;
	}
	
	public Request getRequest(){
		return this.request;
	}
	
	public void setResponse(Response response){
		synchronized(responseLock){
			this.response = response;
		}
	}
	
	public Response getAndClearResponse(){
		synchronized(responseLock){
			Response temresponse = this.response;
			this.response = null;
			return temresponse;
		}
	}
	
	public void setRequest(Request request){
		synchronized(requestLock){
			this.request = request;
		}
	}
	
	public void clearRequest(){
		this.request = null;
	}
	
	public void setcurrentPath(Path path)
	{
		this.currentPath = path;
	}
	
	public Path getCurrentPath()
	{
		return this.currentPath;
	}
	
	public Path getRootPath()
	{
		return this.rootPath;
	}
	
	public User getUser()
	{
		return this.user;
	}
	
	public void close(){
		
	}
}
