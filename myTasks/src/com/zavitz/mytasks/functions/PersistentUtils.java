package com.zavitz.mytasks.functions;

import java.util.Vector;

import com.zavitz.mytasks.MTScreen;
import com.zavitz.mytasks.Note;
import com.zavitz.mytasks.ProjectVector;
import com.zavitz.mytasks.TodoItem2;
import com.zavitz.mytasks.elements.Project;
import com.zavitz.mytasks.elements.Task;

import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.util.Arrays;

public class PersistentUtils {

	private static final long KEY = 0x2c7395e574fffdb5L; //com.zavitz.mytasks.v2
	private static final long TRIAL = 0xda7b8bfdc6a4717dL; //com.zavitz.mytasks.
															// trial
	private static PersistentObject persistentStorage, persistentTrial;

	static {
		persistentStorage = PersistentStore.getPersistentObject(KEY);
	}

	public static boolean importOld() {
		PersistentObject oldPersistent = PersistentStore
				.getPersistentObject(0xa13a76a32df94af3L);
		Object contents = oldPersistent.getContents();
		if (contents instanceof ProjectVector) {
			ProjectVector vector = (ProjectVector) contents;
			
			// Set mainProjects so you can add projects
			MTScreen.mainProjects = new com.zavitz.mytasks.elements.Project[0];

			// Go through old main projects
			for (int o = 0; o < vector.size(); o++) {
				com.zavitz.mytasks.Project old = (com.zavitz.mytasks.Project) vector
						.elementAt(o);
				com.zavitz.mytasks.elements.Project newP = new com.zavitz.mytasks.elements.Project();
				newP.setName(old.getTitle());

				// load sub projects of this old main project
				ProjectVector projects = old.getProjects();
				for (int k = 0; k < projects.size(); k++)
					syncProject(newP, (com.zavitz.mytasks.Project) projects
							.elementAt(k));

				// load tasks for this old main project
				for (int i = 0; i < old.size(); i++) {
					if(!(old.elementAt(i) instanceof TodoItem2))
						return false;
					TodoItem2 item = (TodoItem2) old.elementAt(i);
					Task newItem = new Task();
					newItem.setName(item.getTitle());
					newItem.setDescription(item.getDescription());
					newItem.setStatus(item.getChecked() ? Task.COMPLETED
							: Task.NOT_STARTED);
					newItem.setDateDue(item.getRawDate());
					newItem.setReminder(item.getReminder());
					newItem.setColor(item.getColorHex() == 0x00CCCCCC ? 0x00FFFFFF : item.getColorHex());
					newItem.setUID(item.getUID());
					Vector notes = item.getNoteVector();
					for (int j = 0; j < notes.size(); j++) {
						Note note = (Note) notes.elementAt(j);
						String s = note.getTrueNote();
						if (s.startsWith("[VOICE]"))
							s = s.substring("[VOICE]".length()).concat("VN:");
						newItem.addNote(s);
					}
					newP.addTask(newItem);
				}
				Arrays.add(MTScreen.mainProjects, newP);
			}
			oldPersistent.setContents(null);
			save();
			return true;
		}
		return false;
	}

	public static void syncProject(com.zavitz.mytasks.elements.Project parent,
			com.zavitz.mytasks.Project old) {
		com.zavitz.mytasks.elements.Project newP = new com.zavitz.mytasks.elements.Project();
		newP.setName(old.getTitle());
		ProjectVector projects = old.getProjects();
		for (int i = 0; i < projects.size(); i++)
			syncProject(newP, (com.zavitz.mytasks.Project) projects
					.elementAt(i));
		for (int i = 0; i < old.size(); i++) {
			TodoItem2 item = (TodoItem2) old.elementAt(i);
			Task newItem = new Task();
			newItem.setName(item.getTitle());
			newItem.setDescription(item.getDescription());
			newItem.setStatus(item.getChecked() ? Task.COMPLETED
					: Task.NOT_STARTED);
			newItem.setDateDue(item.getRawDate());
			newItem.setReminder(item.getRawDate() - item.getReminder());
			newItem.setColor(item.getColorHex() == 0x00CCCCCC ? 0x00FFFFFF : item.getColorHex());
			newItem.setUID(item.getUID());
			Vector notes = item.getNoteVector();
			for (int j = 0; j < notes.size(); j++) {
				Note note = (Note) notes.elementAt(j);
				String s = note.getTrueNote();
				if (s.startsWith("[VOICE]"))
					s = s.substring("[VOICE]".length()) + "VN:";
				newItem.addNote(s);
			}
			newP.addTask(newItem);
		}
		parent.addProject(newP);
	}

	public static long trialValid() {
		persistentTrial = PersistentStore.getPersistentObject(TRIAL);
		Object contents = persistentTrial.getContents();
		if (contents instanceof Long) {
			long l = ((Long) contents).longValue();
			if (l < System.currentTimeMillis())
				return -1;
			else
				return System.currentTimeMillis() - l;
		} else {
			long validUntil = CalUtils.daysToMs(5);
			return System.currentTimeMillis() + validUntil;
		}
	}

	public static void init() {
		MTScreen.mainProjects = new Project[0];
		if (!importOld()) {
			Object object = persistentStorage.getContents();
			if (object instanceof String) {
				try {
					String s = (String) object;
					TaskUtils.importXML(s);
				} catch (Exception e) {
				}
			} else {
				MTScreen.mainProjects = new Project[0];
				save();
			}
		}
	}
	
	public static void reset() {
		persistentStorage.setContents(null);
		persistentStorage.commit();
	}

	public static void save() {
		String s = TaskUtils.getXML();
		persistentStorage.setContents(s);
		persistentStorage.commit();
	}

}
