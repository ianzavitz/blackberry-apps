package com.zavitz.mytasks;

import net.rim.device.api.i18n.DateFormat;
import net.rim.device.api.i18n.SimpleDateFormat;
import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.ui.container.*;

import com.zavitz.mytasks.elements.*;
import com.zavitz.mytasks.fields.*;
import com.zavitz.mytasks.functions.*;

public class EditTaskScreen extends MainScreen {

	EditTaskCallback callback;
	VerticalFieldManager internalManager, manager;
	Task task;
	
	BitmapInput name, description, priority, status, color, percent_complete, tags, 
				dueDate, reminder,
				save;

	public EditTaskScreen(EditTaskCallback _callback, Task task) {
		super(Manager.NO_VERTICAL_SCROLL | Manager.NO_VERTICAL_SCROLLBAR);
		this.task = task;

		internalManager = new VerticalFieldManager(Manager.NO_VERTICAL_SCROLL
				| Manager.NO_VERTICAL_SCROLLBAR);
		super.add(internalManager);
		internalManager.add(new TitleField("Editing a Task"));

		manager = new VerticalFieldManager(Manager.VERTICAL_SCROLL
				| Manager.VERTICAL_SCROLLBAR);
		internalManager.add(manager);
		
		callback = _callback;
		
		setDirty(true);

		Font header = getFont().derive(Font.BOLD, getFont().getHeight() - 4);

		/* NAME & DESCRIPTION SECTION */
		HeaderLabel l_nd = new HeaderLabel("General Information");
		l_nd.setFont(header);
		name = new BitmapInput("Task Name", "completed.png");
		name.setTextQuiet(task.getName());
		description = new BitmapInput("Description", "description.png");
		description.setTextQuiet(task.getDescription().equals("") ? "No description" : task.getDescription());
		priority = new BitmapInput("Priority", "Normal", "high_priority.png");
		priority.setSelected(task.getPriority());
		percent_complete = new BitmapInput("Percent Complete", task.getPercentComplete() + "% Complete","percent_complete.png");
		status = new BitmapInput("Status", "Not Started", "progress.png") {
			public void selectionChange(int status) {
				if(status == Task.NOT_STARTED)
					percent_complete.setTextQuiet("0% Complete");
				else if(status == Task.COMPLETED)
					percent_complete.setTextQuiet("100% Complete");					
			}
		};
		status.setSelected(task.getStatus());
		color = new BitmapInput("Color", "White", "color.png");
		color.setTextQuiet(getColorName(task.getColor()));
		tags = new BitmapInput("Tags", "tags.png");
		tags.setTextQuiet(TaskUtils.getTags(task));
		add(l_nd);
		add(name);
		add(description);
		add(priority);
		add(status);
		add(percent_complete);
		add(color);
		add(tags);
		
		/* DUE DATE & REMINDER SECTION */
		HeaderLabel l_dr = new HeaderLabel("Due Date");
		l_dr.setFont(header);
		dueDate = new BitmapInput("Due Date", "soon.png");
		dueDate.date = task.getDateDue();
		dueDate.setTextQuiet(dueDate.date == 0 ? "No due date set." : new SimpleDateFormat(DateFormat.DATETIME_DEFAULT).formatLocal(dueDate.date));
		reminder = new BitmapInput("Reminder", "reminder.png");
		reminder.date = task.getDateDue() - (1000 * task.getReminder());
		reminder.setTextQuiet(reminder.date == 0 ? "No reminder set." : new SimpleDateFormat(DateFormat.DATETIME_DEFAULT).formatLocal(reminder.date));
		add(l_dr);
		add(dueDate);
		add(reminder);
		
		HeaderLabel l_s = new HeaderLabel(" ");
		l_s.setFont(header);
		save = new BitmapInput("Save", "save.png") {
			public void open() {
				save();				
				close();
			}
		};
		BitmapInput close = new BitmapInput("Close", "cancel.png") {
			public void open() {
				close();
			}
		};
		add(l_s);
		add(save);
		add(close);
	}
	
	public void makeMenu(Menu menu, int instance) {
		menu.add(m_save);
		menu.addSeparator();
		menu.add(new MenuItem("About", 110, 1) {
			public void run() {
				UiApplication.getUiApplication().pushScreen(new AboutScreen());
			}
		});
		super.makeMenu(menu, instance);
	}
	
	private MenuItem m_save = new MenuItem("Save", 110, 1) {
		public void run() {
			save();
			close();
		}
	};
	
	public void save() {
		task.setName(name.getText());
		task.setDescription(description.getText());
		if (dueDate.getDate() <= 0 && reminder.getDate() > 0) {
			Status.show("Tasks need a due date for a reminder to be set.");
			task.setDateDue(0);
			task.setReminder(0);
		} else {
			if(dueDate.getDate() < reminder.getDate()) {
				Status.show("Reminder needs to be before the due date");
				task.setDateDue(dueDate.getDate());
				task.setReminder(0);
			} else {
				task.setDateDue(dueDate.getDate());
				if(reminder.getDate() > 0)
					task.setReminder((dueDate.getDate() - reminder.getDate()) / 1000);
				else
					task.setReminder(0);
			}
		}
		TaskUtils.parseTags(task, tags.getText());
		task.setPriority(priority.getSelected());
		task.setStatus(status.getSelected());
		task.setColor(getColorHex(color.getText()));
		String p = percent_complete.getText();
		p = p.substring(0, p.indexOf("%"));
		task.setPercentComplete(Integer.parseInt(p));
		callback.taskEdited(task);
	}
	
	public int getColorHex(String c) {
		if(c.indexOf("White") >= 0)
			return 0x00FFFFFF;
		else if(c.indexOf("Red") >= 0)
			return 0x00FF0000;
		else if(c.indexOf("Green") >= 0)
			return 0x0002d111;
		else if(c.indexOf("Orange") >= 0)
			return 0x00ff9600;
		else if(c.indexOf("Yellow") >= 0)
			return 0x00f5e700;
		else if(c.indexOf("Teal") >= 0)
			return 0x0000d4e7;
		else if(c.indexOf("Pink") >= 0)
			return 0x00e700df;
		else return 0x00FFFFFF;
	}
	
	public String getColorName(int hex) {
		if(hex == 0x00FFFFFF)
			return "White";
		else if(hex == 0x00FF0000)
			return "Red";
		else if(hex == 0x0002d111)
			return "Green";
		else if(hex == 0x00ff9600)
			return "Orange";
		else if(hex == 0x00f5e700)
			return "Yellow";
		else if(hex == 0x0000d4e7)
			return "Teal";
		else if(0x00e700df == hex)
			return "Pink";
		else return "White";
	}
	
	public void add(Field f) {
		manager.add(f);
	}

	public interface EditTaskCallback {
		public void taskEdited(Task task);
	}
	
}