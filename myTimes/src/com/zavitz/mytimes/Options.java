package com.zavitz.mytimes;

import java.util.Vector;

import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;

public class Options {

	public static boolean SHOW_ANALOG_CLOCK = true;
	public static boolean SHOW_DIGITAL_CLOCK = true;
	public static boolean LOCK_KEYS = false;
	public static int SCREENSAVER_TIMEOUT = 0;

	public static void setShowAnalogClock(boolean flag) {
		SHOW_ANALOG_CLOCK = flag;
		save();
	}

	public static void setShowDigitalClock(boolean flag) {
		SHOW_DIGITAL_CLOCK = flag;
		save();
	}

	public static void setLockKeys(boolean flag) {
		LOCK_KEYS = flag;
		save();
	}

	public static void setScreensaverTimeout(int i) {
		SCREENSAVER_TIMEOUT = i;
		save();
	}

	// key: greg.zavitz.mytimes.options
	private static final long key = 0x368ff51a6e1c77f0L;
	private static PersistentObject persistentObject;

	static {
		persistentObject = PersistentStore.getPersistentObject(key);
	}

	public static void loadOptions() {
		int options[] = new int[4];
		try {
			Object contents = persistentObject.getContents();
			if (contents instanceof int[]) {
				options = (int[]) contents;
			} else if (contents == null) {
				options = new int[] { 1, 1, 0, 0 };
			}
			SHOW_DIGITAL_CLOCK = options[0] == 1;
			SHOW_ANALOG_CLOCK = options[1] == 1;
			LOCK_KEYS = options[2] == 1;
			SCREENSAVER_TIMEOUT = options[3];
		} catch (Exception e) {
			persistentObject.setContents(new int[] { 1, 1, 0, 0 });
			loadOptions();
		}
	}

	public static void save() {
		persistentObject.setContents(new int[] { SHOW_DIGITAL_CLOCK ? 1 : 0,
				SHOW_ANALOG_CLOCK ? 1 : 0, LOCK_KEYS ? 1 : 0,
				SCREENSAVER_TIMEOUT });
		persistentObject.commit();
	}

}
