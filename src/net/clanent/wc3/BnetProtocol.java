package net.clanent.wc3;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

import net.clanent.wc3.util.LittleEndianDataInputStream;
import net.clanent.wc3.util.LittleEndianDataOutputStream;
import net.clanent.wc3.util.Util;


public class BnetProtocol {
	public static final int BNET_HEADER_CONSTANT = 255;
	public static final int SID_NULL = 0;
	public static final int SID_STOPADV = 2;
	public static final int SID_GETADVLISTEX = 9;
	public static final int SID_ENTERCHAT = 10;
	public static final int SID_JOINCHANNEL = 12;
	public static final int SID_CHATCOMMAND = 14;
	public static final int SID_CHATEVENT = 15;
	public static final int SID_CHECKAD = 21;
	public static final int SID_STARTADVEX3 = 28;
	public static final int SID_DISPLAYAD = 33;
	public static final int SID_NOTIFYJOIN = 34;
	public static final int SID_PING = 37;
	public static final int SID_LOGONRESPONSE = 41;
	public static final int SID_NETGAMEPORT = 69;
	public static final int SID_AUTH_INFO = 80;
	public static final int SID_AUTH_CHECK = 81;
	public static final int SID_AUTH_ACCOUNTCREATE = 82;
	public static final int SID_AUTH_ACCOUNTLOGON = 83;
	public static final int SID_AUTH_ACCOUNTLOGONPROOF = 84;
	public static final int SID_WARDEN = 94;
	public static final int SID_FRIENDSLIST = 101;
	public static final int SID_FRIENDSUPDATE = 102;
	public static final int SID_CLANMEMBERLIST = 125;
	public static final int SID_CLANMEMBERSTATUSCHANGE = 127;
	
	//key result
	public static final int OLD_GAME_VERSION = 256;
	public static final int INVALID_VERSION = 257;
	public static final int ROC_KEY_IN_USE = 513;
	public static final int TFT_KEY_IN_USE = 529;
	
	LittleEndianDataInputStream in;
	LittleEndianDataOutputStream out;
	
	byte[] clientToken; // set in constructor
	byte[] logonType; // set in RECEIVE_SID_AUTH_INFO
	byte[] serverToken; // set in RECEIVE_SID_AUTH_INFO
	byte[] MPQFileTime; // set in RECEIVE_SID_AUTH_INFO
	byte[] IX86VerFileName; // set in RECEIVE_SID_AUTH_INFO
	byte[] valueStringFormula; // set in RECEIVE_SID_AUTH_INFO
	byte[] keyState;// set in RECEIVE_SID_AUTH_CHECK
	byte[] keyStateDescription;	// set in RECEIVE_SID_AUTH_CHECK
	byte[] salt; // set in RECEIVE_SID_AUTH_ACCOUNTLOGON
	byte[] serverPublicKey; // set in RECEIVE_SID_AUTH_ACCOUNTLOGON
	String uniqueName; // set in RECEIVE_SID_ENTERCHAT
	
	public BnetProtocol(LittleEndianDataInputStream in, LittleEndianDataOutputStream out) {
		this.in = in;
		this.out = out;
		
		clientToken = new byte[] { (byte) 220, 1, (byte) 203, 7 };
		logonType = new byte[4];
		serverToken = new byte[4];
		MPQFileTime = new byte[8];

		keyState = new byte[4];

		salt = new byte[32];
		serverPublicKey = new byte[32];
	}
	
	public byte[] getValueStringFormula() {
		return valueStringFormula;
	}
	
	public byte[] getIX86VerFileName() {
		return IX86VerFileName;
	}
	
	public byte[] getClientToken() {
		return clientToken;
	}
	
	public byte[] getServerToken() {
		return serverToken;
	}
	
	public byte[] getKeyState() {
		return keyState;
	}
	
	public byte[] getSalt() {
		return salt;
	}
	
	public byte[] getServerPublicKey() {
		return serverPublicKey;
	}
	
	public String getKeyStateDescription() {
		return new String(keyStateDescription);
	}
	
	public String getUniqueName() {
		return uniqueName;
	}
	
