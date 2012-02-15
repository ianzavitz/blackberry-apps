package com.zavitz.mytimes;

import java.util.Calendar;
import java.util.TimeZone;

import net.rim.device.api.i18n.DateFormat;

public class Time {
	
	public static final int YESTERDAY = 0x01;
	public static final int TODAY 	  = 0x02;
	public static final int TOMORROW  = 0x03;
	private static final String DAY_OF_WEEK[] = {"","Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"};
	
	private Calendar _calendar;
	
	public Time(Calendar calendar) {
		_calendar = calendar;
	}
	
	public String getTime() {
		StringBuffer buffer = new StringBuffer();
		DateFormat.getInstance(DateFormat.TIME_MEDIUM).format(_calendar,buffer,null);
		return buffer.toString();
	}
	
	public String getTimeLong() {
		StringBuffer buffer = new StringBuffer();
		DateFormat.getInstance(DateFormat.TIME_LONG).format(_calendar,buffer,null);
		return buffer.toString();
	}
	
	public String getString() {
		String s = DAY_OF_WEEK[_calendar.get(Calendar.DAY_OF_WEEK)];
		
		int hour = getHours();
		if(hour > 21 || hour < 6)
			s += " Night";
		else if(hour >= 6 && hour < 12)
			s += " Morning";
		else if(hour >= 12 && hour < 13)
			s += " Noon";
		else if(hour >= 13 && hour < 17)
			s += " Afternoon";
		else if(hour >= 17 && hour < 21)
			s += " Evening";
		
		return s;
	}
	
	public int getHours() {
		return _calendar.get(Calendar.HOUR_OF_DAY);
	}
	
	public int getMinutes() {
		return _calendar.get(Calendar.MINUTE);
	}
	
	public int getSeconds() {
		return _calendar.get(Calendar.SECOND);
	}
	
	public boolean getAM() {
		return _calendar.get(Calendar.HOUR_OF_DAY) < 12;
	}
	
	public int getDay() {
		Calendar local = Calendar.getInstance(TimeZone.getDefault());
		if(local.get(Calendar.YEAR) > _calendar.get(Calendar.YEAR) ||
		   local.get(Calendar.MONTH) > _calendar.get(Calendar.MONTH) ||
		   local.get(Calendar.DAY_OF_MONTH) > _calendar.get(Calendar.DAY_OF_MONTH))
		   return YESTERDAY;
		else if(local.get(Calendar.YEAR) < _calendar.get(Calendar.YEAR) ||
		   local.get(Calendar.MONTH) < _calendar.get(Calendar.MONTH) ||
		   local.get(Calendar.DAY_OF_MONTH) < _calendar.get(Calendar.DAY_OF_MONTH))
		   return TOMORROW;
	   else return TODAY;
	}
	
}
