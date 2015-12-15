package util;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import java.nio.channels.FileChannel;

import java.nio.channels.FileLock;
import java.nio.file.FileAlreadyExistsException;

/*
 * 文件复制
 * 将原文件复制到目的文件
 */
public class CopyFile {
	private FileChannel sourceFileChannel;
	private FileChannel targetFileChannel;
	private RandomAccessFile fileInputStream;
	private RandomAccessFile fileOutputStream;
	private long fileLength;
	private int option;
	
	/*
	 * sourceFile 源文件
	 * targetFile 目标文件
	 * option 复制文件操作选项
	 */
	private CopyFile(final File sourceFile,final File targetFile,CopyOption... options) throws Exception
	{
		if(sourceFile==null||targetFile==null)
		{
			throw new NullPointerException();
		}
		for(CopyOption option:options){
			this.option=this.option|(1<<option.ordinal());
		}

		if(!sourceFile.exists())
		{
			throw new FileNotFoundException("source file not found");
		}
		if(targetFile.exists())
		{
			System.out.println(targetFile.getAbsolutePath());
			throw new FileAlreadyExistsException("target file exists");
		}
		fileInputStream = new RandomAccessFile(sourceFile,"rws");
		sourceFileChannel = fileInputStream.getChannel();
		fileOutputStream = new RandomAccessFile(targetFile,"rws");
		targetFileChannel = fileOutputStream.getChannel();
		
		
	}
	
	public static void copyFile(final File sourceFile,final File targetFile,CopyOption... options) throws Exception
	{
		CopyFile cbc = new CopyFile(sourceFile,targetFile,options);
		cbc.fileLength = sourceFile.length();
		FileLock lock = cbc.sourceFileChannel.lock();
		try{
			cbc.copyNormalFile();
		}catch(IOException e){
				throw new IOException("copy file IO error");
		}finally{
			if(lock!=null)lock.release();
			if(cbc.sourceFileChannel!=null) cbc.sourceFileChannel.close();
			if(cbc.targetFileChannel!=null) cbc.targetFileChannel.close();
			if(cbc.fileInputStream!=null) cbc.fileInputStream.close();
			if(cbc.fileOutputStream!=null) cbc.fileInputStream.close();
			System.out.println("end lock release");
		}
	}
	
	/*private void copyBigFile() throws IOException
	{
		long position=0;
		int dataChunk = (1<<MAX_BUFFER_SIZE)*BUFFERSIZE;
		byte[] data = new byte[dataChunk];
		while((position+dataChunk)<this.fileLength)
		{
			MappedByteBuffer sourceMbb = this.sourceFileChannel.map(MapMode.READ_ONLY,position,dataChunk);
			MappedByteBuffer targetMbb = this.targetFileChannel.map(MapMode.READ_WRITE, position,dataChunk);
			sourceMbb.get(data);
			targetMbb.put(data);
			position+=dataChunk;
			targetMbb.force();
		}
		if(position<this.fileLength)
		{
			int size = (int) (this.fileLength-position);
			byte[] buffer = new byte[size];
			MappedByteBuffer sourceMbb = this.sourceFileChannel.map(MapMode.READ_ONLY,position,size);
			MappedByteBuffer targetMbb = this.targetFileChannel.map(MapMode.READ_WRITE, position,size);
			sourceMbb.get(buffer);
			targetMbb.put(buffer);
			targetMbb.force();
		}
	}*/
	
	private void copyNormalFile() throws IOException{
		this.targetFileChannel.transferFrom(sourceFileChannel, 0, this.fileLength);
	}
	
}
