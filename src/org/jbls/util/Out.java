/*
 * Created on Feb 20, 2005
 */
package org.jbls.util;

/**
 * @author FooL
 *
 * This class controls output from the server, specifying where it goes
 *
 * !!!All methods in this class are temp until I fill it out.
 *
 * Currently supports 4 "types" of output, but the system I use now sucks
 * and the formatting is ugly.
 */

public class Out {

    static
    {
        //System.out.println(new Date() + "");
    }

	/**
	 @param text: Text to be send to the output stream directly(no formatting)*/
	public static void print(String text)
	{
		System.out.print(text);
	}

	/**@param source: source of the info
	//@param text:text to show*/
	public static void println(String source, String text)
	{
		print("[" + source + "] " + text + "\n");
	}

	/**Displays errors
	//@param source: source of the info
	//@param text: text to show*/
	public static void error(String source, String text)
	{
		println(source, "Error: " + text);
	}

	/**Displays debug information, if wanted
	//@param source - source of the info
	//@param text -text to show*/
	public static void debug(String source, String text)
	{
		if(Constants.debugInfo)
			println(source, "Debug: " + text);
	}

	/**Displays "info"
	@param source - source of the info
	@param text -text to show*/
	public static void info(String source, String text)
	{
		println(source, text);
	}
}
