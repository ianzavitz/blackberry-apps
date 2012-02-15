package com.zavitz.fml.fields;

import java.util.Calendar;
import java.util.TimeZone;

import com.zavitz.fml.data.Config;
import com.zavitz.fml.data.FMLComments.Comment;

import net.rim.device.api.i18n.DateFormat;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.ui.container.*;

public class CommentField extends HorizontalFieldManager {
	
	private static final int LINE_COLOR = 0x009d9d9d;
	private static final int WHITE = Config.WHITE;
	private static final int GREY = 0x00e0e0e0;
	
	private Comment _comment;
	
	public CommentField(Comment comment) {
		super(FOCUSABLE);
		_comment = comment;
		
		LabelField author = new LabelField("#" + comment.id + " by " + comment.author) {
			public void paint(Graphics g) {
				g.setColor(Config.GRAY);
				super.paint(g);
			}
			public boolean isSelectionCopyable() {
				return false;
			}
		};
		
		LabelField date = new LabelField("Posted: " + getDate()) {
			public void paint(Graphics g) {
				g.setColor(0x00666666);
				super.paint(g);
			}
			public boolean isSelectionCopyable() {
				return false;
			}
		};
		
		RichTextField text = new RichTextField(comment.text) {
			public void paint(Graphics g) {
				g.setColor(Config.WHITE);
				g.fillRect(0, 0, Display.getWidth(), Display.getHeight());
				g.setColor(Config.GRAY);
				super.paint(g);
			}
			public boolean isSelectionCopyable() {
				return false;
			}
		};
		text.setFont(text.getFont().derive(Font.PLAIN, Config.FONT_SIZE));
		author.setFont(text.getFont());
		date.setFont(author.getFont());
		
		add(author);
		add(date);
		add(text);
	}
	
	public String getDate() {
		try {
			Calendar cal = parse(_comment.date);
			StringBuffer buffer = new StringBuffer();
			DateFormat.getInstance(DateFormat.DATE_MEDIUM | DateFormat.TIME_LONG).formatLocal(buffer,cal.getTime().getTime());
			
			return buffer.toString();
		} catch(Exception e) {
			return e.getMessage();
		}
	}
	
	public static Calendar parse(final String text) {
		String time = text;
		if (time == null) {
			throw new IllegalArgumentException("argument may not be null");
		}
		time = time.trim();
		char sign;
		int curPos;
		if (time.startsWith("-")) {
			sign = '-';
			curPos = 1;
		} else if (time.startsWith("+")) {
			sign = '+';
			curPos = 1;
		} else {
			sign = '+'; // no sign specified, implied '+'
			curPos = 0;
		}

		int year, month, day, hour, min, sec, ms;
		String tzID;
		char delimiter;
		try {
			year = Integer.parseInt(time.substring(curPos, curPos + 4));
		} catch (NumberFormatException e) {
			return Calendar.getInstance();
		}
		curPos += 4;
		delimiter = '-';
		if (time.charAt(curPos) != delimiter) {
			return Calendar.getInstance();
		}
		curPos++;
		try {
			month = Integer.parseInt(time.substring(curPos, curPos + 2));
		} catch (NumberFormatException e) {
			return Calendar.getInstance();
		}
		curPos += 2;
		delimiter = '-';
		if (time.charAt(curPos) != delimiter) {
			return Calendar.getInstance();
		}
		curPos++;
		try {
			day = Integer.parseInt(time.substring(curPos, curPos + 2));
		} catch (NumberFormatException e) {
			return Calendar.getInstance();
		}
		curPos += 2;
		delimiter = 'T';
		if (time.charAt(curPos) != delimiter) {
			return Calendar.getInstance();
		}
		curPos++;
		try {
			hour = Integer.parseInt(time.substring(curPos, curPos + 2));
		} catch (NumberFormatException e) {
			return Calendar.getInstance();
		}
		curPos += 2;
		delimiter = ':';
		if (time.charAt(curPos) != delimiter) {
			return Calendar.getInstance();
		}
		curPos++;
		try {
			min = Integer.parseInt(time.substring(curPos, curPos + 2));
		} catch (NumberFormatException e) {
			return Calendar.getInstance();
		}
		curPos += 2;
		delimiter = ':';
		if (time.charAt(curPos) != delimiter) {
			return Calendar.getInstance();
		}
		curPos++;
		try {
			sec = Integer.parseInt(time.substring(curPos, curPos + 2));
		} catch (NumberFormatException e) {
			return Calendar.getInstance();
		}
		curPos += 2;
		delimiter = '.';
		if (curPos < time.length() && time.charAt(curPos) == '.') {
			curPos++;
			try {
				ms = Integer.parseInt(time.substring(curPos, curPos + 3));
			} catch (NumberFormatException e) {
				return Calendar.getInstance();
			}
			curPos += 3;
		} else {
			ms = 0;
		}
		// time zone designator (Z or +00:00 or -00:00)
		if (curPos < time.length()
				&& (time.charAt(curPos) == '+' || time.charAt(curPos) == '-')) {
			// offset to UTC specified in the format +00:00/-00:00
			tzID = "GMT" + time.substring(curPos);
		} else if (curPos < time.length() && time.substring(curPos).equals("Z")) {
			tzID = "GMT";
		} else {
			// throw new ParseException("invalid time zone designator", curPos);
			tzID = "GMT";
		}

		TimeZone tz = TimeZone.getTimeZone(tzID);
		// verify id of returned time zone (getTimeZone defaults to "GMT")
		if (!tz.getID().equals(tzID)) {
			return Calendar.getInstance();
		}

		// initialize Calendar object
		Calendar cal = Calendar.getInstance(tz);
		if (sign == '-' || year == 0) {
			// not CE, need to set era (BCE) and adjust year
			cal.set(Calendar.YEAR, year + 1);
		} else {
			cal.set(Calendar.YEAR, year);
		}
		cal.set(Calendar.MONTH, month - 1); // month is 0-based
		cal.set(Calendar.DAY_OF_MONTH, day);
		cal.set(Calendar.HOUR_OF_DAY, hour);
		cal.set(Calendar.MINUTE, min);
		cal.set(Calendar.SECOND, sec);
		cal.set(Calendar.MILLISECOND, ms);

		// the following will trigger an IllegalArgumentException if any of
		// the set values are illegal or out of range
		cal.getTime();

		return cal;
	}
	
	public Comment getComment() {
		return _comment;
	}
	
	public int getPreferredWidth() {
		return Display.getWidth();
	}
	
	public int getPreferredHeight() {
		int i = 20 + (getFieldCount() - 1) * 2;
		for(int j = 0; j < getFieldCount(); j++)
			if(getField(j).getContentHeight() > 300)
				i += 300;
			else
				i += getField(j).getContentHeight();
		return i;
	}
	
	public void paint(Graphics g) {
		g.setColor(WHITE);
		g.fillRoundRect(5, 5, getPreferredWidth() - 10, getContentHeight() - 10, 15, 15);
		subpaint(g);
	}

	protected void sublayout(int wh, int hw) {
		int h = 10;
		
		for(int i = 0; i < getFieldCount(); i++) {
			Field f = getField(i);
			layoutChild(f, getPreferredWidth() - 20, f.getPreferredHeight() > 300 ? 300 : f.getPreferredHeight());
			setPositionChild(f, 10, h);
			h += f.getPreferredHeight() > 300 ? 300 : f.getPreferredHeight() + 2;
		}
		
		setExtent(getPreferredWidth(), getPreferredHeight());
	}
	
	public boolean isFocus() {
		return getFieldWithFocus() != null;
	}

}
