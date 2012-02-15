package com.zavitz.mytasks.fields;

import java.util.*;
import com.zavitz.mytasks.*;
import com.zavitz.mytasks.elements.Project;
import com.zavitz.mytasks.elements.Task;
import net.rim.device.api.ui.*;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Comparator;

public class TaskManager extends VerticalFieldManager {
 
	private Comparator compareType;
	Timer paintTimer;
	EmptyField emptyLabel;
	Screen parent;
	
	public TaskManager(Screen parent) {
		compareType = dateDue;
		this.parent = parent;
		
		emptyLabel = new EmptyField(parent, EmptyField.TASK);
		if(parent instanceof ProjectScreen) {
			switch(((ProjectScreen)parent).getProject().getSortType()) {
			case Project.SORT_DATE:
				compareType = dateDue;
				break;
			case Project.SORT_NAME:
				compareType = name;
				break;
			case Project.SORT_PRIORITY:
				compareType = priority;
				break;
			case Project.SORT_STATUS:
				compareType = status;
				break;
			case Project.SORT_MANUAL:
				compareType = null;
			}
			super.add(emptyLabel);
		}
		
		paintTimer = new Timer();
		TimerTask task = new TimerTask() {
			public void run() {
				paintTimerEvent();
			}
		};
		paintTimer.scheduleAtFixedRate(task, 2000, 2000);
	}
	
	public void paintTimerEvent() {
		if(getFieldCount() > 0 && getFieldWithFocus() instanceof TaskField)
			((TaskField) getFieldWithFocus()).alternate();
	}
	
	public void add(Task[] t) {
		if(t.length == 0)
			return;
		if(getFieldCount() > 0 && getField(0) == emptyLabel)
			super.delete(emptyLabel);
		for(int i = 0; i < t.length; i++)
			super.add(new TaskField(t[i]));
		sortBy(compareType);
	}
	
	public void add(Field[] f) {
		if(f.length == 0)
			return;
		if(getFieldCount() > 0 && getField(0) == emptyLabel)
			super.delete(emptyLabel);
		for(int i = 0; i < f.length; i++)
			super.add(f[i]);
		sortBy(compareType);
	}
	
	public void add(Field f) {
		if(getFieldCount() > 0 && getField(0) == emptyLabel)
			super.delete(emptyLabel);
		super.add(f);
		sortBy(compareType);
	}
	
	public void delete(Field f) {
		super.delete(f);
		if(getFieldCount() == 0) {
			super.add(emptyLabel);
		}
	}
	
	public void setFocus(Task task) {
		for(int i = 0; i < getFieldCount(); i++) {
			TaskField temp = (TaskField) getField(i);
			if(temp.getTask() == task)
				temp.setFocus();
		}
	}
	
	public void sort() {
		switch(((ProjectScreen)parent).getProject().getSortType()) {
		case Project.SORT_DATE:
			compareType = dateDue;
			break;
		case Project.SORT_NAME:
			compareType = name;
			break;
		case Project.SORT_PRIORITY:
			compareType = priority;
			break;
		case Project.SORT_STATUS:
			compareType = status;
			break;
		case Project.SORT_MANUAL:
			compareType = null;
		}
		sortBy(compareType);
	}
	
	private void sortBy(Comparator c) {		
		if(c == null || getFieldCount() <= 1 || parent instanceof SearchScreen) {
			return;
		}
		
		TaskField[] tasks = new TaskField[getFieldCount()];
		for(int i = 0; i < tasks.length; i++)
			tasks[i] = (TaskField) getField(i);
		Arrays.sort(tasks, c);

		Field focus = getFieldWithFocus();
		deleteAll();
		for(int i = 0; i < tasks.length; i++)
			super.add(tasks[i]);
		
		if(focus != null)
			focus.setFocus();
	}
	
	Comparator name = new Comparator() {
		public int compare(Object o1, Object o2) {
			if(o1 == null && o2 == null)
				return 0;
			else if(o1 == null && o2 != null)
				return 1;
			else if(o1 != null && o2 == null)
				return -1;
			Task t1 = ((TaskField) o1).getTask();
			Task t2 = ((TaskField) o2).getTask();
			return t1.getName().compareTo(t2.getName());
		}
	};
	
	Comparator dateDue = new Comparator() {
		public int compare(Object o1, Object o2) {
			if(o1 == null && o2 == null)
				return 0;
			else if(o1 == null && o2 != null)
				return 1;
			else if(o1 != null && o2 == null)
				return -1;
			Task t1 = ((TaskField) o1).getTask();
			Task t2 = ((TaskField) o2).getTask();
			if(t1.getDateDue() < t2.getDateDue())
				return -1;
			else if(t1.getDateDue() == t2.getDateDue())
				return name.compare(o1, o2);
			else
				return 1;
		}
	};
	
	Comparator status = new Comparator() {
		public int compare(Object o1, Object o2) {
			if(o1 == null && o2 == null)
				return 0;
			else if(o1 == null && o2 != null)
				return 1;
			else if(o1 != null && o2 == null)
				return -1;
			Task t1 = ((TaskField) o1).getTask();
			Task t2 = ((TaskField) o2).getTask();
			if(t1.getStatus() < t2.getStatus())
				return -1;
			else if(t1.getStatus() == t2.getStatus())
				return dateDue.compare(o1, o2);
			else
				return 1;
		}
	};

	Comparator priority = new Comparator() {
		public int compare(Object o1, Object o2) {
			if(o1 == null && o2 == null)
				return 0;
			else if(o1 == null && o2 != null)
				return 1;
			else if(o1 != null && o2 == null)
				return -1;
			Task t1 = ((TaskField) o1).getTask();
			Task t2 = ((TaskField) o2).getTask();
			if(t1.getPriority() < t2.getPriority())
				return -1;
			else if(t1.getPriority() == t2.getPriority())
				return name.compare(o1, o2);
			else
				return 1;
		}
	};
	
}