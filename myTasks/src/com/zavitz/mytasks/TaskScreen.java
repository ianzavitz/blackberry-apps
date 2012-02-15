package com.zavitz.mytasks;

import com.zavitz.mytasks.EditTaskScreen.EditTaskCallback;
import com.zavitz.mytasks.elements.*;
import com.zavitz.mytasks.fields.*;
import com.zavitz.mytasks.functions.*;

import net.rim.device.api.i18n.DateFormat;
import net.rim.device.api.i18n.SimpleDateFormat;
import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.ui.container.*;

public class TaskScreen extends MainScreen implements EditTaskCallback {

	Task task;
	VerticalFieldManager internalManager, manager;
	BitmapInput status, percent_complete;
	public NoteManager notes;

	public TaskScreen(Task task) {
		super(Manager.NO_VERTICAL_SCROLL | Manager.NO_VERTICAL_SCROLLBAR);

		internalManager = new VerticalFieldManager(Manager.NO_VERTICAL_SCROLL
				| Manager.NO_VERTICAL_SCROLLBAR);
		super.add(internalManager);
		internalManager.add(new TitleField(TaskUtils.getPath(task)));

		manager = new VerticalFieldManager(Manager.VERTICAL_SCROLL
				| Manager.VERTICAL_SCROLLBAR);
		internalManager.add(manager);

		this.task = task;

		Font bold = getFont().derive(Font.BOLD);

		HeaderLabel name = new HeaderLabel(task.getName());
		name.setFont(bold);
		RichTextField description = new RichTextField(task.getDescription()) {
			public void paint(Graphics graphics) {
				graphics.setColor(0x00777777);
				super.paint(graphics);
			}
		};

		add(name);

		if (task.getPriority() != Task.NORMAL_PRIORITY) {
			BitmapInput priority = new BitmapInput("Priority", task
					.getPriority() == Task.LOW_PRIORITY ? "low_priority.png"
					: "high_priority");
			priority.setEditable(false);
			priority.setSelected(task.getPriority());
			add(priority);
		}

		String statusRes = "";
		switch (task.getStatus()) {
		case Task.NOT_STARTED:
			statusRes = ("not_started.png");
			break;
		case Task.IN_PROGRESS:
			statusRes = ("progress.png");
			break;
		case Task.WAITING:
			statusRes = ("waiting.png");
			break;
		case Task.DEFERRED:
			statusRes = ("deferred.png");
			break;
		case Task.COMPLETED:
			statusRes = ("completed.png");
			break;
		}
		status = new BitmapInput("Status", statusRes);
		status.setEditable(false);
		status.setSelected(task.getStatus());
		add(status);

		percent_complete = new BitmapInput("Percent Complete", task
				.getPercentComplete()
				+ "% Complete", "percent_complete.png");
		percent_complete.setEditable(false);
		add(percent_complete);

		BitmapInput tags = new BitmapInput("Tags", "tags.png");
		tags.setTextQuiet(TaskUtils.getTags(task));
		tags.setEditable(false);
		add(tags);

		if (task.getDescription().length() > 0)
			add(description);

		Font header = getFont().derive(Font.BOLD, getFont().getHeight() - 4);
		HeaderLabel l_dr = new HeaderLabel("Due Date");
		l_dr.setFont(header);
		BitmapInput dueDate = new BitmapInput("Due Date", "soon.png");
		if (task.getDateDue() > 0) {
			dueDate.setTextQuiet("Due date: "
					+ new SimpleDateFormat(DateFormat.DATETIME_DEFAULT)
							.formatLocal(task.getDateDue()));
			System.out.println("REMINDER: " + task.getDateDue());
		} else
			dueDate.setTextQuiet("No due date set.");
		dueDate.setEditable(false);
		BitmapInput reminder = new BitmapInput("Reminder", "reminder.png");
		if (task.getReminder() > 0) {
			reminder.setTextQuiet("Reminder: "
					+ new SimpleDateFormat(DateFormat.DATETIME_DEFAULT)
							.formatLocal(task.getDateDue() - (task.getReminder() * 1000)));
			System.out.println("REMINDER: " + task.getReminder());
		} else
			reminder.setTextQuiet("No reminder set.");
		reminder.setEditable(false);
		add(l_dr);
		add(dueDate);
		add(reminder);
		if (task.getDateDue() > 0) {
			BitmapInput viewInCalendar = new BitmapInput("View in Calendar",
					"today.png");
			viewInCalendar.setTask(task);
			add(viewInCalendar);
		}

		HeaderLabel l_notes = new HeaderLabel("Notes");
		l_notes.setFont(header);
		notes = new NoteManager(task);
		add(l_notes);
		add(notes);
	}

	public Task getTask() {
		return task;
	}

	public void add(Field f) {
		manager.add(f);
	}

	public TaskScreen instance() {
		return this;
	}

