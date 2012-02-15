package com.zavitz.fml.data;

import java.io.EOFException;
import net.rim.blackberry.api.browser.URLEncodedPostData;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.system.CoverageInfo;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.util.DataBuffer;

public class URL {

	private String _path;
	private URLEncodedPostData _data;

	public URL(String path) {
		_path = Config.BASE_URL + path;

		_data = new URLEncodedPostData(null, false);
		addVar("key", Config.KEY);
		addVar("language", "en");
		if (!PStore.TOKEN.equals(""))
			addVar("token", PStore.TOKEN);
	}

	public void addVar(String key, String val) {
		_data.append(key, val);
	}

	public String construct() {
		return _path + "?" + new String(_data.getBytes()) + getSuffix();
	}
	
	public static String getConnectionType() {
		initializeTransportAvailability();
		String suffix = getSuffix();
		if(suffix.equals(getBIS()))
			return "BlackBerry Internet Service";
		if(suffix.equals(getBES()))
			return "BlackBerry Enterprise Server";
		if(suffix.equals(getWAP2()))
			return "WAP2";
		if(suffix.equals(getTCP()))
			return "TCP/IP";
		if(suffix.equals(getWAP()))
			return "WAP1.0";
		if(suffix.equals(getWiFi()))
			return "WiFi";
		return "";
	}
	
	public static boolean canBIS() {
		return coverageBIS && srBIS != null;
	}
	
	public static boolean canBES() {
		return coverageMDS || coverageBES;
	}
	
	public static boolean canWAP2() {
		return coverageWAP2 && srWAP2 != null;
	}
	
	public static boolean canTCP() {
		return coverageTCP;
	}
	
	public static boolean canWAP() {
		return coverageWAP;
	}
	
	public static boolean canWiFi() {
		return coverageWiFi && srWiFi != null;
	}

	public static String getSuffix() {
		initializeTransportAvailability();
		switch(Config.CONNECTION_TYPE) {
		case 0:
			if(coverageBIS && srBIS != null)
				return getBIS();
			if(coverageBES)
				return getBES();
			if(coverageWAP2 && srWAP2 != null)
				return getWAP2();
			if(coverageMDS && srMDS != null)
				return getMDS();
			if(coverageTCP)
				return getTCP();
			if(coverageWiFi && srWiFi != null)
				return getWiFi();
			break;
		case 1:
			return getBIS();
		case 2:
			return getBES();
		case 3:
			return getWAP2();
		case 4:
			return getTCP();
		case 5:
			return getWAP();
		case 6:
			return getWiFi();
		}
		return "";
	}
	
	public static String getSuffix(int i) {
		initializeTransportAvailability();
		switch(i) {
		case 0:
			return getBIS();
		case 1:
			return getBES();
		case 2:
			return getWAP2();
		case 3:
			return getTCP();
		case 4:
			return getWAP();
		case 5:
			return getWiFi();
		}
		return "";
	}
	
	private static String getBIS() { if(srBIS == null) return ";error-bis"; return ";deviceside=false;connectionUID=" + srBIS.getUid() + ";ConnectionType=mds-public"; }
	private static String getBES() { return ";deviceside=false"; }
	private static String getMDS() { return getBES(); }
	private static String getTCP() { return ";deviceside=true"; }
	private static String getWAP() { loadWAPSettings(CARRIER);
									 return ";deviceside=true;"
									 	    + (!wapGatewayAPN.equals("") ? "WapGatewayAPN=" + wapGatewayAPN : "")
									 	    + (!wapGatewayIP.equals("") ? ";WapGatewayIP=" + wapGatewayIP : "")
									 	    + (!wapGatewayPort.equals("") ? ";WapGatewayPort=" + wapGatewayPort : "")
											+ (!tunnelAuthUsername.equals("") ? ";TunnelAuthUsername=" + tunnelAuthUsername : "")
											+ (!tunnelAuthPassword.equals("") ? ";TunnelAuthPassword=" + tunnelAuthPassword : ""); }
	private static String getWAP2() { if(srWAP2 == null) return ";error-wap2"; return ";deviceside=true" + ";ConnectionUID=" + srWAP2.getUid(); }
	private static String getWiFi() { return ";interface=wifi"; }
	
