package util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Stack;

public class CopyDirectory {
	private int options;
	private CopyDirectory(final File sourceFile,final File targetFile,CopyOption... options) throws Exception{
		if(sourceFile == null || targetFile == null){
			throw new NullPointerException();
		}
		if(!sourceFile.exists()){
			throw new FileNotFoundException("sourceFile not found");
		}
		if(!targetFile.exists()){
			if(!targetFile.mkdirs())
				throw new FileNotFoundException("create targetFile fail");
		}
		this.options=0;
		for(CopyOption tem:options){
			this.options|=(1<<tem.ordinal());
		}
	}
	
	private void DFSCopyDirectory(File sourceDirFile,File targetFile) {
		String targetCurrentDir = targetFile.getAbsolutePath().replace('\\','/');
		boolean atomicCopy = false;
		Stack<File> stack = null; 
		if((this.options&(1<<CopyOption.ATOMIC_MOVE.ordinal()))>0){
			atomicCopy = true;
		}
		if(atomicCopy){
			stack = new Stack<File>();
		}
		File[] files = sourceDirFile.listFiles();
		for(File temFile:files){
			File target = new File(targetCurrentDir+"/"+temFile.getName());
			if(!target.exists()||(this.options&(1<<CopyOption.REPLACE_EXISTING.ordinal()))>0){
				if(temFile.isDirectory()){
					if(target.mkdirs()){
						if(atomicCopy) stack.add(target);
						DFSCopyDirectory(temFile,target);
					}
				}
				else if(temFile.isFile()){
					try{
						if(atomicCopy) stack.add(target);
						CopyFile.copyFile(temFile,target);
					}catch(Exception e){
						if(atomicCopy){
							recovery(stack);
						}
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	private void recovery(Stack<File> stack){
		File file=null;
		while(stack!=null&&!stack.isEmpty())
		{
			file = stack.pop();
			file.delete();
		}
	}
	
	public static void copyDirectory(final File sourceDir,final File targetDir,CopyOption... options) throws Exception{
		CopyDirectory cd = new CopyDirectory(sourceDir,targetDir,options);
		cd.DFSCopyDirectory(sourceDir, targetDir);
	}
	
}
