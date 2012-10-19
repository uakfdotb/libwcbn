package net.clanent.wc3.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;


public class Util {
	public static InetAddress getLoopbackAddress() {
		try {
			return InetAddress.getLocalHost();
		} catch(UnknownHostException uhe) {
			System.out.println("[Util] Failed to get localhost address: " + uhe.getLocalizedMessage());
			return null;
		}
	}
	
	public static byte[] getTerminatedArray(ByteBuffer buf, int length) {
		int start = buf.position();
		int end;
		
		for(end = start; end < start + length; end++) {
			if(buf.get() == 0) break;
		}
		
		byte[] bytes = new byte[end - start]; //don't include terminator
		buf.position(start);
		buf.get(bytes);
		
		//position ByteBuffer correctly
		buf.position(end + 1); //skip terminator
		
		return bytes;
	}
	
	public static String getTerminatedString(ByteBuffer buf, int length) {
		return new String(getTerminatedArray(buf, length));
	}
	
	public static String toStr(byte[] bytes) {
		StringBuilder build = new StringBuilder(bytes.length);
		
		for(int i = 0; i < bytes.length; i++) {
			build.append((char) bytes[i]);
		}
		
		return build.toString();
	}

	public static byte[] toBytes(String str) {
		byte[] bytes = new byte[str.length()];

		for(int i = 0; i < bytes.length; i++) {
			bytes[i] = (byte) str.charAt(i);
		}

		return bytes;
	}
	
	public static byte[] toBytes(short data) {
		return new byte[] {
			(byte)((data >> 0) & 0xff),
			(byte)((data >> 8) & 0xff),
			};
	}
	
	public static byte[] toBytes(int data) {
		return new byte[] {
			(byte)((data >> 0) & 0xff),
			(byte)((data >> 8) & 0xff),
			(byte)((data >> 16) & 0xff),
			(byte)((data >> 24) & 0xff),
		};
	}
	
	public static int toInt(byte[] bytes) {
		if (bytes == null || bytes.length != 4) return 0x0;
		return (
			(0xff & bytes[0]) << 0 |
			(0xff & bytes[1]) << 8 |
			(0xff & bytes[2]) << 16 |
			(0xff & bytes[3]) << 24
			);
	}
	
	public static short toShort(byte[] bytes) {
		if (bytes == null || bytes.length != 2) return 0x0;
		return (short)(
				(0xff & bytes[0]) << 0   |
				(0xff & bytes[1]) << 8
				);
	}

	static final String HEXES = "0123456789ABCDEF";

	public static String getHex(byte [] raw) {
		if ( raw == null ) {
			return null;
		}

		final StringBuilder hex = new StringBuilder( 2 * raw.length );

		for ( final byte b : raw ) {
			hex.append(HEXES.charAt((b & 0xF0) >> 4))
					.append(HEXES.charAt((b & 0x0F)));
		}
		
		return hex.toString();
	}
	
	public static byte[] hexToBytes(String s) {
		int len = s.length();
		
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len - 1; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
								 + Character.digit(s.charAt(i+1), 16));
		}
		
		return data;
	}
	
	public static long unsignedInt(int x) {
		byte[] array = toBytes(x); //this actually reverses the int, we'll have to unreverse
		int b1 = (0x000000FF & ((int)array[3]));
		int b2 = (0x000000FF & ((int)array[2]));
		int b3 = (0x000000FF & ((int)array[1]));
		int b4 = (0x000000FF & ((int)array[0]));
		return ((long) (b1 << 24
						| b2 << 16
						| b3 << 8
						| b4))
						& 0xFFFFFFFFL;
	}

	public static int unsignedShort(short s) {
		byte[] array = toBytes(s); //this actually reverses the short, we'll have to unreverse
		int b1 = (0x000000FF & ((int) array[1]));
		int b2 = (0x000000FF & ((int) array[0]));
		return (b1 << 8 | b2);
	}

	public static int unsignedByte(byte b) {
		return (0x000000FF & ((int)b));
	}

	public static byte[] readFile(File f) {
		try {
			byte[] data = new byte[(int) f.length()];
			FileInputStream in = new FileInputStream(f);

			int i = 0;
			while(i < data.length) {
				int len = in.read(data, i, data.length - i);

				if(len == -1) {
					System.out.println("[Util] Warning: may not have read fully on " + f.getAbsolutePath());
					break;
				}
			}

			return data;
		} catch(IOException ioe) {
			System.out.println("[Util] Error while reading " + f.getAbsolutePath() + ": " + ioe.getLocalizedMessage());
			return new byte[] {};
		}
	}
	
	public static MessageDigest getSha1Digest() {
        try {
            return MessageDigest.getInstance("SHA-1");
        } catch(NoSuchAlgorithmException e) {
            System.out.println("[Util] Fatal error: SHA-1 not available");
			return null;
        }
	}
	
	public static byte[] getSha1Result(byte[] bytes) {
		MessageDigest sha1 = getSha1Digest();
		return sha1.digest(bytes);
	}
	
	public static String getSha1Result(String str) {
		byte[] result = getSha1Result(toBytes(str));
		return getHex(result);
	}

	public static String readTerminatedString(InputStream in) {
		try {
			StringBuilder build = new StringBuilder();

			int b;
			while((b = in.read()) != 0 && b != -1) {
				build.append((char) b);
			}

			return build.toString();
		} catch(IOException ioe) {
			System.out.println("[Util] Error: unable to read from input stream");
			return null;
		}
	}
	
	public static byte[] bytesFromNumberString(String str) {
		String[] parts = str.split(" ");
		byte[] bytes = new byte[parts.length];
		
		for(int i = 0; i < bytes.length; i++) {
			bytes[i] = (byte) Integer.parseInt(parts[i]);
		}
		
		return bytes;
	}
	
	public static ByteBuffer decodeStatString( byte[] data )
	{
		byte Mask = 0;
		ByteBuffer result = ByteBuffer.allocate(data.length);

		for(int i = 0; i < data.length; i++)
		{
			if( ( i % 8 ) == 0 )
				Mask = data[i];
			else
			{
				if( ( Mask & ( 1 << ( i % 8 ) ) ) == 0 )
					result.put( (byte) (data[i] - 1) );
				else
					result.put( data[i] );
			}
		}
		
		return result;
	}
	
	public static void reverse(byte[] data) {
		for(int i = 0; i < data.length / 2; i++) {
			byte tmp = data[i];
			data[i] = data[data.length - 1 -  i];
			data[data.length - 1 - i] = tmp;
		}
	}
	
	public static String ipString(byte[] ip) {
		if(ip.length < 4) return null;
		else return unsignedByte(ip[0]) + "." +
			unsignedByte(ip[1]) + "." +
			unsignedByte(ip[2]) + "." +
			unsignedByte(ip[3]);
	}
	
	public static int parseInt(String str) {
		try {
			return Integer.parseInt(str);
		} catch(NumberFormatException e) {
			return 0;
		}
	}
	
	public static SecureRandom RANDOM = new SecureRandom();
	public static byte[] randomBytes(int num) {
		byte[] bytes = new byte[num];
		RANDOM.nextBytes(bytes);
		return bytes;
	}
}
