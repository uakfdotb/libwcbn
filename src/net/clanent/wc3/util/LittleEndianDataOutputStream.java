//from Google's guava-libraries
package net.clanent.wc3.util;

import java.io.DataOutputStream;
import java.io.FilterOutputStream;
import java.io.DataOutput;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

public class LittleEndianDataOutputStream extends FilterOutputStream implements DataOutput {

  public LittleEndianDataOutputStream(OutputStream out) {
    super(new DataOutputStream(out));
  }

  public void write(byte[] b, int off, int len) throws IOException {
    // Override slow FilterOutputStream impl
    out.write(b, off, len);
  }

  public void writeBoolean(boolean v) throws IOException {
    ((DataOutputStream) out).writeBoolean(v);
  }

  public void writeByte(int v) throws IOException {
    ((DataOutputStream) out).writeByte(v);
  }
  
  public void writeBytes(String s) throws IOException {
    ((DataOutputStream) out).writeBytes(s);
  }

  public void writeChar(int v) throws IOException {
    writeShort(v);
  }

  public void writeChars(String s) throws IOException {
    for (int i = 0; i < s.length(); i++) {
      writeChar(s.charAt(i));
    }
  }

  public void writeFloat(float v) throws IOException {
    writeInt(Float.floatToIntBits(v));
  }

  public void writeInt(int v) throws IOException {
    out.write(0xFF & v);
    out.write(0xFF & (v >> 8));
    out.write(0xFF & (v >> 16));
    out.write(0xFF & (v >> 24));
  }

  public void writeShort(int v) throws IOException {
    out.write(0xFF & v);
    out.write(0xFF & (v >> 8));
  }

  public void writeUTF(String str) throws IOException {
    ((DataOutputStream) out).writeUTF(str);
  }
  
  public void writeLong(long v) throws IOException {
  	//unsupported
  }
  
  public void writeDouble(double v) throws IOException {
  	//unsupported
  }
  
  public void writeString(String str) throws IOException {
  	try {
  		write(str.getBytes("UTF-8"));
  	} catch(UnsupportedEncodingException e) {} //todo
  }
  
  public void writeTerminatedString(String str) throws IOException {
  	writeString(str);
  	writeByte(0);
  }
}
