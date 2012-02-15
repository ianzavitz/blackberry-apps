package com.zavitz.mytimes;

import java.util.Vector;
import net.rim.device.api.system.*;

public class PersistentUtils {
	// key: greg.zavitz.mytimes
	private static final long key = 0x5744616c462dcd77L;
	private static boolean active = false;
	public static boolean firstRun = false;
	private static PersistentObject persistentObject;
	
	static {
		persistentObject = PersistentStore.getPersistentObject(key);
	}

	public static Vector getTimeZones() {
		active = true;
		Vector times = (Vector) persistentObject.getContents();
		if(times == null) {
			times = new Vector();
			times.addElement("GMT");
			firstRun = true;
		}
		active = false;
		return times;
	}
	
	public static boolean save() {
		if(active)
			return false;
		persistentObject.setContents(HomeScreen.timeZones);
		persistentObject.commit();
		return true;
	}

}
