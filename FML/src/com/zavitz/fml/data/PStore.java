package com.zavitz.fml.data;

import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;

public class PStore {

	public static String TOKEN = "";
	public static String USERNAME = "";
	public static String PASSWORD = "";

	private static final long KEY_LOGININFO = 0x20243917c9792870L; // com.zavitz.fml.logininfo
	private static final long KEY_CONFIG = 0x6cca5848c82eb634L;// com.zavitz.fml.config
	private static final long KEY_WAP = 0x2101e0aaac897fadL;// com.zavitz.fml.wap
	private static final long KEY_REGISTER = 0x7d3fde68d1a9bcf0L;// com.zavitz.fml.register
	private static PersistentObject pLoginInfo, pConfig, pWap, pRegister;

	static {
		pLoginInfo = PersistentStore.getPersistentObject(KEY_LOGININFO);
		pConfig = PersistentStore.getPersistentObject(KEY_CONFIG);
		pWap = PersistentStore.getPersistentObject(KEY_WAP);
		pRegister = PersistentStore.getPersistentObject(KEY_REGISTER);
	}
	
	public static boolean hasRegistered() {
		Object contents = null;

		contents = pRegister.getContents();
		if (contents != null && contents instanceof String) {
			Config.REGISTRATION_KEY = (String) contents;
			return true;
		}
		
		return false;
	}
	
	public static void register() {
		pRegister.setContents(Config.REGISTRATION_KEY);
		pRegister.commit();
	}

	public static void load() {
		Object contents = null;

		// Login Info:
		contents = pLoginInfo.getContents();
		if (contents != null && contents instanceof String[]
				&& ((String[]) contents).length == 3) {
			String[] info = (String[]) contents;
			USERNAME = info[0];
			PASSWORD = info[1];
			TOKEN = info[2];
		}

		// Connection Info:
		contents = pConfig.getContents();
		if (contents != null && contents instanceof String[] && ((String[])contents).length == 2) {
			String[] info = (String[]) contents;
			Config.CONNECTION_TYPE = Integer.parseInt(info[0]);
			Config.FONT_SIZE = Integer.parseInt(info[1]);
		}

		// Wap Settings:
		contents = pWap.getContents();
		if (contents != null) {
			if(contents instanceof String) { // Preset, ie. AT&T
				URL.CARRIER = Integer.valueOf((String) contents).intValue();
			} else { // Custom, String[] { apn, ip, port, u/n, p/w
				String[] s = (String[]) contents;
				URL.CARRIER = URL.CUSTOM;
				URL.wapGatewayAPN = s[0];
				URL.wapGatewayIP = s[1];
				URL.wapGatewayPort = s[2];
				URL.tunnelAuthUsername = s[3];
				URL.tunnelAuthPassword = s[4];
			}
		}
	}

	public static void store() {
		// Login Info:
		pLoginInfo.setContents(new String[] { USERNAME, PASSWORD, TOKEN });
		pLoginInfo.commit();
		
		// Connection Info:
		pConfig.setContents(new String[] { String.valueOf(Config.CONNECTION_TYPE) , String.valueOf(Config.FONT_SIZE)});
		pConfig.commit();
		
		// Wap Settings:
		if(URL.CARRIER == URL.CUSTOM)
			pWap.setContents(new String[] {URL.wapGatewayAPN, URL.wapGatewayIP, URL.wapGatewayPort, URL.tunnelAuthUsername, URL.tunnelAuthPassword});
		else
			pWap.setContents(String.valueOf(URL.CARRIER));
	}

}