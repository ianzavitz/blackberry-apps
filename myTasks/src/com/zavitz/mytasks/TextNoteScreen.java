package com.zavitz.mytasks;

import com.zavitz.mytasks.elements.Project;
import com.zavitz.mytasks.functions.PersistentUtils;

import net.rim.device.api.system.Characters;
import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.ui.container.*;

public class TextNoteScreen extends PopupScreen {
	
	TaskScreen callback;
	AutoTextEditField input;
	ObjectChoiceField sort;

	public TextNoteScreen(TaskScreen _callback) {
		super(new VerticalFieldManager());
		callback = _callback;
		
		LabelField label = new LabelField("Enter Note:");
		label.setFont(getFont().derive(Font.BOLD));

		input = new AutoTextEditField();
		input.setFont(getFont().derive(Font.PLAIN));
		
		add(label);
		add(input);

		ButtonField ok = new ButtonField("Save");
		ok.setChangeListener(new FieldChangeListener() {
			public void fieldChanged(Field f, int instance) {
				callback.getTask().addNote(input.getText());
				callback.notes.add(input.getText());
				PersistentUtils.save();
				setDirty(false);
				close();
			}
		});
		ButtonField close = new ButtonField("Close");
		close.setChangeListener(new FieldChangeListener() {
			public void fieldChanged(Field f, int instance) {
				setDirty(false);
				close();
			}
		});

		HorizontalFieldManager buttons = new HorizontalFieldManager();
		buttons.add(ok);
		buttons.add(close);
		add(buttons);
	}

	public boolean keyChar(char key, int status, int time) {
		boolean retval = false;
		switch (key) {
		case Characters.ENTER:
			callback.getTask().addNote(input.getText());
			callback.notes.add(input.getText());
			com.zavitz.mytasks.functions.PersistentUtils.save();
			setDirty(false);
			close();
			retval = true;
			break;
		case Characters.ESCAPE:
			close();
			break;
		default:
			retval = super.keyChar(key, status, time);
		}
		return retval;
	}
	
	public interface CreateProjectCallback {
		public void projectCreated(Project project);
	}

}
