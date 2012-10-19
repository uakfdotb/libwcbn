package net.clanent.wc3.util;
/**
 * A BufferedReader that prevents DoS attacks by providing bounds for line length and number of lines
 * @author Sean Malone <sean@seantmalone.com>
 * @version 1.0
 */

import java.io.*;

public class BoundedBufferedReader extends BufferedReader 
{
	private static final int DEFAULT_MAX_LINE_LENGTH = 1024;	//Max bytes per line
	
	private int readerMaxLineLen;
	private int currentLine = 1;
	
	public BoundedBufferedReader(Reader reader, int maxLineLen) 
	{
		super(reader);
		if (maxLineLen<=0) throw new IllegalArgumentException("BoundedBufferedReader - maxLines and maxLineLen must be greater than 0");
		readerMaxLineLen = maxLineLen;
	}
	
	public BoundedBufferedReader(FileReader reader) 
	{
		super(reader);
		readerMaxLineLen = DEFAULT_MAX_LINE_LENGTH;
	}
	
	public String readLine() throws IOException 
	{
		//Check readerMaxLines limit
		currentLine++;
		
		int currentPos=0;
		char[] data=new char[readerMaxLineLen];
		final int CR = 13;
		final int LF = 10;
		int currentCharVal=super.read();
		
		//Read characters and add them to the data buffer until we hit the end of a line or the end of the file.
		while( (currentCharVal!=CR) && (currentCharVal!=LF) && (currentCharVal>=0)) 
		{
			data[currentPos++]=(char) currentCharVal;
			//Check readerMaxLineLen limit
			if (currentPos<readerMaxLineLen) 
				currentCharVal = super.read();
			else
				break;
		}
		
		if (currentCharVal<0)
			//End of file
			return null;
		else
		{       
			//Remove newline characters from the buffer
			if(currentCharVal==CR) 
			{
				//Check for LF and remove from buffer
				super.mark(1);
				if (super.read() != LF) 
					super.reset();
			} 
			else if(currentCharVal!=LF)
			{
				//readerMaxLineLen has been hit, but we still need to remove newline characters.
				super.mark(1);
				int nextCharVal = super.read();
				if (nextCharVal==CR) 
				{
					super.mark(1);
					if (super.read() != LF) 
						super.reset();  
				} 
				else if (nextCharVal!=LF) 
					super.reset();
			}
			return(new String(data,0,currentPos));
		}
		
	}
}