	public static String wapGatewayAPN = "";
	public static String wapGatewayIP = "";
	public static String wapGatewayPort = "";
	public static String tunnelAuthPassword = "";
	public static String tunnelAuthUsername = "";
	public static final int ATT = 1;
	public static final int BELL = 2;
	public static final int CINGULAR = 3;
	public static final int O2 = 4;
	public static final int ROGERS = 5;
	public static final int SFR = 6;
	public static final int SUNRISE = 7;
	public static final int TELEFONICA_MOVIL = 8;
	public static final int TMOBILE = 9;
	public static final int VODAFONE_GERMANY = 10;
	public static final int VODAFONE_IRELAND = 11;
	public static final int VODAFONE_SWEDEN = 12;
	public static final int VODAFONE_UK = 13;
	public static final int CUSTOM = 0;
	public static int CARRIER = CUSTOM;
	
	public static void loadWAPSettings(int id) {
		switch(id) {
		case ATT:
			wapGatewayAPN = "proxy";
			wapGatewayIP = "10.250.250.250";
			wapGatewayPort = "9201";
			tunnelAuthUsername = "";
			tunnelAuthPassword = "";
			break;
		case BELL:
			wapGatewayAPN = "";
			wapGatewayIP = "207.236.197.199";
			wapGatewayPort = "9203";
			tunnelAuthUsername = "";
			tunnelAuthPassword = "";
			break;
		case CINGULAR:
			wapGatewayAPN = "proxy";
			wapGatewayIP = "10.250.250.250";
			wapGatewayPort = "9201";
			tunnelAuthUsername = "";
			tunnelAuthPassword = "";
			break;
		case O2:
			wapGatewayAPN = "mobile.o2.co.uk";
			wapGatewayIP = "193.113.200.200";
			wapGatewayPort = "";
			tunnelAuthUsername = "web";
			tunnelAuthPassword = "password";
			break;
		case ROGERS:
			wapGatewayAPN = "internet.com";
			wapGatewayIP = "";
			wapGatewayPort = "";
			tunnelAuthUsername = "wapuser1";
			tunnelAuthPassword = "wap";
			break;
		case SFR:
			wapGatewayAPN = "websfr";
			wapGatewayIP = "172.20.2.10";
			wapGatewayPort = "";
			tunnelAuthUsername = "";
			tunnelAuthPassword = "";
			break;
		case SUNRISE:
			wapGatewayAPN = "internet";
			wapGatewayIP = "212.35.35.35";
			wapGatewayPort = "";
			tunnelAuthUsername = "internet";
			tunnelAuthPassword = "internet";
			break;
		case TELEFONICA_MOVIL:
			wapGatewayAPN = "movistar.es";
			wapGatewayIP = "194.179.001.100";
			wapGatewayPort = "";
			tunnelAuthUsername = "movistar";
			tunnelAuthPassword = "movistar";
			break;
		case TMOBILE:
			wapGatewayAPN = "internet2.voicestream.com";
			wapGatewayIP = "216.155.175.105";
			wapGatewayPort = "";
			tunnelAuthUsername = "";
			tunnelAuthPassword = "";
			break;
		case VODAFONE_GERMANY:
			wapGatewayAPN = "web.vodafone.de";
			wapGatewayIP = "139.7.30.125";
			wapGatewayPort = "";
			tunnelAuthUsername = "vodafone";
			tunnelAuthPassword = "vodafone";
			break;
		case VODAFONE_IRELAND:
			wapGatewayAPN = "isp.vodafone.ie";
			wapGatewayIP = "";
			wapGatewayPort = "";
			tunnelAuthUsername = "vodafone";
			tunnelAuthPassword = "vodafone";
			break;
		case VODAFONE_SWEDEN:
			wapGatewayAPN = "internet.vodafone.net";
			wapGatewayIP = "";
			wapGatewayPort = "";
			tunnelAuthUsername = "";
			tunnelAuthPassword = "";
			break;
		case VODAFONE_UK:
			wapGatewayAPN = "Internet";
			wapGatewayIP = "";
			wapGatewayPort = "";
			tunnelAuthUsername = "web";
			tunnelAuthPassword = "web";
			break;
		}
	}

