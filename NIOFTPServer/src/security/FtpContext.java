package security;

import java.nio.file.Path;

/*
 * 用于记录用户访问的上下文
 */
public class FtpContext {
	private final Path rootPath;
	private Path currentPath;
	private final User user;
	
	public FtpContext(Path rootPath,User user)
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
	
}