	public void makeMenu(Menu menu, int instance) {
		menu.add(m_editTask);
		menu.addSeparator();
		switch (task.getStatus()) {
		case Task.NOT_STARTED:
			menu.addSeparator();
			menu.add(m_markTaskInProgress);
			menu.add(m_markTaskDeferred);
			menu.add(m_markTaskWaiting);
			menu.add(m_markTaskCompleted);
			break;
		case Task.IN_PROGRESS:
			menu.addSeparator();
			menu.add(m_markTaskNotStarted);
			menu.add(m_markTaskDeferred);
			menu.add(m_markTaskWaiting);
			menu.add(m_markTaskCompleted);
			break;
		case Task.COMPLETED:
			break;
		case Task.WAITING:
			menu.addSeparator();
			menu.add(m_markTaskNotStarted);
			menu.add(m_markTaskInProgress);
			menu.add(m_markTaskDeferred);
			menu.add(m_markTaskCompleted);
			break;
		case Task.DEFERRED:
			menu.addSeparator();
			menu.add(m_markTaskNotStarted);
			menu.add(m_markTaskInProgress);
			menu.add(m_markTaskDeferred);
			menu.add(m_markTaskCompleted);
		}
		menu.addSeparator();
		menu.add(m_createTextNote);
		menu.add(m_createVoiceNote);
		if (getLeafFieldWithFocus() instanceof NoteField) {
			menu.add(m_deleteNote);
		}
		menu.addSeparator();
		menu.add(new MenuItem("About", 110, 1) {
			public void run() {
				UiApplication.getUiApplication().pushScreen(new AboutScreen());
			}
		});
		super.makeMenu(menu, instance);
	}

	public boolean keyChar(char c, int status, int time) {
		boolean retVal = true;
		switch (c) {
		case 'n':
			// mark not started
			m_markTaskNotStarted.run();
			break;
		case 'd':
			// mark deferred
			m_markTaskDeferred.run();
			break;
		case 'w':
			// mark waiting
			m_markTaskWaiting.run();
			break;
		case 'r':
			// mark in progress
			m_markTaskInProgress.run();
			break;
		case 'c':
			// mark complete
			m_markTaskCompleted.run();
			break;
		default:
			retVal = super.keyChar(c, status, time);
		}
		return retVal;
	}

	private void editTask() {
		UiApplication.getUiApplication().pushScreen(
				new EditTaskScreen(instance(), task));
	}

	private MenuItem m_editTask = new MenuItem("Edit Task", 110, 1) {
		public void run() {
			editTask();
		}
	};

	private MenuItem m_createTextNote = new MenuItem("Create Text Note", 110, 1) {
		public void run() {
			UiApplication.getUiApplication().pushScreen(
					new TextNoteScreen(instance()));
		}
	};

	private MenuItem m_createVoiceNote = new MenuItem("Create Voice Note", 110,
			1) {
		public void run() {
			UiApplication.getUiApplication().pushScreen(
					new RecordNoteScreen(instance()));
		}
	};

	private MenuItem m_deleteNote = new MenuItem("Delete Note", 110, 1) {
		public void run() {
			NoteField noteField = (NoteField) getLeafFieldWithFocus();
			task.deleteNote(noteField.note);
			notes.delete(noteField);
			com.zavitz.mytasks.functions.PersistentUtils.save();
		}
	};

	private MenuItem m_markTaskCompleted = new MenuItem("Mark C\u0332ompleted",
			110, 1) {
		public void run() {
			changeStatus(Task.COMPLETED);
		}
	};

	private MenuItem m_markTaskInProgress = new MenuItem(
			"Mark In Pr\u0332ogress", 110, 1) {
		public void run() {
			changeStatus(Task.IN_PROGRESS);
		}
	};

	private MenuItem m_markTaskWaiting = new MenuItem("Mark W\u0332aiting",
			110, 1) {
		public void run() {
			changeStatus(Task.WAITING);
		}
	};

	private MenuItem m_markTaskDeferred = new MenuItem("Mark D\u0332eferred",
			110, 1) {
		public void run() {
			changeStatus(Task.DEFERRED);
		}
	};

	private MenuItem m_markTaskNotStarted = new MenuItem(
			"Mark N\u0332ot Started", 110, 1) {
		public void run() {
			changeStatus(Task.NOT_STARTED);
		}
	};

	private void changeStatus(int status) {
		task.setStatus(status);
		com.zavitz.mytasks.functions.PersistentUtils.save();

		this.status.setSelected(status);
		percent_complete.setTextQuiet(task.getPercentComplete() + "%");
	}

	public void taskEdited(Task task) {
		task.updateEvent();
		com.zavitz.mytasks.functions.PersistentUtils.save();

		UiApplication.getUiApplication().popScreen(this);
		UiApplication.getUiApplication().pushScreen(new TaskScreen(task));
	}

}