package com.zavitz.mytasks.elements;

import javax.microedition.pim.Event;

import com.zavitz.mytasks.functions.CalUtils;

import net.rim.device.api.util.*;

public class Task implements Persistable {

	/* STATUS */
	public static final int NOT_STARTED = 0;
	public static final int IN_PROGRESS = 1;
	public static final int WAITING = 2;
	public static final int DEFERRED = 3;
	public static final int COMPLETED = 4;

	/* PRIORITY */
	public static final int LOW_PRIORITY = 2;
	public static final int NORMAL_PRIORITY = 1;
	public static final int HIGH_PRIORITY = 0;

	/* TASK DATA */
	private String name;
	private String description;
	private int priority;
	private int color;
	private int status;
	private int percent_complete;
	private long date_due;
	private long reminder;
	private String[] notes;
	private String[] tags;

	private String uID;

	public Task() {
		name = "";
		description = "";
		priority = NORMAL_PRIORITY;
		color = 0x00FFFFFF;
		status = NOT_STARTED;
		percent_complete = 0;
		date_due = 0L;
		reminder = 0L;
		notes = new String[0];
		tags = new String[0];
		uID = "";
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public int getPriority() {
		return priority;
	}

	public int getColor() {
		return color;
	}

	public int getStatus() {
		return status;
	}
	
	public int getPercentComplete() {
		return percent_complete;
	}

	public long getDateDue() {
		return date_due;
	}

	public long getReminder() {
		return reminder;
	}

	public String[] getNotes() {
		return notes;
	}

	public String getNote(int index) {
		return notes[index];
	}
	
	public String[] getTags() {
		return tags;
	}
	
	public String getTag(int index) {
		return tags[index];
	}

	public String getUID() {
		return uID;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public void setStatus(int status) {
		if(status == COMPLETED)
			percent_complete = 100;
		else if(status == NOT_STARTED)
			percent_complete = 0;
		this.status = status;
	}
	
	public void setPercentComplete(int percent_complete) {
		this.percent_complete = percent_complete;
	}

	public void setDateDue(long date_due) {
		this.date_due = date_due;
	}

	public void setReminder(long reminder) {
		this.reminder = reminder;
	}

	public void addNote(String note) {
		Arrays.add(notes, note);
		System.out.println("Added " + note + " length : " + notes.length);
	}

	public void addVoiceNote(String path) {
		Arrays.add(notes, "VN:" + path);
	}

	public void addNotes(String[] notes) {
		for (int i = 0; i < notes.length; i++)
			Arrays.add(this.notes, notes[i]);
	}

	public void deleteVoiceNote(String path) {
		deleteNote("VN:" + path);
	}

	public void deleteNote(String note) {
		for (int i = 0; i < notes.length; i++)
			if (notes[i].equals(note)) {
				Arrays.remove(notes, notes[i]);
				return;
			}
	}

	public void editNote(int index, String note) {
		notes[index] = note;
	}

	public void addTags(String[] tag) {
		for(int i = 0; i < tag.length; i++)
			if(!Arrays.contains(tags, tag[i]))
				Arrays.add(tags, tag[i]);
	}
	
	public void addTag(String tag) {
		if(!Arrays.contains(tags, tag))
			Arrays.add(tags, tag);
	}
	
	public void deleteTag(String tag) {
		for(int i = 0; i < tags.length; i++)
			if(tags[i].equals(tag)) {
				Arrays.removeAt(tags, i);
				return;
			}
	}

	public void setUID(String uID) {
		this.uID = uID;
	}

	public void updateEvent() {
		Event event = CalUtils.getEvent(this);
		if (getDateDue() > 0) {
			if (event == null)
				event = CalUtils.createEvent(this);
			else
				CalUtils.updateEvent(event, this);
		} else if (event != null)
			CalUtils.deleteEvent(event);
	}

}