// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ProjectScreen.java

package com.zavitz.mytasks;

import com.zavitz.mytasks.elements.Project;
import com.zavitz.mytasks.elements.Task;
import com.zavitz.mytasks.fields.HeaderLabel;
import com.zavitz.mytasks.fields.ProjectField;
import com.zavitz.mytasks.fields.ProjectManager;
import com.zavitz.mytasks.fields.TaskField;
import com.zavitz.mytasks.fields.TaskManager;
import com.zavitz.mytasks.fields.TitleField;
import com.zavitz.mytasks.functions.PersistentUtils;
import com.zavitz.mytasks.functions.TaskUtils;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.ui.container.*;
import net.rim.device.api.util.Arrays;

public class ProjectScreen extends MainScreen implements
		CreateProjectScreen.CreateProjectCallback,
		CreateTaskScreen.CreateTaskCallback,
		EditProjectScreen.EditProjectCallback, EditTaskScreen.EditTaskCallback {
	class MovePopup extends PopupScreen implements FieldChangeListener {

		public void sublayout(int width, int height) {
			super.sublayout(width, height);
			setPosition(Display.getWidth() - getWidth(), Display.getHeight()
					- getHeight());
		}

		public void fieldChanged(Field field, int context) {
			if (field == t_up)
				moveTask(task, -1);
			else if (field == t_set)
				close();
			else if (field == t_down)
				moveTask(task, 1);
			else if (field == p_up)
				moveProject(project, -1);
			else if (field == p_set)
				close();
			else if (field == p_down)
				moveProject(project, 1);
		}

		public boolean keyChar(char c, int status, int time) {
			if (c == '\033') {
				close();
				return true;
			}
			switch (c) {
			case 117: // 'u'
				fieldChanged(task != null ? t_up : p_up, 0);
				return true;

			case 115: // 's'
				fieldChanged(task != null ? t_set : p_set, 0);
				return true;

			case 100: // 'd'
				fieldChanged(task != null ? t_down : p_down, 0);
				return true;
			}
			return super.keyChar(c, status, time);
		}

		ButtonField t_up;
		ButtonField t_set;
		ButtonField t_down;
		ButtonField p_up;
		ButtonField p_set;
		ButtonField p_down;
		Task task;
		Project project;

		public MovePopup(Task task) {
			super(new VerticalFieldManager());
			this.task = task;
			t_up = new ButtonField("U\u0332p", 0x300010004L);
			t_up.setChangeListener(this);
			t_set = new ButtonField("S\u0332et", 0x300010004L);
			t_set.setChangeListener(this);
			t_down = new ButtonField("D\u0332own", 0x300010004L);
			t_down.setChangeListener(this);
			add(t_up);
			add(t_set);
			add(t_down);
		}

		public MovePopup(Project project) {
			super(new VerticalFieldManager());
			this.project = project;
			p_up = new ButtonField("U\u0332p", 0x300010004L);
			p_up.setChangeListener(this);
			p_set = new ButtonField("S\u0332et", 0x300010004L);
			p_set.setChangeListener(this);
			p_down = new ButtonField("D\u0332own", 0x300010004L);
			p_down.setChangeListener(this);
			add(p_up);
			add(p_set);
			add(p_down);
		}
	}

	public ProjectScreen(Project project) {
		super(Manager.NO_VERTICAL_SCROLL | Manager.NO_VERTICAL_SCROLLBAR);
		m_openProject = new MenuItem("O\u0332pen Project", 110, 1) {
			public void run() {
				openProject();
			}
		};
		m_openTask = new MenuItem("O\u0332pen Task", 110, 1) {
			public void run() {
				openTask();
			}
		};
		m_editTask = new MenuItem("Edit Task", 110, 1) {
			public void run() {
				editTask();
			}
		};
		m_markTaskCompleted = new MenuItem("Mark C\u0332ompleted", 110, 1) {
			public void run() {
				changeStatus(4);
			}
		};
		m_markTaskInProgress = new MenuItem("Mark In Pr\u0332ogress", 110, 1) {
			public void run() {
				changeStatus(1);
			}
		};
		m_markTaskWaiting = new MenuItem("Mark W\u0332aiting", 110, 1) {
			public void run() {
				changeStatus(2);
			}
		};
		m_markTaskDeferred = new MenuItem("Mark D\u0332eferred", 110, 1) {
			public void run() {
				changeStatus(3);
			}
		};
		m_markTaskNotStarted = new MenuItem("Mark N\u0332ot Started", 110, 1) {
			public void run() {
				changeStatus(0);
			}
		};
		m_moveTask = new MenuItem("M\u0332ove Task", 110, 1) {
			public void run() {
				moveTask();
			}
		};
		m_deleteTask = new MenuItem("Delete Task", 110, 1) {
			public void run() {
				deleteTask();
			}
		};
		m_deleteProject = new MenuItem("Delete Project", 110, 1) {
			public void run() {
				deleteProject();
			}
		};
		m_moveProject = new MenuItem("M\u0332ove Project", 110, 1) {
			public void run() {
				moveProject();
			}
		};
		m_createProject = new MenuItem("Create P\u0332roject", 110, 1) {
			public void run() {
				createProject();
			}
		};
		m_editProject = new MenuItem("E\u0332dit Project", 110, 1) {
			public void run() {
				editProject();
			}
		};
		m_createTask = new MenuItem("Create T\u0332ask", 110, 1) {
			public void run() {
				createTask();
			}
		};
		internalManager = new VerticalFieldManager(Manager.NO_VERTICAL_SCROLL
				| Manager.NO_VERTICAL_SCROLLBAR);
		super.add(internalManager);
		internalManager.add(new TitleField(TaskUtils.getPath(project)));
		manager = new VerticalFieldManager(Manager.VERTICAL_SCROLL
				| Manager.VERTICAL_SCROLLBAR);
		internalManager.add(manager);
		this.project = project;
		HeaderLabel projectsLabel = new HeaderLabel("Projects");
		projectsLabel.setFont(getFont().getHeight() - 4);
		projects = new ProjectManager(this);
		HeaderLabel tasksLabel = new HeaderLabel("Tasks");
		tasksLabel.setFont(getFont().getHeight() - 4);
		tasks = new TaskManager(this);
		loadProjects();
		loadTasks();
		add(projectsLabel);
		add(projects);
		add(tasksLabel);
		add(tasks);
	}

	public void add(Field f) {
		manager.add(f);
	}

	public boolean keyChar(char c, int status, int time) {
		boolean retVal = true;
		switch (c) {
		case 8: // '\b'
			if (getLeafFieldWithFocus() instanceof TaskField) {
				deleteTask();
				break;
			}
			if (getLeafFieldWithFocus() instanceof ProjectField)
				deleteProject();
			break;

		case 'e': // 'e'
			m_editProject.run();
			break;

		case 'o': // 'o'
			if (getLeafFieldWithFocus() instanceof TaskField) {
				openTask();
				break;
			}
			if (getLeafFieldWithFocus() instanceof ProjectField)
				openProject();
			break;

		case 'n': // 'n'
			if (getLeafFieldWithFocus() instanceof TaskField)
				m_markTaskNotStarted.run();
			break;

		case 'd': // 'd'
			if (getLeafFieldWithFocus() instanceof TaskField)
				m_markTaskDeferred.run();
			break;

		case 'w': // 'w'
			if (getLeafFieldWithFocus() instanceof TaskField)
				m_markTaskWaiting.run();
			break;

		case 'r': // 'r'
			if (getLeafFieldWithFocus() instanceof TaskField)
				m_markTaskInProgress.run();
			break;

		case 'c': // 'c'
			if (getLeafFieldWithFocus() instanceof TaskField)
				m_markTaskCompleted.run();
			break;

		case 'm': // 'm'
			if (getLeafFieldWithFocus() instanceof TaskField) {
				if (project.getSortType() == Project.SORT_MANUAL)
					m_moveTask.run();
			} else {
				m_moveProject.run();
			}
			break;

		case 't': // 't'
			m_createTask.run();
			break;

		case 'p': // 'p'
			m_createProject.run();
			break;

		default:
			retVal = super.keyChar(c, status, time);
			break;
		}
		return retVal;
	}

	private void loadProjects() {
		for (int i = 0; i < project.getProjects().length; i++)
			if (project.getProject(i) != null)
				projects.add(new ProjectField(project.getProject(i)));

	}

	private void loadTasks() {
		TaskField fields[] = new TaskField[project.getTasks().length];
		for (int i = 0; i < fields.length; i++)
			if (project.getTask(i) != null)
				fields[i] = new TaskField(project.getTask(i));

		tasks.add(fields);
	}

	private ProjectScreen instance() {
		return this;
	}

	public Project getProject() {
		return project;
	}

	private void deleteProject() {
		if (Dialog.ask(Dialog.D_DELETE) == Dialog.DELETE) {
			ProjectField field = (ProjectField) projects.getFieldWithFocus();
			projects.delete(field);
			Arrays.remove(project.getProjects(), field.getProject());
			com.zavitz.mytasks.functions.PersistentUtils.save();
		}
	}

	private void deleteTask() {
		if (Dialog.ask(Dialog.D_DELETE) == Dialog.DELETE) {
			TaskField field = (TaskField) tasks.getFieldWithFocus();
			tasks.delete(field);
			Arrays.remove(project.getTasks(), field.getTask());
			com.zavitz.mytasks.functions.PersistentUtils.save();
		}
	}

	private void openProject() {
		UiApplication.getUiApplication().pushScreen(
				new ProjectScreen(((ProjectField) projects.getFieldWithFocus())
						.getProject()));
	}

	private void editTask() {
		UiApplication.getUiApplication().pushScreen(
				new EditTaskScreen(instance(),
						((TaskField) getLeafFieldWithFocus()).getTask()));
	}

	private void changeStatus(int status) {
		Task task = ((TaskField) tasks.getFieldWithFocus()).getTask();
		task.setStatus(status);
		com.zavitz.mytasks.functions.PersistentUtils.save();
		((TaskField) tasks.getFieldWithFocus()).invalidate();
	}

	private void openTask() {
		UiApplication.getUiApplication().pushScreen(
				new TaskScreen(((TaskField) tasks.getFieldWithFocus())
						.getTask()));
	}

	private void moveTask() {
		UiApplication.getUiApplication()
				.pushScreen(
						new MovePopup(((TaskField) tasks.getFieldWithFocus())
								.getTask()));
	}

	private void moveProject() {
		UiApplication.getUiApplication().pushScreen(
				new MovePopup(((ProjectField) projects.getFieldWithFocus())
						.getProject()));
	}

	public void makeMenu(Menu menu, int instance) {
		menu.add(m_createProject);
		menu.add(m_createTask);
		if (projects.getFieldWithFocus() != null
				&& (projects.getFieldWithFocus() instanceof ProjectField)) {
			menu.addSeparator();
			menu.add(m_openProject);
			if (projects.getFieldCount() > 1)
				menu.add(m_moveProject);
			menu.add(m_deleteProject);
			menu.setDefault(m_openProject);
		}
		if (tasks.getFieldWithFocus() != null
				&& (tasks.getFieldWithFocus() instanceof TaskField)) {
			menu.addSeparator();
			menu.add(m_openTask);
			if (project.getSortType() == Project.SORT_MANUAL
					&& tasks.getFieldCount() > 1)
				menu.add(m_moveTask);
			menu.add(m_editTask);
			menu.add(m_deleteTask);
			menu.setDefault(m_openTask);
			TaskField field = (TaskField) tasks.getFieldWithFocus();
			switch (field.getTask().getStatus()) {
			case Task.NOT_STARTED: // '\0'
				menu.addSeparator();
				menu.add(m_markTaskInProgress);
				menu.add(m_markTaskDeferred);
				menu.add(m_markTaskWaiting);
				menu.add(m_markTaskCompleted);
				break;

			case Task.IN_PROGRESS: // '\001'
				menu.addSeparator();
				menu.add(m_markTaskNotStarted);
				menu.add(m_markTaskDeferred);
				menu.add(m_markTaskWaiting);
				menu.add(m_markTaskCompleted);
				break;

			case Task.WAITING: // '\002'
				menu.addSeparator();
				menu.add(m_markTaskNotStarted);
				menu.add(m_markTaskInProgress);
				menu.add(m_markTaskDeferred);
				menu.add(m_markTaskCompleted);
				break;

			case Task.DEFERRED: // '\003'
				menu.addSeparator();
				menu.add(m_markTaskNotStarted);
				menu.add(m_markTaskInProgress);
				menu.add(m_markTaskWaiting);
				menu.add(m_markTaskCompleted);
				break;
			}
		}
		menu.addSeparator();
		menu.add(m_editProject);
		menu.addSeparator();
		menu.add(new MenuItem("About", 110, 1) {
			public void run() {
				UiApplication.getUiApplication().pushScreen(new AboutScreen());
			}
		});
		super.makeMenu(menu, instance);
	}

	public void editProject() {
		UiApplication.getUiApplication().pushScreen(
				new EditProjectScreen(project, instance()));
	}

	public void createProject() {
		UiApplication.getUiApplication().pushScreen(
				new CreateProjectScreen(instance()));
	}

	public void createTask() {
		UiApplication.getUiApplication().pushScreen(
				new CreateTaskScreen(instance()));
	}

	public void projectCreated(Project project) {
		this.project.addProject(project);
		com.zavitz.mytasks.functions.PersistentUtils.save();
		ProjectField projectField = new ProjectField(project);
		projects.add(projectField);
		projectField.setFocus();
	}

	public void taskCreated(Task task) {
		project.addTask(task);
		task.updateEvent();
		System.out.println("Created task: " + task.getUID());
		com.zavitz.mytasks.functions.PersistentUtils.save();
		TaskField taskField = new TaskField(task);
		tasks.add(taskField);
		taskField.setFocus();
	}

	public void projectEdited(Project project) {
		tasks.sort();
		com.zavitz.mytasks.functions.PersistentUtils.save();
	}

	public void moveTask(Task task, int change) {
		int index = Arrays.getIndex(project.getTasks(), task);
		if (index + change >= 0 && index + change < tasks.getFieldCount()) {
			Arrays.remove(project.getTasks(), task);
			Arrays.insertAt(project.getTasks(), task, index + change);
			PersistentUtils.save();
			tasks.deleteAll();
			loadTasks();
			tasks.setFocus(task);
		}
	}

	public void moveProject(Project project, int change) {
		int index = Arrays.getIndex(this.project.getProjects(), project);
		if (index + change >= 0 && index + change < projects.getFieldCount()) {
			Arrays.remove(this.project.getProjects(), project);
			Arrays
					.insertAt(this.project.getProjects(), project, index
							+ change);
			com.zavitz.mytasks.functions.PersistentUtils.save();
			projects.deleteAll();
			loadProjects();
			projects.setFocus(project);
		}
	}

	public void taskEdited(Task task) {
		task.updateEvent();
		com.zavitz.mytasks.functions.PersistentUtils.save();
	}

	Project project;
	ProjectManager projects;
	TaskManager tasks;
	VerticalFieldManager internalManager;
	VerticalFieldManager manager;
	private MenuItem m_openProject;
	private MenuItem m_openTask;
	private MenuItem m_editTask;
	private MenuItem m_markTaskCompleted;
	private MenuItem m_markTaskInProgress;
	private MenuItem m_markTaskWaiting;
	private MenuItem m_markTaskDeferred;
	private MenuItem m_markTaskNotStarted;
	private MenuItem m_moveTask;
	private MenuItem m_deleteTask;
	private MenuItem m_deleteProject;
	private MenuItem m_moveProject;
	private MenuItem m_createProject;
	private MenuItem m_editProject;
	private MenuItem m_createTask;

}
