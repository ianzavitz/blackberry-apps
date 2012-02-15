package com.zavitz.mytasks;

import java.util.Date;
import java.util.Vector;

import net.rim.device.api.i18n.SimpleDateFormat;
import net.rim.device.api.util.Persistable;

public class TodoItem2 implements Persistable {
	
	private boolean checked;
	private String title;
	private String description;
	private String color;
	private long dueDate = 0;
	private Vector notes;
	private String uid;
	private long reminder;

	public static final int TODAY = 0x001;
	public static final int TOMORROW = 0x002;
	public static final int TWO_PLUS = 0x003;
	public static final int NO_DATE = 0x004;
	public static final int EXPIRED = 0x005;
	public static final int COMPLETE = 0x006;
	
	public TodoItem2(String pTitle, String pDesc) {
		title = pTitle;
		description = pDesc;
		checked = false;
		color = "White";
		notes = new Vector();
	}
	
	public TodoItem2(TodoItem item) {
		checked = item.getChecked();
		title = new String(item.getTitle());
		description = new String(item.getDescription());
		color = new String(item.getColor());
		dueDate = item.getRawDate();
		notes = new Vector();
		while(item.getNoteVector().size() > 0) {
			notes.addElement(item.getNoteVector().elementAt(0));
			item.getNoteVector().removeElementAt(0);
		}
	}

	public TodoItem2(String pTitle, String pDesc, String c) {
		title = pTitle;
		description = pDesc;
		checked = false;
		color = c;
		notes = new Vector();
	}
	
	public TodoItem2(String pTitle, String pDesc, String c, Date d) {
		title = pTitle;
		description = pDesc;
		checked = false;
		color = c;
		dueDate = d.getTime();
	}
	
	public TodoItem2(String pTitle, String pDesc, boolean flag) {
		title = pTitle;
		description = pDesc;
		checked = flag;
		color = "White";
		notes = new Vector();
	}
	
	public TodoItem2(String pTitle, String pDesc, boolean flag, String c) {
		title = pTitle;
		description = pDesc;
		checked = flag;
		color = c;
		notes = new Vector();
	}
	
	public TodoItem2(String s) {
		String parsed[] = Utils.split(s,"|");
		for(int i = 0; i < parsed.length; i++) {
			switch(i) {
			case 0: // Title
				title = parsed[i];
				break;
			case 1: // Description
				description = parsed[i];
				break;
			case 2: // Color
				color = parsed[i];
				break;
			case 3: // Completed
				checked = parsed[i].equals("true");
				break;
			case 4: // Date
				if(!parsed[i].equals("null"))
					dueDate = Long.parseLong(parsed[i]);
				else
					dueDate = 0;
				break;
			case 5: // Notes : parsed by \\
				String parsed_notes[] = Utils.split(parsed[i], "\\");
				notes = new Vector();
				for(int j = 0; j < parsed_notes.length; j++) {
					Note note;
					if(parsed_notes[j].startsWith("[VOICE]"))
						note = new Note(parsed_notes[j].substring(7),Note.VOICE);
					else
						note = new Note(parsed_notes[j]);
					notes.addElement(note);
				}
			}
		}
	}
	
	public void setReminder(long l) {
		reminder = l;
	}
	
	public long getReminder() {
		return reminder;
	}
	
	public void setUID(String s) {
		uid = s;
	}
	
	public String getUID() {
		return uid;
	}
	
	public void addVoiceNote(String path) {
		notes.addElement(new Note(path,Note.VOICE));
		PersistentUtils.save();
	}
	
	public void addNote(String note) {
		notes.addElement(new Note(note,Note.DEFAULT));
		PersistentUtils.save();
	}
	
	public void editNote(Note note, String s) {
		note.set(s);
		PersistentUtils.save();
	}
	
	public void deleteNote(Note note) {
		notes.removeElement(note);
		PersistentUtils.save();
	}
	
	public int getDateType() {
		if(getChecked())
			return COMPLETE;
		else if(dueDate == 0)
			return NO_DATE;
		else if(dueDate < System.currentTimeMillis())
			return EXPIRED;
		else if(getDateFormatted("MM-dd-yyyy").equals(Utils.getDateFormatted("MM-dd-yyyy")))
			return TODAY;
		else if(getDateFormatted("MM-dd-yyyy").equals(Utils.getDateFormatted("MM-dd-yyyy",24 * 60 * 60 * 1000)))
			return TOMORROW;
		else
			return TWO_PLUS;
	}
	
	public void setDate(Date d) {
		if(d == null)
			dueDate = 0;
		else
			dueDate = d.getTime();
		PersistentUtils.save();
	}
	
	public Date getDate() {
		return new Date(dueDate);
	}
	
	public long getRawDate() {
		return dueDate;
	}
	
	public long getTimeDiff() {
		return dueDate - System.currentTimeMillis();
	}

	public String getDateFormatted(String pattern) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
		return dateFormat.formatLocal(dueDate);
	}
	
	public String getDateFormatted(int i) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(i);
		return dateFormat.formatLocal(dueDate);
	}
	
	public void setTitle(String pTitle) {
		title = pTitle;
		PersistentUtils.save();
	}
	
	public void setDescription(String pDesc) {
		description = pDesc;
		PersistentUtils.save();
	}
	
	public void setChecked(boolean flag) {
		checked = flag;
		PersistentUtils.save();
	}
	
	public void setColor(String c) {
		color = c;
		PersistentUtils.save();
	}
	
	public int getColorHex() {
		String c = color;
		if(c.indexOf("White") >= 0)
			return 0x00CCCCCC;
		else if(c.indexOf("Red") >= 0)
			return 0x00FF0000;
		else if(c.indexOf("Green") >= 0)
			return 0x0002d111;
		else if(c.indexOf("Orange") >= 0)
			return 0x00ff9600;
		else if(c.indexOf("Yellow") >= 0)
			return 0x00f5e700;
		else if(c.indexOf("Teal") >= 0)
			return 0x0000d4e7;
		else if(c.indexOf("Pink") >= 0)
			return 0x00e700df;
		else return 0x00CCCCCC;
	}
	
	public void set(TodoItem2 item) {
		title = item.getTitle();
		description = item.getDescription();
		checked = item.getChecked();
		color = item.getColor();
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getDescription() {
		return description.length() > 0 ? description  : "No description";
	}
	
	public String getRawDescription() {
		return description;
	}
	
	public boolean getChecked() {
		return checked;
	}
	
	public String getColor() {
		return color;
	}
	
	public Vector getNoteVector() {
		return notes;
	}
	
	private String getNotes() {
		String s = "";
		for(int i = 0; i < notes.size(); i++)
			s += ((Note)notes.elementAt(i)).getTrueNote() + (i + 1 == notes.size() ? "" : "\\");
		return s;
	}
	
	public String toString() {
		return  title + "|" + 
				description + "|" +
				color + "|" + 
				checked + "|" + 
				(dueDate != 0 ?  "" + dueDate : "0") + "|" +
				getNotes();
	}
	
}
