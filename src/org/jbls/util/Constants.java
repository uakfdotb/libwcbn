package org.jbls.util;

public final class Constants{
	

    public static final byte PRODUCT_STARCRAFT         = 0x01;
    public static final byte PRODUCT_BROODWAR          = 0x02;
    public static final byte PRODUCT_WAR2BNE           = 0x03;
    public static final byte PRODUCT_DIABLO2           = 0x04;
    public static final byte PRODUCT_LORDOFDESTRUCTION = 0x05;
    public static final byte PRODUCT_JAPANSTARCRAFT    = 0x06;
    public static final byte PRODUCT_WARCRAFT3         = 0x07;
    public static final byte PRODUCT_THEFROZENTHRONE   = 0x08;
    public static final byte PRODUCT_DIABLO            = 0x09;
    public static final byte PRODUCT_DIABLOSHAREWARE   = 0x0A;
    public static final byte PRODUCT_STARCRAFTSHAREWARE= 0x0B;
    public static String[] prods = {"STAR", "SEXP", "W2BN", "D2DV", "D2XP", "JSTR", "WAR3", "W3XP", "DRTL", "DSHR", "SSHR"};
	   
    
    public static String[][] IX86files = {
    	{"IX86/STAR/", "Starcraft.exe",       "Storm.dll",    "Battle.snp"},
    	{"IX86/STAR/", "Starcraft.exe",       "Storm.dll",    "Battle.snp"},
    	{"IX86/W2BN/", "Warcraft II BNE.exe", "Storm.dll",    "Battle.snp"},
    	{"IX86/D2DV/", "game.exe",            "Bnclient.dll", "D2Client.dll"},
    	{"IX86/D2XP/", "game.exe",            "Bnclient.dll", "D2Client.dll"},
    	{"IX86/JSTR/", "StarcraftJ.exe",      "Storm.dll",    "Battle.snp"},
    	{"IX86/WAR3/", "war3.exe",            "Storm.dll",    "Game.dll"},
    	{"IX86/WAR3/", "war3.exe",            "Storm.dll",    "Game.dll"},
    	{"IX86/DRTL/", "Diablo.exe",          "Storm.dll",    "Battle.snp"},
    	{"IX86/DSHR/", "Diablo_s.exe",        "Storm.dll",    "Battle.snp"},
    	{"IX86/SSHR/", "Starcraft_s.exe",     "Storm.dll",    "Battle.snp"}
    };
    
    public static int[] IX86verbytes = {0xcf, 0xcf, 0x4f, 0x0b, 0x0b, 0xa9, 0x14, 0x14, 0x2a, 0x2a, 0xa5};    
    
    public static String build="Build V2.9 Remote admin, extended admin commands w/ JSTR support.(01/18/06)";
	public static int maxThreads=500;
    public static int maxAdminThreads=5;
	public static int BNLSPort=9367;
	public static boolean requireAuthorization=false;
	public static boolean trackStatistics=true;
	public static int ipAuthStatus=1; //default is IPBans on

	public static boolean displayPacketInfo=true;
	public static boolean displayParseInfo=false;
	public static boolean debugInfo=false;

	public static boolean RunAdmin = true;
	public static int     AdminPort=9360;
	
	public static boolean RunHTTP = true;
	public static int     HTTPPort = 81;
	
	
	
	public static int lngServerVer=0x01;
	public static int numOfNews=0;
	public static String[] strNews={"", "", "", "", ""};
}
