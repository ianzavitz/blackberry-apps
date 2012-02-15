package com.zavitz.mytasks;

import java.util.Vector;
import net.rim.device.api.util.Persistable;

public class ProjectVector extends Vector implements Persistable {
	
	public void addElement(Object object) {
		super.addElement(object);
		PersistentUtils.save();
	}
	
	public void addElement(Object object, boolean flag) {
		super.addElement(object);
	}
	
	public boolean removeElement(Object object) {
		boolean flag = super.removeElement(object);
		PersistentUtils.save();
		System.out.println();
		return flag;
	}
	
	public void removeElementAt(int i) {
		super.removeElementAt(i);
		PersistentUtils.save();
	}
	
}
