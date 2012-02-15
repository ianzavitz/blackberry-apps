package com.zavitz.mybudget.elements;

import java.util.Calendar;
import java.util.Date;

import net.rim.device.api.util.Arrays;

import com.zavitz.mybudget.Utilities;

public class Income {
	
	private String 			name;
	private double 			amount;
	private long			start;
	private int 			length[];
	private boolean			recurring;
	
	public Income() {
		name = "";
		amount = 0.0;
		start = 0L;
		length = new int[3];
	}
	
	public void setName(String _name) {
		name = _name;
	}
	
	public void setAmount(double _amount) {
		amount = _amount;
	}
	
	public void setLength(int[] _length) {
		length = _length;
	}
	
	public void setLength(int week, int month, int year) {
		length = new int[] {week,month,year};
	}
	
	public String getCycleEnd() {
		String s = "(ends " + Utilities.getFormatted(getEnd(), Utilities.MDY) + ")";
		return s;
	}
	
	public void setLength(String s) {
		length = new int[3];
		
		for(int i = 0; i < 3; i++) {
			if(s.indexOf(":") > -1) {
				length[i] = Integer.parseInt(s.substring(0, s.indexOf(":")));
				s = s.substring(s.indexOf(":") + 1);
			} else 
				length[i] = Integer.parseInt(s);
		}
	}
	
	public void setStart(long _start) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date(_start));
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		start = calendar.getTime().getTime();
	}
	
	public void setRecurring(boolean flag) {
		recurring = flag;
	}
	
	public String getName() {
		return name;
	}
	
	public long getEnd() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date(start));
		for(int i = 0; i < length[0]; i++)
			cal.setTime(new Date(cal.getTime().getTime() + (7 * (1000 * 60 * 60 * 24))));
		for(int i = 0; i < length[1]; i++) {
			int currentMonth = cal.get(Calendar.MONTH);
			int currentDay = cal.get(Calendar.DAY_OF_MONTH);
			if(currentDay > 28)
				cal.set(Calendar.DAY_OF_MONTH, 28);
			if(currentMonth < Calendar.DECEMBER) {
				cal.set(Calendar.MONTH, currentMonth + 1);
			} else {
				cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) + 1);
				cal.set(Calendar.MONTH, Calendar.JANUARY);
			}
		}
		for(int i = 0; i < length[2]; i++)
			cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) + 1);
		return cal.getTime().getTime();
	}
	
	public double getAmount() {
		return amount;
	}
	
	public long getStart() {
		return start;
	}
	
	public int[] getLength() {
		return length;
	}
	
	public boolean getRecurring() {
		return recurring;
	}
	 
	public String serialize() {
		String s = "<income>";
		s += "<name>" + name + "</name>";
		s += "<start>" + start + "</start>";
		s += "<length>" + length[0] + ":" + length[1] + ":" + length[2] + "</length>";
		s += "<amount>" + amount + "</amount>";
		s += "<recurring>" + recurring + "</recurring>";
		s += "</income>";
		return s;
	}
	
	public Budget reset() {
		Budget newBudget = new Budget();
		newBudget.setName(name);
		newBudget.setAmount(amount);
		newBudget.setLength(length);
		newBudget.setStart(getEnd());
		newBudget.setRecurring(recurring);
		return newBudget;
	}
	
}
