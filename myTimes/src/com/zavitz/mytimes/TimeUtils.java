package com.zavitz.mytimes;

import java.util.*;

import net.rim.device.api.i18n.DateFormat;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;

public class TimeUtils {

	public static final String[] TIMEZONES;
	
	static {
		TIMEZONES = TimeZone.getAvailableIDs();
	}
	
	public static Date getCurrentTime() {
		return new Date(System.currentTimeMillis());
	}
	
	public static Time getTime(Object o) {
		if(o instanceof String)
			return getTime((String) o);
		else
			return getTime((TZone) o);
	}
	
	public static Time getTime(String zone) {
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(zone));
		return new Time(calendar);
	}
	
	public static Time getTime(TZone zone) {
		return getTime(zone.zone);
	}
	
}
