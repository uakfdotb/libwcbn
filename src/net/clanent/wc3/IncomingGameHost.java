package net.clanent.wc3;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import net.clanent.wc3.util.Util;

public class IncomingGameHost {
	static int NEXT_GAME_ID;

	public int gameType;
	public int parameter;
	public int languageID;
	public int port;
	public byte[] ip;
	public int status;
	public int elapsedTime;
	public String gameName;
	public int slotsTotal;
	public int hostCounter;
	public byte[] statString;
	public int uniqueGameID;
	public int receivedTime;

	// decoded from stat string:
	public int mapFlags;
	public int mapWidth;
	public int mapHeight;
	public int mapCRC;
	public String mapPath;
	public String hostName;

	public IncomingGameHost(int gameType, int parameter, int languageID, int port, byte[] ip,
			int status, int elapsedTime, String gameName, int slotsTotal, int hostCounter, byte[] statString ) {
		this.gameType = gameType;
		this.parameter = parameter;
		this.languageID = languageID;
		this.port = port;
		this.ip = ip;
		this.status = status;
		this.elapsedTime = elapsedTime;
		this.gameName = gameName;
		this.slotsTotal = slotsTotal;
		this.hostCounter = hostCounter;
		this.statString = statString;
		
		ByteBuffer buf = Util.decodeStatString(statString);
		buf.order(ByteOrder.LITTLE_ENDIAN);
		int length = buf.position();
		buf.position(0);
		
		if(length >= 14) {
			int i = 13;
			mapFlags = buf.getInt();
			buf.get();
			mapWidth = buf.getShort();
			mapHeight = buf.getShort();
			mapCRC = buf.getInt();
			mapPath = Util.getTerminatedString(buf, length);
			i += mapPath.length() + 1;
			
			if(length >= i + 1) {
				buf.position(i);
				hostName = Util.getTerminatedString(buf, length);
			}
		}
	}
	
	public String getCodeString() {
		return mapPath + "|" + hostName + "|" + Util.ipString(ip) + "|" +
		port + "|" + hostCounter + "|" + gameType + "|" + mapFlags + "|" + mapWidth + "|" +
		mapHeight + "|" + elapsedTime + "|" + mapCRC + "|" + gameName;
	}
}