	private static ServiceRecord srMDS;
	private static ServiceRecord srBIS;
	private static ServiceRecord srWAP;
	static ServiceRecord srWAP2;
	private static ServiceRecord srWiFi;
	private static ServiceRecord srUnite;
	private static boolean coverageTCP = false;
	private static boolean coverageMDS = false;
	private static boolean coverageBES = false;
	private static boolean coverageBIS = false;
	private static boolean coverageWAP = false;
	static boolean coverageWAP2 = false;
	static boolean coverageWiFi = false;
	private static boolean coverageUnite = false;
	private static final int CONFIG_TYPE_WAP = 0;
	private static final int CONFIG_TYPE_BES = 1;
	private static final int CONFIG_TYPE_WIFI = 3;
	private static final int CONFIG_TYPE_BIS = 4;
	private static final int CONFIG_TYPE_WAP2 = 7;
	private static final String UNITE_NAME = "Unite";

	private static void initializeTransportAvailability() {
		ServiceBook sb = ServiceBook.getSB();
		ServiceRecord[] records = sb.getRecords();

		for (int i = 0; i < records.length; i++) {
			ServiceRecord myRecord = records[i];
			String cid, uid;

			if (myRecord.isValid() && !myRecord.isDisabled()) {
				cid = myRecord.getCid().toLowerCase();
				uid = myRecord.getUid().toLowerCase();
				// BIS
				if (cid.indexOf("ippp") != -1 && uid.indexOf("gpmds") != -1) {
					srBIS = myRecord;
				}

				// BES
				if (cid.indexOf("ippp") != -1 && uid.indexOf("gpmds") == -1) {
					srMDS = myRecord;
				}

				// WiFi
				if (cid.indexOf("wptcp") != -1 && uid.indexOf("wifi") != -1) {
					srWiFi = myRecord;
				}
				
				// Wap2.0
				if (cid.indexOf("wptcp") != -1 && uid.indexOf("wifi") == -1
						&& uid.indexOf("mms") == -1) {
					srWAP2 = myRecord;
				}
				
				// Wap1.0
				if (getConfigType(myRecord) == CONFIG_TYPE_WAP && cid.equalsIgnoreCase("wap")) {
					srWAP = myRecord;
				}
			}
		}

		if (CoverageInfo.isCoverageSufficient(CoverageInfo.COVERAGE_BIS_B)) {
			coverageBIS = true;
		}
		if (CoverageInfo.isCoverageSufficient(CoverageInfo.COVERAGE_DIRECT)) {
			coverageTCP = true;
			coverageWAP2 = true;
			coverageWAP = true;
		}
		if (CoverageInfo.isCoverageSufficient(CoverageInfo.COVERAGE_MDS)) {
			coverageMDS = true;
			coverageBES = true;
		}
	}

	private static int getConfigType(ServiceRecord record) {
		return getDataInt(record, 12);
	}

	private static int getDataInt(ServiceRecord record, int type) {
		DataBuffer buffer = null;
		buffer = getDataBuffer(record, type);

		if (buffer != null) {
			try {
				return ConverterUtilities.readInt(buffer);
			} catch (EOFException e) {
				return -1;
			}
		}
		return -1;
	}

	private static DataBuffer getDataBuffer(ServiceRecord record, int type) {
		byte[] data = record.getApplicationData();
		if (data != null) {
			DataBuffer buffer = new DataBuffer(data, 0, data.length, true);
			try {
				buffer.readByte();
			} catch (EOFException e1) {
				return null;
			}
			if (ConverterUtilities.findType(buffer, type)) {
				return buffer;
			}
		}
		return null;
	}

}
