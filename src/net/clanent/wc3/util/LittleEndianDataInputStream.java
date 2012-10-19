package net.clanent.wc3.util;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class LittleEndianDataInputStream extends FilterInputStream implements DataInput {
  public LittleEndianDataInputStream(InputStream in) {
    super(new DataInputStream(in));
  }

  public String readLine() {
    throw new UnsupportedOperationException("readLine is not supported");
  }

  public void readFully(byte[] b) throws IOException {
    ((DataInputStream) in).readFully(b);
  }

  public void readFully(byte[] b, int off, int len) throws IOException {
    ((DataInputStream) in).readFully(b, off, len);
  }

  public int skipBytes(int n) throws IOException {
    return (int) in.skip(n);
  }

  public int readUnsignedByte() throws IOException {
    int b1 = in.read();
    if (0 > b1) {
      throw new EOFException("end of stream reached");
    }
    
    return b1;
  }
  
  public static final int byteArrayToInt(int b1, int b2, int b3, int b4) {
  	return (
		(0xff & b1) << 24 |
		(0xff & b2) << 16 |
		(0xff & b3) << 8 |
		(0xff & b4) << 0
		);
  }

  public int readUnsignedShort() throws IOException {
    byte b1 = readAndCheckByte();
    byte b2 = readAndCheckByte();
    return byteArrayToInt(0, 0, b2, b1);
  }

  public int readInt() throws IOException {
    byte b1 = readAndCheckByte();
    byte b2 = readAndCheckByte();
    byte b3 = readAndCheckByte();
    byte b4 = readAndCheckByte();

    return byteArrayToInt( b4, b3, b2, b1);
  }

  public long readLong() throws IOException {
    return 0; //unsupported
  }

  public float readFloat() throws IOException {
    return Float.intBitsToFloat(readInt());
  }
  
  public double readDouble() throws IOException {
    return 0; //unsupported
  }

  public String readUTF() throws IOException {
    return ((DataInputStream) in).readUTF();
  }
  
  public short readShort() throws IOException {
    return (short) readUnsignedShort();
  }

  public char readChar() throws IOException {
    return (char) readUnsignedShort();
  }

  public byte readByte() throws IOException {
    return (byte) readUnsignedByte();
  }

  public boolean readBoolean() throws IOException {
    return readUnsignedByte() != 0;
  }

  private byte readAndCheckByte() throws IOException, EOFException {
    int b1 = in.read();

    if (-1 == b1) {
      throw new EOFException();
    }

    return (byte) b1;
  }
}
