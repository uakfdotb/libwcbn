// from jghost at http://code.google.com/p/jghost/
package net.clanent.wc3;

import java.io.File;

import net.clanent.wc3.util.Util;

import org.jbls.hashing.HashException;
import org.jbls.hashing.HashMain;
import org.jbls.hashing.SRP;

public class BNCSUtilInterface {
	//---- State

	private SRP srp;
	
	private byte[] m_EXEVersion;			// set in HELP_SID_AUTH_CHECK
	private byte[] m_EXEVersionHash;		// set in HELP_SID_AUTH_CHECK
	private String m_EXEInfo;				// set in HELP_SID_AUTH_CHECK
	private byte[] m_KeyInfoROC;			// set in HELP_SID_AUTH_CHECK
	private byte[] m_KeyInfoTFT;			// set in HELP_SID_AUTH_CHECK
	private byte[] m_ClientKey;			// set in HELP_SID_AUTH_ACCOUNTLOGON
	private byte[] m_M1;					// set in HELP_SID_AUTH_ACCOUNTLOGONPROOF
	private byte[] m_PvPGNPasswordHash;	// set in HELP_PvPGNPasswordHash

	//---- Constructors
	
	public BNCSUtilInterface (String userName, String userPassword) {
		srp = new SRP(userName, userPassword);
	}

	//---- Methods
	
	public byte[] GetEXEVersion () 			{ return m_EXEVersion; }
	public byte[] GetEXEVersionHash ()		{ return m_EXEVersionHash; }
	public String GetEXEInfo () 				{ return m_EXEInfo; }
	public byte[] GetKeyInfoROC( ) 			{ return m_KeyInfoROC; }
	public byte[] GetKeyInfoTFT( ) 			{ return m_KeyInfoTFT; }
	public byte[] GetClientKey( ) 			{ return m_ClientKey; }
	public byte[] GetM1( ) 					{ return m_M1; }
	public byte[] GetPvPGNPasswordHash( ) 	{ return m_PvPGNPasswordHash; }

	public void SetEXEVersion( byte[] nEXEVersion )			{ m_EXEVersion = nEXEVersion; }
	public void SetEXEVersionHash( byte[] nEXEVersionHash )	{ m_EXEVersionHash = nEXEVersionHash; }
	
	public void Reset (String userName, String userPassword) {
		srp = new SRP(userName, userPassword);
	}

	public boolean HELP_SID_AUTH_CHECK (boolean TFT, String war3Path, String keyROC, String keyTFT, String valueStringFormula, String mpqFileName, int clientToken, int serverToken ) {
		// set m_EXEVersion, m_EXEVersionHash, m_EXEInfo, m_InfoROC, m_InfoTFT
		
		if(keyROC == null) keyROC = "";
		if(TFT && keyTFT == null) keyTFT = "";
		
		if(keyROC.length() != 26 || (TFT && keyTFT.length() != 26)) {
			System.out.println("[BNCS] One or both keys are invalid (not 26 characters)");
		}
		
		srp.set_NLS(2); //little endian

		String FileWar3EXE = war3Path + "war3.exe";
		String FileStormDLL = war3Path + "storm.dll";
		String FileGameDLL = war3Path + "game.dll";
		
		boolean ExistsWar3EXE = new File(FileWar3EXE).exists();
		boolean ExistsStormDLL = new File(FileStormDLL).exists();
		boolean ExistsGameDLL = new File(FileGameDLL).exists();

		if( ExistsWar3EXE && ExistsStormDLL && ExistsGameDLL )
		{
			String[] files = new String[] {FileWar3EXE, FileStormDLL, FileGameDLL};
			m_EXEInfo = HashMain.getExeInfo(0x08, files);
			m_EXEVersion = Util.toBytes(HashMain.getExeVer(0x08, files));
			int EXEVersionHash = HashMain.getChecksum(0x08, valueStringFormula, mpqFileName, 0, files);
			m_EXEVersionHash = Util.toBytes(EXEVersionHash);
			m_KeyInfoROC = CreateKeyInfo( keyROC, clientToken, serverToken);

			if(TFT) m_KeyInfoTFT = CreateKeyInfo( keyTFT, clientToken, serverToken);

			if(m_KeyInfoROC.length == 36 && (!TFT || m_KeyInfoTFT.length == 36))
				return true;
			else
			{
				if( m_KeyInfoROC.length != 36 )
					System.out.println("[BNCS] unable to create ROC key info - invalid ROC key");

				if( TFT && m_KeyInfoTFT.length != 36 )
					System.out.println("[BNCS] unable to create TFT key info - invalid TFT key" );
			}
		}
		else
		{
			if( !ExistsWar3EXE )
				System.out.println("[BNCS] unable to open [" + FileWar3EXE + "]" );

			if( !ExistsStormDLL )
				System.out.println("[BNCS] unable to open [" + FileStormDLL + "]" );

			if( !ExistsGameDLL )
				System.out.println("[BNCS] unable to open [" + FileGameDLL + "]" );
		}

		return false;
	}
	
	public boolean HELP_SID_AUTH_ACCOUNTLOGON( ) {
		m_ClientKey = srp.get_A();
		return true;
	}
	
	public boolean HELP_SID_AUTH_ACCOUNTLOGONPROOF( String salt, String serverKey ) {
		byte[] saltBytes = Util.toBytes(salt);
		byte[] serverKeyBytes = Util.toBytes(serverKey);

		m_M1 = srp.getM1(saltBytes, serverKeyBytes);
		return true;
	}
	
	public byte[] HELP_SID_AUTH_ACCOUNTCREATE( byte[] salt ) {
		return srp.get_v(salt).toByteArray();
	}
	
	public boolean HELP_PvPGNPasswordHash(String userPassword ) {
		/*// set m_PvPGNPasswordHash
		//TODO implement hash
		char buf[20];
		hashPassword( userPassword.c_str( ), buf );
		m_PvPGNPasswordHash = UTIL_CreateByteArray( (unsigned char *)buf, 20 );*/
		return true;
	}

	private byte[] CreateKeyInfo(String key, int clientToken, int serverToken ) {
		try {
			byte[] keyInfo = HashMain.hashKey(clientToken, serverToken, key).getBuffer();
			return keyInfo;
		} catch(HashException e) {
			System.out.println("[BNCS] Key hash failed: " + e.getLocalizedMessage());
			return new byte[] {};
		}
	}
}