	//receive functions (buf and length do not include the header)
	
	public int receivePing(ByteBuffer buf, int length) {
		if(length >= 4) {
			return buf.getInt();
		} else {
			return 0;
		}
	}
	
	public boolean receiveAuthInfo(ByteBuffer buf, int length) {
		if(length >= 21) {
			buf.get(logonType); //4 bytes
			buf.get(serverToken); //4 bytes
			buf.position(buf.position() + 4); //skip 4 bytes unknown
			buf.get(MPQFileTime); //8 bytes
			IX86VerFileName = Util.getTerminatedArray(buf, length);
			valueStringFormula = Util.getTerminatedArray(buf, length);
			
			return true;
		} else {
			return false;
		}
	}
	
	public boolean receiveAuthCheck(ByteBuffer buf, int length) {
		if(length >= 5) {
			buf.get(keyState); //4 bytes
			keyStateDescription = Util.getTerminatedArray(buf, length);
			return true;
		} else {
			return false;
		}
	}
	
	public boolean receiveAuthAccountLogon(ByteBuffer buf, int length) {
		if(length >= 68) {
			int status = buf.getInt();
			
			if(status == 0) {
				buf.get(salt);
				buf.get(serverPublicKey);
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	public boolean receiveAuthAccountLogonProof(ByteBuffer buf, int length) {
		if(length >= 4) {
			int status = buf.getInt();
			
			if(status == 0 || status == 14) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	public boolean receiveEnterChat(ByteBuffer buf, int length) {
		if(length >= 1) {
			uniqueName = Util.getTerminatedString(buf, length);
			return true;
		} else {
			return false;
		}
	}
	
	public ArrayList<IncomingGameHost> receiveGetAdvListex3(ByteBuffer buf, int length) throws IOException {
		ArrayList<IncomingGameHost> games = new ArrayList<IncomingGameHost>();
		
		if(length >= 8) {
			int gamesFound = buf.getInt();
			
			while(gamesFound > 0) {
				gamesFound--;
				
				if(length < buf.position() + 33) break;
				
				int gameType = Util.unsignedShort(buf.getShort());
				int parameter = Util.unsignedShort(buf.getShort());
				int languageID = buf.getInt();
				
				buf.getShort(); //AF_INET
				
				buf.order(ByteOrder.BIG_ENDIAN);
				int port = Util.unsignedShort(buf.getShort());
				buf.order(ByteOrder.LITTLE_ENDIAN);
				
				byte[] ip = new byte[4];
				buf.get(ip);
				buf.getInt(); //zeroes
				buf.getInt(); //zeroes
				int status = buf.getInt();
				int elapsedTime = buf.getInt();
				String gamename = Util.getTerminatedString(buf, length);
				if(length < buf.position() + 1) break;
				
				Util.getTerminatedArray(buf, length); //game password
				if(length < buf.position() + 10) break;
				
				int slotsTotal = Util.hexToBytes("0" + (char) buf.get())[0];
				
				byte[] hostCounterRaw = new byte[8];
				buf.get(hostCounterRaw);
				Util.reverse(hostCounterRaw);
				int hostCounter = Util.toInt(Util.hexToBytes(Util.toStr(hostCounterRaw)));
				
				byte[] statString = Util.getTerminatedArray(buf, length);
				
				games.add(new IncomingGameHost(gameType, parameter, languageID, port, ip, status, elapsedTime, gamename, slotsTotal, hostCounter, statString));
			}
			
			return games;
		} else {
			return null;
		}
	}
	
	//send functions
	
	public void sendProtocolInitializeSelector() throws IOException {
		out.write(1);
	}
	
	public void sendPing(int ping) throws IOException {
		out.write(BNET_HEADER_CONSTANT);
		out.write(SID_PING);
		out.writeShort(8);
		out.writeInt(ping);
	}
	
	public void sendAuthInfo(byte ver, boolean TFT, int localeID, String countryAbbrev, String country) throws IOException {
		byte ProtocolID[] = {   0,   0,   0,   0 };
		byte PlatformID[] = {  54,  56,  88,  73 }; //IX86
		byte ProductID_ROC[] = {  51,  82,  65,  87 }; //WAR3
		byte ProductID_TFT[] = {  80,  88,  51,  87 }; //W3XP
		byte Version[] = { ver,   0,   0,   0 };
		byte Language[] = {  83,  85, 110, 101 }; //enUS
		byte LocalIP[] = { 127,   0,   0,   1 };
		byte TimeZoneBias[] = {  44,   1,   0,   0 }; // 300 minutes (GMT -0500)
		
		out.write(BNET_HEADER_CONSTANT); //BNET header constant
		out.write(SID_AUTH_INFO); //AUTH_INFO identifier
		out.writeShort(40 + countryAbbrev.length() + country.length() + 2);
		
		out.write(ProtocolID);
		out.write(PlatformID);
		
		if(TFT) out.write(ProductID_TFT);
		else out.write(ProductID_ROC);
		
		out.write(Version);
		out.write(Language);
		out.write(LocalIP);
		out.write(TimeZoneBias);
		out.writeInt(localeID); //locale ID
		out.writeInt(localeID); //language ID (copying the locale ID should be sufficient since we don't care about sublanguages)
		out.writeTerminatedString(countryAbbrev);
		out.writeTerminatedString(country);
	}
	
	public void sendAuthCheck(boolean TFT, byte[] exeVersion, byte[] exeVersionHash, byte[] keyInfoROC, byte[] keyInfoTFT, String exeInfo, String keyOwnerName) throws IOException {
		int numKeys = 1;
		if(TFT) numKeys = 2;
		
		if(clientToken.length == 4 && exeVersion.length == 4 && exeVersionHash.length == 4 && keyInfoROC.length == 36 && (!TFT || keyInfoTFT.length == 36)) {
			out.write(BNET_HEADER_CONSTANT); //BNET header constant
			out.write(SID_AUTH_CHECK); //AUTH_CHECK identifier
			
			if(TFT) out.writeShort(96 + exeInfo.length() + keyOwnerName.length() + 2);
			else out.writeShort(60 + exeInfo.length() + keyOwnerName.length() + 2);
			
			out.write(clientToken);
			out.write(exeVersion);
			out.write(exeVersionHash);
			out.writeInt(numKeys); //number of keys in the packet
			out.writeInt(0); //32-bit boolean using spawn
			out.write(keyInfoROC);
			if(TFT) out.write(keyInfoTFT);
			out.writeTerminatedString(exeInfo);
			out.writeTerminatedString(keyOwnerName);
		}
	}
	
	public void sendAuthAccountCreate(byte[] salt, byte[] verifier, String username) throws IOException {
		if(verifier.length == 32) {
			out.write(BNET_HEADER_CONSTANT);
			out.write(SID_AUTH_ACCOUNTCREATE);
			out.writeShort(68 + username.length() + 1);
			
			out.write(salt);
			out.write(verifier);
			out.writeTerminatedString(username);
		}
	}
	
	public void sendAuthAccountLogon(byte[] clientPublicKey, String accountName) throws IOException {
		if(clientPublicKey.length == 32) {
			out.write(BNET_HEADER_CONSTANT);
			out.write(SID_AUTH_ACCOUNTLOGON);
			out.writeShort(36 + accountName.length() + 1);
			
			out.write(clientPublicKey);
			out.writeTerminatedString(accountName);
		}
	}

	public void sendAuthAccountLogonProof(byte[] clientPasswordProof) throws IOException {
		if(clientPasswordProof.length == 20) {
			out.write(BNET_HEADER_CONSTANT);
			out.write(SID_AUTH_ACCOUNTLOGONPROOF);
			out.writeShort(24);
			out.write(clientPasswordProof);
		}
	}

	public void sendNetGamePort(int port) throws IOException {
		out.write(BNET_HEADER_CONSTANT);
		out.write(SID_NETGAMEPORT);
		out.writeShort(6);
		out.writeShort((short) port);
	}

	public void sendEnterChat() throws IOException {
		out.write(BNET_HEADER_CONSTANT);
		out.write(SID_ENTERCHAT);
		out.writeShort(6);
		out.write(0); //account name is null on Warcraft III
		out.write(0); //stat string is null on cdkey products
	}
	
	public void sendJoinChannel(String channel) throws IOException {
		byte[] NoCreateJoin = new byte[] {2, 0, 0, 0};
		byte[] FirstJoin = new byte[] {1, 0, 0, 0};
		
		out.write(BNET_HEADER_CONSTANT);
		out.write(SID_JOINCHANNEL);
		out.writeShort(8 + channel.length() + 1);
		
		if(channel.length() > 0)
			out.write(NoCreateJoin);
		else
			out.write(FirstJoin);
		
		out.writeTerminatedString(channel);
	}
	
	public void sendChatCommand(String command) throws IOException {
		out.write(BNET_HEADER_CONSTANT);
		out.write(SID_CHATCOMMAND);
		out.writeShort(4 + command.length() + 1);
		out.writeTerminatedString(command);
	}
	
	public void sendNotifyJoin(String gamename) throws IOException {
		byte[] ProductID = new byte[] {0, 0, 0, 0};
        byte[] ProductVersion = {14, 0, 0, 0};
		
		out.write(BNET_HEADER_CONSTANT);
		out.write(SID_NOTIFYJOIN);
		out.writeShort(13 + gamename.length() + 1);
		out.write(ProductID);
		out.write(ProductVersion);
		out.writeTerminatedString(gamename);
		out.write(0); //game password
	}

	public void sendStartAdvex3(int state, byte[] mapGameType, String gameName, int upTime, int hostcounter, byte[] statString) throws IOException {
		byte[] Unknown = new byte[] {(byte) 255, 3, 0, 0};
		byte[] CustomGame = new byte[] {0, 0, 0, 0};
		
		String hostCounterString = Util.getHex(Util.toBytes(hostcounter));
		while(hostCounterString.length() < 8) hostCounterString = hostCounterString + "0";

		if(gameName != null && statString != null && hostCounterString.length() == 8 && mapGameType != null && mapGameType.length == 4) {
			out.write(BNET_HEADER_CONSTANT);
			out.write(SID_STARTADVEX3);
			out.writeShort(26 + gameName.length() + hostCounterString.length() + statString.length + 3);
			out.writeInt(state);
			out.writeInt(upTime);
			out.write(mapGameType);
			out.write(Unknown);
			out.write(CustomGame);
			out.writeTerminatedString(gameName);
			out.write(0); //game password is NULL
			out.write(98); //11 slots free
			out.writeTerminatedString(hostCounterString);
			out.write(statString);
			out.write(0); //stat string null terminator
		} else {
			System.out.println("[BnetProtocol] Invalid parameters passed to sendStartAdvex3");
		}
	}

	public void sendGetAdvListex3(String gamename, int numGames) throws IOException {
		byte[] Condition1 = new byte[] {0, 0};
		byte[] Condition2 =  new byte[] {0, 0};
		byte[] Condition3 = new byte[] {0, 0, 0, 0};
		byte[] Condition4 = new byte[] {0, 0, 0, 0};
		
		if(gamename == null) {
			gamename = "";
		}
		
		if(gamename.isEmpty()) {
			Condition1[0] = 0;
			Condition1[1] = (byte) 224;
			Condition2[0] = 127;
			Condition2[1] = 0;
		} else {
			Condition1[0] = (byte) 255;
			Condition1[1] = 3;
			Condition2[0] = 0;
			Condition2[1] = 0;
			Condition3[0] = (byte) 255;
			Condition3[1] = 3;
			Condition3[2] = 0;
			Condition3[3] = 0;
			numGames = 1;
		}
		
		out.write(BNET_HEADER_CONSTANT);
		out.write(SID_GETADVLISTEX);
		out.writeShort(22 + gamename.length() + 1);
		out.write(Condition1);
		out.write(Condition2);
		out.write(Condition3);
		out.write(Condition4);
		out.writeInt(numGames);
		out.writeTerminatedString(gamename);
		out.write(0); //game password is null
		out.write(0); //game stats is null
	}
}