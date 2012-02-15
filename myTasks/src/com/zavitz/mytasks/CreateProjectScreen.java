package com.zavitz.mytasks;

import com.zavitz.mytasks.elements.Project;
import net.rim.device.api.system.Characters;
import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.ui.container.*;

public class CreateProjectScreen extends PopupScreen {
	
	CreateProjectCallback callback;
	AutoTextEditField input;
	ObjectChoiceField sort;

	public CreateProjectScreen(CreateProjectCallback _callback) {
		super(new VerticalFieldManager());
		callback = _callback;
		
		LabelField label = new LabelField("Enter Project Name:");
		label.setFont(getFont().derive(Font.BOLD));

		input = new AutoTextEditField();
		input.setFont(getFont().derive(Font.PLAIN));
		
		sort = new ObjectChoiceField("Sorting:", new String[] {"By Name", "By Date", "By Priority", "By Status", "Manually"}, 1);
		
		add(label);
		add(input);
		add(sort);

		ButtonField ok = new ButtonField("Ok");
		ok.setChangeListener(new FieldChangeListener() {
			public void fieldChanged(Field f, int instance) {
				Project project = new Project();
				project.setName(input.getText());
				project.setSortType(sort.getSelectedIndex());
				
				callback.projectCreated(project);
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
			Project project = new Project();
			project.setName(input.getText());
			project.setSortType(sort.getSelectedIndex());
			
			callback.projectCreated(project);
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
