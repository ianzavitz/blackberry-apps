package com.zavitz.mytasks;

import com.zavitz.mytasks.CreateProjectScreen.CreateProjectCallback;
import com.zavitz.mytasks.elements.Project;
import com.zavitz.mytasks.fields.*;
import com.zavitz.mytasks.functions.CalUtils;
import com.zavitz.mytasks.functions.PersistentUtils;
import com.zavitz.mytasks.functions.TaskUtils;

import net.rim.device.api.system.Characters;
import net.rim.device.api.system.Clipboard;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.KeypadListener;
import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.ui.container.*;
import net.rim.device.api.util.Arrays;

public class MTScreen extends MainScreen implements CreateProjectCallback {

	public static Project[] mainProjects;
	ProjectManager projects;
	VerticalFieldManager dateCategories;
	VerticalFieldManager internalManager, manager;

	static {
		com.zavitz.mytasks.functions.PersistentUtils.init();
		CalUtils.loadEvents();
	}

	/*
	 * MTScreen is responsible for showing main projects, along with options
	 * such as all tasks, etc...
	 */
	public MTScreen() {
		super(Manager.NO_VERTICAL_SCROLL | Manager.NO_VERTICAL_SCROLLBAR);

		internalManager = new VerticalFieldManager(Manager.NO_VERTICAL_SCROLL
				| Manager.NO_VERTICAL_SCROLLBAR);
		super.add(internalManager);
		internalManager.add(new TitleField("DASHBOARD"));

		manager = new VerticalFieldManager(Manager.VERTICAL_SCROLL
				| Manager.VERTICAL_SCROLLBAR);
		internalManager.add(manager);

		HeaderLabel projectsLabel = new HeaderLabel("Projects");
		projectsLabel.setFont(getFont().getHeight() - 4);

		projects = new ProjectManager(this);
		loadProjects();

		add(projectsLabel);
		add(projects);

		HeaderLabel categoriesLabel = new HeaderLabel("Categories");
		categoriesLabel.setFont(getFont().getHeight() - 4);

		dateCategories = new VerticalFieldManager();
		for (int i = 0; i <= CategoryField.SEARCH; i++) {
			CategoryField field = new CategoryField(i);
			dateCategories.add(field);
		}
		
		add(categoriesLabel);
		add(dateCategories);
	}

	public void add(Field f) {
		manager.add(f);
	}

	public boolean keyChar(char c, int status, int time) {
		boolean retVal = true;
		if((status & KeypadListener.STATUS_ALT) == KeypadListener.STATUS_ALT && c == '3') {
			if(Dialog.ask(Dialog.D_YES_NO, "Are you sure you want to reset myTasks?") == Dialog.YES)
				com.zavitz.mytasks.functions.PersistentUtils.reset();
		}
		switch (c) {
		case Characters.BACKSPACE:
			// delete selection
			if (getLeafFieldWithFocus() instanceof ProjectField)
				deleteProject();
			break;
		case 'o':
			// open selection
			if (getLeafFieldWithFocus() instanceof ProjectField)
				openProject();
			break;
		case 'p':
			// mark project
			m_createProject.run();
			break;
		case 'm':
			// move project
			m_moveProject.run();
		default:
			retVal = super.keyChar(c, status, time);
		}
		return retVal;
	}

	private void loadProjects() {
		for (int i = 0; i < mainProjects.length; i++)
			projects.add(new ProjectField(mainProjects[i]));
	}

	private MTScreen instance() {
		return this;
	}

	private void deleteProject() {
		if (Dialog.ask(Dialog.D_DELETE) == Dialog.DELETE) {
			ProjectField field = (ProjectField) projects.getFieldWithFocus();
			projects.delete(field);
			Arrays.remove(mainProjects, field.getProject());
			com.zavitz.mytasks.functions.PersistentUtils.save();
		}
	}

