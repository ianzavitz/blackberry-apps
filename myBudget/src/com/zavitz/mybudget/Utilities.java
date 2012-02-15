package com.zavitz.mybudget;

import java.util.*;
import com.zavitz.mybudget.elements.*;
import net.rim.device.api.i18n.SimpleDateFormat;
import net.rim.device.api.util.Arrays;

public class Utilities {

	public static final int MONTH = 0;
	public static final int WEEK = 1;
	public static final int YEAR = 2;

	public static final String MDY = "MM-dd-yyyy";
	public static final String MY = "MM-yyyy";

	/* Calendar Utilities */

	public static long getMonthStartLong() {
		return getMonthStartDate().getTime();
	}
	
	public static void addWeek(Calendar calendar) {
		calendar.setTime(new Date(calendar.getTime().getTime() + (7 * (1000 * 60 * 60 * 24))));
	}
	
	public static void addMonth(Calendar calendar) {
		int current = calendar.get(Calendar.MONTH);
		if(current < Calendar.DECEMBER) {
			calendar.set(Calendar.MONTH, current++);
		} else {
			calendar.set(Calendar.YEAR, calendar.get(YEAR) + 1);
			calendar.set(Calendar.MONTH, Calendar.JANUARY);
		}
	}
	
	public static void addYear(Calendar calendar) {
		calendar.set(Calendar.YEAR, calendar.get(YEAR) + 1);
	}
	
	public static Date getNextHour() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date(calendar.getTime().getTime() + 3610000));
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}
	
	public static Date getMonthStartDate() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}

	public static Date getWeekStartDate() {
		Calendar calendar = Calendar.getInstance();
		int sunday = calendar.get(Calendar.DAY_OF_MONTH);
		while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY)
			calendar.set(Calendar.DAY_OF_MONTH, sunday--);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}

	public static String getFormatted(long l, String pattern) {
		SimpleDateFormat f = new SimpleDateFormat(pattern);
		return f.formatLocal(l);
	}

	public static long parseFormatted(String s, String pattern) {
		Calendar calendar = Calendar.getInstance();
		if (pattern.equals(MDY)) {
			int m = Integer.parseInt(s.substring(0, 2));
			int d = Integer.parseInt(s.substring(3, 5));
			int y = Integer.parseInt(s.substring(6, 10));
			calendar.set(Calendar.MONTH, m - 1);
			calendar.set(Calendar.DAY_OF_MONTH, d);
			calendar.set(Calendar.YEAR, y);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			return calendar.getTime().getTime();
		} else if (pattern.equals(MY)) {
			int m = Integer.parseInt(s.substring(0, 2));
			int y = Integer.parseInt(s.substring(3, 7));
			calendar.set(Calendar.MONTH, m - 1);
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			calendar.set(Calendar.YEAR, y);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			return calendar.getTime().getTime();
		}
		return 0;
	}

	/* Budget Utilities */

	/* Checks a list of items to see if they have expired */
	public static boolean budgetCheck(ActiveManager activeManager,
			ArchiveManager archiveManager) {
		Budget[] itemsToMove = new Budget[0];

		Calendar calendar = Calendar.getInstance();
		long curTime = calendar.getTime().getTime();
		
		for(int i = 0; i < activeManager.getLength(); i++) {
			if(activeManager.getBudgetItem(i).getEnd() <= curTime) {
				Arrays.add(itemsToMove, activeManager.getBudgetItem(i));
			}
		}
		
		if (itemsToMove.length > 0) {
			activeManager.addSavings(itemsToMove);
			archiveManager.addBudgets(itemsToMove);
			activeManager.resetItems(itemsToMove);
			activeManager.deleteAll();
			for(int i = 0; i < itemsToMove.length; i++)
				if(itemsToMove[i].getRecurring())
					activeManager.addBudgetItem(itemsToMove[i]);
			UiApp.save();
			return true;
		}
		return false;
	}

	public static String formatDouble(double d) {
		String s = String.valueOf(d);
		if (s.endsWith(".0"))
			return insertCommas(String.valueOf((int) d));
		String dec = s.substring(s.indexOf(".") + 1);
		if (dec.length() == 1)
			return insertCommas(s + "0");
		else
			return insertCommas(s);
	}

	public static String formatDoubleNoCommas(double d) {
		String s = String.valueOf(d);
		if (s.endsWith(".0"))
			return String.valueOf((int) d);
		String dec = s.substring(s.indexOf(".") + 1);
		if (dec.length() == 1)
			return s + "0";
		else
			return s;
	}
	
	private static String insertCommas(String input) {
		boolean negative = input.startsWith("-");
		if(negative)
			input = input.substring(1);
		String[] split = new String[2];
		if(input.indexOf(".") > -1) {
			split[0] = input.substring(0,input.indexOf("."));
			split[1] = input.substring(input.indexOf("."));
		} else {
			split[0] = input;
			split[1] = "";
		}
		StringBuffer buff = new StringBuffer(split[0]);
		int pos = buff.length() - 3;
		while(pos < buff.length() && pos > 0) {
			buff.insert(pos, ",");
			pos -= 3;
		}
		return (negative ? "-" : "") + buff.toString() + split[1];
	}

}
