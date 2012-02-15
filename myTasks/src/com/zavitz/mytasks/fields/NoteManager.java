package com.zavitz.mytasks.fields;

import com.zavitz.mytasks.elements.*;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.ui.container.VerticalFieldManager;

public class NoteManager extends VerticalFieldManager {

	Task task;
	LabelField empty;

	public NoteManager(Task task) {
		empty = new LabelField("No notes saved.") { 
			public void paint(Graphics graphics) {
				graphics.setColor(0x00777777);
				super.paint(graphics);
			}
		};
		for(int i = task.getNotes().length - 1; i >= 0; i--)
			add(new NoteField(task.getNote(i)));
		if(getFieldCount() == 0)
			add(empty);
	}
	
	public void add(String s) {
		if(getField(0) == empty)
			super.delete(empty);
		NoteField noteField = new NoteField(s);
		insert(noteField, 0);
		noteField.setFocus();
	}
	
	public void add(Field f) {
		if(getFieldCount() > 0 && getField(0) == empty)
			super.delete(empty);
		super.add(f);
	}
	
	public void delete(Field f) {
		super.delete(f);
		if(getFieldCount() == 0)
			add(empty);
	}

}
