package com.zavitz.mytimes;

import java.util.Vector;

import net.rim.device.api.system.Characters;
import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.*;

public class TimeListField extends ObjectListField {

	private AddTimeZone tZone;
	
	public TimeListField(AddTimeZone zone) {
		tZone = zone;
		setRowHeight(15);
		setFont(getFont().derive(Font.PLAIN,13));
		set(TimeUtils.TIMEZONES);
		setEmptyString("No matching time zones", 0);
	}
	
	public void setTerm(String s) {
		s = s.toLowerCase();
		
		Vector v = new Vector();
		for(int i = 0; i < TimeUtils.TIMEZONES.length; i++) {
			if(TimeUtils.TIMEZONES[i].toLowerCase().indexOf(s) > -1)
				v.addElement(TimeUtils.TIMEZONES[i]);
		}
		
		String items[] = new String[v.size()];
		v.copyInto(items);
		set(items);		
	}
	
	public void set(Object[] object) {
		for(int i = 0; i < object.length; i++)
			if(object[i] instanceof String)
				object[i] = Utils.replace((String)object[i], "_", " ");
		super.set(object);
	}
	
	public void paint(Graphics g) {
		g.setColor(0x00FFFFFF);
		super.paint(g);
	}
	
	public boolean keyChar(char key, int status, int time) {
		if(key == Characters.ENTER && getSize() != 0) {
    		tZone.zonePressed();
			return true;
		}
		
		return tZone.searchTerm.keyChar(key,status,time);
	}
	
	public boolean navigationClick(int status, int time) {
		tZone.zonePressed();
		return true;
	}
	
}
