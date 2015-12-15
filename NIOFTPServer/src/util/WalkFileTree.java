package util;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import java.util.Queue;

public class WalkFileTree {
	private Queue<File> filesQueue = new LinkedList<File>();
	
	private WalkFileTree(){
		
	}
	
	private void dfsWalkFileTree(File rootFile,boolean addDirectory){
		if(rootFile.isDirectory())
		{
			if(addDirectory)
			{
				filesQueue.add(rootFile);
			}
			File[] list = rootFile.listFiles();
			for(File file:list)
			{
				dfsWalkFileTree(file,addDirectory);
			}
		}
		else
		{
			filesQueue.add(rootFile);
		}
	}
	
	
	
	public static Queue<File> visitFileTree(File rootFile,boolean addDirectory) throws IOException
	{
		WalkFileTree wft = new WalkFileTree();
		wft.dfsWalkFileTree(rootFile,addDirectory);
		return wft.filesQueue;
	}
}
