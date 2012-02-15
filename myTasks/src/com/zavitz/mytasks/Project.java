package com.zavitz.mytasks;

import java.util.Vector;

import net.rim.device.api.util.Persistable;

public class Project extends Vector implements Persistable {
	
	public static final int DEFAULT 	= 0x00BEEF;
	public static final int SEQUENTIAL  = 0x00C0CA;
	
	private String title;
	private final int TYPE;
	private ProjectVector subProjects;
	
	public Project() {
		title = "";
		subProjects = new ProjectVector();
		TYPE = DEFAULT;
	}
	
	public Project(int i) {
		title = "";
		subProjects = new ProjectVector();
		TYPE = i;
	}
	
	public Project(String s) {
		title = s;
		subProjects = new ProjectVector();
		TYPE = DEFAULT;
	}
	
	public Project(String s, int i) {
		title = s;
		subProjects = new ProjectVector();
		TYPE = i;
	}
	
	public Project(String s, Vector v) {
		title = s;
		for(int i = 0; i < v.size(); i++)
			super.addElement(v.elementAt(i));
		sort();
		subProjects = new ProjectVector();
		TYPE = DEFAULT;
	}
	
	public Project(Object[] object) {
		for(int i = 0; i < object.length; i++) {
			if(i == 0) {
				setTitle((String)object[0]);
			} else {
				super.addElement(new TodoItem2((String)object[i]));
			}
		}
		subProjects = new ProjectVector();
		TYPE = DEFAULT;
	}
	
	public ProjectVector getProjects() {
		return subProjects;
	}
	
	public int getType() {
		return TYPE;
	}
	
	public void addElement(TodoItem2 item) {
		super.addElement(item);
		PersistentUtils.save();
	}
	
	public void insertElementAt(TodoItem2 item, int index) {
		super.insertElementAt(item,index);
		PersistentUtils.save();
	}

	public boolean removeElement(TodoItem2 item) {
		boolean flag = super.removeElement(item);
		PersistentUtils.save();
		return flag;
	}
	
	public void removeElementAt(int i) {
		super.removeElementAt(i);
		PersistentUtils.save();
	}
	
	public void sort() {
		if(TYPE == DEFAULT) {
			Vector regular_storage = new Vector();
			Vector expired_storage = new Vector();
			Vector no_date_storage = new Vector();
			Vector completed_storage = new Vector();
			
			for(int i = 0; i < size(); i++)
				if(((TodoItem2)elementAt(i)).getChecked())
					completed_storage.addElement(elementAt(i));
				else if(((TodoItem2)elementAt(i)).getDateType() == TodoItem2.NO_DATE)
					no_date_storage.addElement(elementAt(i));
				else if(((TodoItem2)elementAt(i)).getDateType() == TodoItem2.EXPIRED)
					expired_storage.addElement(elementAt(i));
			
			for(int i = 0; i < no_date_storage.size(); i++)
				super.removeElement(no_date_storage.elementAt(i));
			
			for(int i = 0; i < expired_storage.size(); i++)
				super.removeElement(expired_storage.elementAt(i));

			for(int i = 0; i < completed_storage.size(); i++)
				super.removeElement(completed_storage.elementAt(i));
			
			while(!isEmpty()) {
				long shortestTime = Long.MAX_VALUE;
				int index = -1;
				
				for(int i = 0; i < size(); i++) {
					TodoItem2 item = (TodoItem2)elementAt(i);
					if(item.getTimeDiff() < shortestTime) {
						shortestTime = item.getTimeDiff();
						index = i;
					}
				}
				
				if(index > -1) {
					regular_storage.addElement(elementAt(index));
					super.removeElementAt(index);
				}
			}
			
			for(int i = 0; i < regular_storage.size(); i++)
				super.addElement(regular_storage.elementAt(i));
			for(int i = 0; i < no_date_storage.size(); i++)
				super.addElement(no_date_storage.elementAt(i));
			for(int i = 0; i < expired_storage.size(); i++)
				super.addElement(expired_storage.elementAt(i));
			for(int i = 0; i < completed_storage.size(); i++)
				super.addElement(completed_storage.elementAt(i));

			PersistentUtils.save();
		} else if(TYPE == SEQUENTIAL) {	
			Vector incomplete = new Vector();
			Vector complete = new Vector();
			
			while(!isEmpty()) {
				if(((TodoItem2)elementAt(0)).getChecked())
					complete.addElement(elementAt(0));
				else
					incomplete.addElement(elementAt(0));
				super.removeElementAt(0);
			}
			
			for(int i = 0; i < incomplete.size(); i++)
				super.addElement(incomplete.elementAt(i));
			for(int i = 0; i < complete.size(); i++)
				super.addElement(complete.elementAt(i));
			
			PersistentUtils.save();
		}
	}
	
	public void addProject(Project project) {
		subProjects.addElement(project);
		PersistentUtils.save();
	}
	
	public void removeProject(Project project) {
		subProjects.removeElement(project);
		PersistentUtils.save();
	}
	
	public String[] getTitles() {
		String s[] = new String[size()];
		for(int i = 0; i < s.length; i++)
			s[i] = ((TodoItem2)elementAt(i)).getTitle();
		return s;
	}
	
	public Object[] toObject() {
		Object[] object = new Object[size() + 1];
		object[0] = getTitle();
		for(int i = 0; i < size(); i++)
			object[i + 1] = elementAt(i).toString();
		return object;
	}
	
	public void setItem(TodoItem2 oldItem, TodoItem2 newItem) {
		setElementAt(newItem, indexOf(oldItem));
		sort();
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String s) {
		title = s;
	}
	
	public String getCompleted() {
		int done = 0;
		int todo = size();
		for(int i = 0; i < todo; i++)
			if(((TodoItem2)elementAt(i)).getChecked())
				done ++;
		if(todo == 0)
			return "-";
		return "" + done + "/" + todo;
	}
	
	public String toString() {
		return title;
	}
	
}