	private void openProject() {
		UiApplication.getUiApplication().pushScreen(
				new ProjectScreen(((ProjectField) projects.getFieldWithFocus())
						.getProject()));
	}

	public void makeMenu(Menu menu, int instance) {
		menu.add(m_createProject);
		if (projects.getFieldWithFocus() != null) {
			menu.add(m_openProject);
			if(projects.getFieldCount() > 1)
				menu.add(m_moveProject);
			menu.add(m_deleteProject);
			menu.setDefault(m_openProject);
		}
		menu.addSeparator();
		menu.add(new MenuItem("About", 110, 1) {
			public void run() {
				UiApplication.getUiApplication().pushScreen(new AboutScreen());
			}
		});
		super.makeMenu(menu, instance);
	}

	public void createProject() {
		UiApplication.getUiApplication().pushScreen(
				new CreateProjectScreen(instance()));
	}

	private MenuItem m_createProject = new MenuItem("Create P\u0332roject",
			110, 1) {
		public void run() {
			createProject();
		}
	};

	private MenuItem m_openProject = new MenuItem("O\u0332pen Project", 110, 1) {
		public void run() {
			openProject();
		}
	};

	private MenuItem m_moveProject = new MenuItem("M\u0332ove Project", 110, 1) {
		public void run() {
			moveProject();
		}
	};

	private MenuItem m_deleteProject = new MenuItem("Delete Project", 110, 1) {
		public void run() {
			deleteProject();
		}
	};

	private void moveProject() {
		UiApplication.getUiApplication()
				.pushScreen(
					new MovePopup(((ProjectField) projects.getFieldWithFocus())
								.getProject()));
	}


	public void projectCreated(Project project) {
		Arrays.add(mainProjects, project);
		projects.add(new ProjectField(project));

		com.zavitz.mytasks.functions.PersistentUtils.save();
	}

	public void moveProject(Project project, int change) {
		int index = Arrays.getIndex(mainProjects, project);
		if (index + change >= 0 && index + change < projects.getFieldCount()) {
			Arrays.remove(mainProjects, project);
			Arrays.insertAt(mainProjects, project, index + change);
			com.zavitz.mytasks.functions.PersistentUtils.save();
			projects.deleteAll();
			loadProjects();
			projects.setFocus(project);
		}
	}

	class MovePopup extends PopupScreen implements FieldChangeListener {

		ButtonField up, set, down;
		Project project;

		public MovePopup(Project project) {
			super(new VerticalFieldManager());
			this.project = project;

			up = new ButtonField("U\u0332p", Field.FIELD_HCENTER
					| ButtonField.CONSUME_CLICK | DrawStyle.HCENTER);
			up.setChangeListener(this);
			set = new ButtonField("S\u0332et", Field.FIELD_HCENTER
					| ButtonField.CONSUME_CLICK | DrawStyle.HCENTER);
			set.setChangeListener(this);
			down = new ButtonField("D\u0332own", Field.FIELD_HCENTER
					| ButtonField.CONSUME_CLICK | DrawStyle.HCENTER);
			down.setChangeListener(this);

			add(up);
			add(set);
			add(down);
		}

		public void sublayout(int width, int height) {
			super.sublayout(width, height);
			setPosition(Display.getWidth() - getWidth(), Display.getHeight()
					- getHeight());
		}
		
		public void fieldChanged(Field field, int context) {
			if (field == up) {
				moveProject(project, -1);
			} else if (field == set) {
				close();
			} else if (field == down) {
				moveProject(project, 1);
			}
		}

		public boolean keyChar(char key, int status, int time) {
			if (key == Characters.ESCAPE) {
				close();
				return true;
			} else {
				switch(key) {
				case 'u':
					fieldChanged(up, 0);
					return true;
				case 's':
					fieldChanged(set, 0);
					return true;
				case 'd':
					fieldChanged(down, 0);
					return true;
				}
			}
			return super.keyChar(key, status, time);
		}

	}

}
