package com.zavitz.mytasks.fields;

import com.zavitz.mytasks.elements.Project;

import net.rim.device.api.ui.*;
import net.rim.device.api.ui.container.VerticalFieldManager;

public class ProjectManager extends VerticalFieldManager {

	EmptyField emptyLabel;
	Screen parent;
	
	public ProjectManager(Screen parent) {
		this.parent = parent;
		
		emptyLabel = new EmptyField(parent, EmptyField.PROJECT);
		super.add(emptyLabel);
	}
	
	public void delete(Field field) {
		super.delete(field);
		if(getFieldCount() == 0) {
			super.add(emptyLabel);
		}
	}
	
	public void add(Field field) {
		if(getFieldCount() > 0 && getField(0) == emptyLabel)
			super.delete(emptyLabel);
		super.add(field);
	}
	
	public void setFocus(Project project) {
		for(int i = 0; i < getFieldCount(); i++) {
			ProjectField temp = (ProjectField) getField(i);
			if(temp.getProject() == project)
				temp.setFocus();
		}
	}

}
