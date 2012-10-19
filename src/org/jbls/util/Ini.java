package org.jbls.util;
import java.io.*;

public class Ini{
	public static String ReadIni(String FN,String Header,String Setting,String Default){
		boolean fndHdr=false;
		String var="";
		createIfNotExist(FN);
		try{
			BufferedReader inputStream = new BufferedReader(new FileReader(FN));
			while ((var = inputStream.readLine()) != null){
				if (var.toLowerCase().equals("[" + Header.toLowerCase() + "]"))
					fndHdr=true;
				else if (fndHdr==true && var.substring(0, 1).equals("[")==true) 
					break; 
				else if( var.length() >= Setting.length()+1 && fndHdr==true && var.toLowerCase().substring(0, Setting.length()).equals(Setting.toLowerCase()))
					return (var.length() == Setting.length()+1) ? Default : var.substring(Setting.length()+1,var.length()); 
			}
		}catch(FileNotFoundException e){
		}catch(IOException ex){}
		return Default;
	}

	public static void WriteIni(String FN,String Header,String Setting,String Value){
		StringBuffer StringB = new StringBuffer();
		String tmpFile;
		String var="";
		boolean doneIt=false;
		boolean fndHdr=false;
		createIfNotExist(FN);
		try{
			BufferedReader inputStream = new BufferedReader(new FileReader(FN));
			while ((var = inputStream.readLine()) != null){
				if (fndHdr==false) StringB.append(var + "\n");
				else if ((var.length() >= Setting.length()+1) && (var.toLowerCase().substring(0, Setting.length()+1).equals(Setting.toLowerCase() +"=")))
					fndHdr=false;
				else
					StringB.append(var + "\n");
				if (var.toLowerCase().equals("[" + Header.toLowerCase() + "]")){
					doneIt=true; 
					fndHdr=true; 
					StringB.append(Setting + "=" + Value + "\n");
				}
			}
			inputStream.close();
			if (doneIt==false) StringB.append("[" + Header +"]\n" + Setting +"="+ Value +"\n");
	
			tmpFile=StringB.toString ();
			RandomAccessFile outp = new RandomAccessFile(FN, "rw");
			outp.writeBytes(tmpFile);
			outp.close();
		}catch(FileNotFoundException e){
		}catch(IOException ex){}
	}

    private static void createIfNotExist(String fn){
    	File file = new File(fn);
    	try{
    		if (!file.exists()) file.createNewFile();	
    	}catch(IOException e){}
    }
    
}