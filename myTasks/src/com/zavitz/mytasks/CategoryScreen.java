package com.zavitz.mytasks;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;

import com.zavitz.mytasks.EditTaskScreen.EditTaskCallback;
import com.zavitz.mytasks.elements.Task;
import com.zavitz.mytasks.fields.*;
import com.zavitz.mytasks.functions.CalUtils;
import com.zavitz.mytasks.functions.PersistentUtils;
import com.zavitz.mytasks.functions.TaskUtils;

public class CategoryScreen extends MainScreen implements EditTaskCallback {

	int style;
	TaskManager tasks;
	VerticalFieldManager internalManager, manager;
	
	public CategoryScreen(int style) {
		super(Manager.NO_VERTICAL_SCROLL | Manager.NO_VERTICAL_SCROLLBAR);

		internalManager = new VerticalFieldManager(Manager.NO_VERTICAL_SCROLL
				| Manager.NO_VERTICAL_SCROLLBAR);
		super.add(internalManager);
		internalManager.add(new TitleField(CategoryField.DEFAULT_STYLES[style]));

		manager = new VerticalFieldManager(Manager.VERTICAL_SCROLL
				| Manager.VERTICAL_SCROLLBAR);
		internalManager.add(manager);
		
		this.style = style;
		
		HeaderLabel label = new HeaderLabel(CategoryField.DEFAULT_STYLES[style]);
		label.setFont(getFont().getHeight() - 4);
		
		tasks = new TaskManager(this);
		tasks.add(style < CategoryField.COMPLETED ? TaskUtils.getTasks(CalUtils.getFrom(style), CalUtils.getTo(style)) : TaskUtils.getCompletedTasks());
		
		add(label);
		add(tasks);
	}
	
	public void add(Field f) {
		manager.add(f);
	}
	
	public void makeMenu(Menu menu, int instance) {
		if (tasks.getFieldWithFocus() != null
				&& tasks.getFieldWithFocus() instanceof TaskField) {
			menu.addSeparator();
			menu.add(m_openTask);
			menu.add(m_editTask);
			menu.setDefault(m_openTask);

			TaskField field = (TaskField) tasks.getFieldWithFocus();
			switch (field.getTask().getStatus()) {
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
		}
		menu.addSeparator();
		menu.add(new MenuItem("About", 110, 1) {
			public void run() {
				UiApplication.getUiApplication().pushScreen(new AboutScreen());
			}
		});
		super.makeMenu(menu, instance);
	}
	
	private void openTask() {
		UiApplication.getUiApplication().pushScreen(new TaskScreen(((TaskField) tasks.getFieldWithFocus()).getTask()));
	}

	public boolean keyChar(char c, int status, int time) {
		boolean retVal = true;
		switch (c) {
		case 'o':
			// open selection
			if (getLeafFieldWithFocus() instanceof TaskField)
				openTask();
		case 'n':
			// mark not started
			if (getLeafFieldWithFocus() instanceof TaskField)
				m_markTaskNotStarted.run();
			break;
		case 'd':
			// mark deferred
			if (getLeafFieldWithFocus() instanceof TaskField)
				m_markTaskDeferred.run();
			break;
		case 'w':
			// mark waiting
			if (getLeafFieldWithFocus() instanceof TaskField)
				m_markTaskWaiting.run();
			break;
		case 'r':
			// mark in progress
			if (getLeafFieldWithFocus() instanceof TaskField)
				m_markTaskInProgress.run();
			break;
		case 'c':
			// mark complete
			if (getLeafFieldWithFocus() instanceof TaskField)
				m_markTaskCompleted.run();
			break;
		default:
			retVal = super.keyChar(c, status, time);
		}
		return retVal;
	}

	private MenuItem m_openTask = new MenuItem("Open Task", 110, 1) {
		public void run() {
			openTask();
		}
	};

	private MenuItem m_editTask = new MenuItem("Edit Task", 110, 1) {
		public void run() {
			editTask();
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
		Task task = ((TaskField) tasks.getFieldWithFocus()).getTask();
		task.setStatus(status);
		com.zavitz.mytasks.functions.PersistentUtils.save();

		((TaskField) tasks.getFieldWithFocus()).invalidate();
	}
	
	private CategoryScreen instance() {
		return this;
	}

	private void editTask() {
		UiApplication.getUiApplication().pushScreen(
				new EditTaskScreen(instance(),
						((TaskField) getLeafFieldWithFocus()).getTask()));
	}

	public void taskEdited(Task task) {
		task.updateEvent();
	}
	
}

