package com.zavitz.mytasks;

import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.ui.container.*;

import com.zavitz.mytasks.elements.Task;
import com.zavitz.mytasks.fields.*;
import com.zavitz.mytasks.functions.TaskUtils;

public class CreateTaskScreen extends MainScreen {

	CreateTaskCallback callback;
	VerticalFieldManager internalManager, manager;

	BitmapInput name, description, priority, status, color, percent_complete,
			tags, dueDate, reminder, save;

	public CreateTaskScreen(CreateTaskCallback _callback) {
		super(Manager.NO_VERTICAL_SCROLL | Manager.NO_VERTICAL_SCROLLBAR);

		internalManager = new VerticalFieldManager(Manager.NO_VERTICAL_SCROLL
				| Manager.NO_VERTICAL_SCROLLBAR);
		super.add(internalManager);
		internalManager.add(new TitleField("Create a Task"));

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
		description = new BitmapInput("Description", "description.png");
		priority = new BitmapInput("Priority", "Normal", "high_priority.png");
		priority.setSelected(Task.NORMAL_PRIORITY);
		percent_complete = new BitmapInput("Percent Complete", "0% Complete",
				"percent_complete.png");
		status = new BitmapInput("Status", "Not Started", "progress.png") {
			public void selectionChange(int status) {
				if (status == Task.NOT_STARTED)
					percent_complete.setTextQuiet("0% Complete");
				else if (status == Task.COMPLETED)
					percent_complete.setTextQuiet("100% Complete");
			}
		};
		status.setSelected(Task.NOT_STARTED);
		color = new BitmapInput("Color", "White", "color.png");
		tags = new BitmapInput("Tags", "tags.png");
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
		reminder = new BitmapInput("Reminder", "reminder.png");
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
		Task task = new Task();
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
		task.setColor(getColorHex(color.getText()));
		String p = percent_complete.getText();
		p = p.substring(0, p.indexOf("%"));
		task.setPercentComplete(Integer.parseInt(p));
		task.setStatus(status.getSelected());
		callback.taskCreated(task);
	}

	public int getColorHex(String c) {
		if (c.indexOf("White") >= 0)
			return 0x00FFFFFF;
		else if (c.indexOf("Red") >= 0)
			return 0x00FF0000;
		else if (c.indexOf("Green") >= 0)
			return 0x0002d111;
		else if (c.indexOf("Orange") >= 0)
			return 0x00ff9600;
		else if (c.indexOf("Yellow") >= 0)
			return 0x00f5e700;
		else if (c.indexOf("Teal") >= 0)
			return 0x0000d4e7;
		else if (c.indexOf("Pink") >= 0)
			return 0x00e700df;
		else
			return 0x00FFFFFF;
	}

	public void add(Field f) {
		manager.add(f);
	}

	public interface CreateTaskCallback {
		public void taskCreated(Task task);
	}

}