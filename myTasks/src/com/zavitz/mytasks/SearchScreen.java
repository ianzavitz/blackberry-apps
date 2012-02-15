package com.zavitz.mytasks;

import net.rim.device.api.ui.*;
import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.*;
import net.rim.device.api.ui.container.VerticalFieldManager;
import com.zavitz.mytasks.elements.Task;
import com.zavitz.mytasks.fields.*;
import com.zavitz.mytasks.fields.SearchInput.SearchCallback;
import com.zavitz.mytasks.functions.TaskUtils;

public class SearchScreen extends MainScreen implements SearchCallback {

	TaskManager results;
	VerticalFieldManager internalManager, manager;
	SearchInput searchInput;

	public SearchScreen() {
		super(Manager.NO_VERTICAL_SCROLL | Manager.NO_VERTICAL_SCROLLBAR);

		internalManager = new VerticalFieldManager(Manager.NO_VERTICAL_SCROLL
				| Manager.NO_VERTICAL_SCROLLBAR);
		super.add(internalManager);
		internalManager.add(new TitleField("Search"));

		manager = new VerticalFieldManager(Manager.VERTICAL_SCROLL
				| Manager.VERTICAL_SCROLLBAR);
		internalManager.add(manager);

		HeaderLabel label = new HeaderLabel("Enter a Search Term");
		label.setFont(getFont().getHeight() - 4);
		searchInput = new SearchInput(this);
		searchInput.setFont(getFont().derive(Font.PLAIN,
				getFont().getHeight() - 3));

		results = new TaskManager(this);
		add(label);
		add(searchInput);
		add(results);
	}
	
	public boolean onClose() {
		setDirty(false);
		return super.onClose();
	}

	public void add(Field f) {
		manager.add(f);
	}

	public void makeMenu(Menu menu, int instance) {
		if (results.getFieldWithFocus() != null) {
			menu.add(m_openTask);
			menu.setDefault(m_openTask);
		}
		super.makeMenu(menu, instance);
	}

	private void openTask() {
		UiApplication.getUiApplication().pushScreen(
				new TaskScreen(((TaskField) results.getFieldWithFocus())
						.getTask()));
	}

	private MenuItem m_openTask = new MenuItem("Open Task", 110, 1) {
		public void run() {
			openTask();
		}
	};

	public void searchRequest(String query) {
		results.deleteAll();
		Task[] tasks = TaskUtils.findTasks(query);
		if(tasks.length > 0)
			results.add(tasks);
		else {
			HorizontalFieldManager hm = new HorizontalFieldManager();
			LabelField noneFound = new LabelField("Sorry, no matches for \"" + query + "\"", Field.USE_ALL_WIDTH
					| DrawStyle.HCENTER) {
				public void paint(Graphics g) {
					g.setColor(0x00444444);
					super.paint(g);
				}
			};
			noneFound.setFont(getFont().derive(Font.PLAIN,
					getFont().getHeight() - 3));
			noneFound.setPadding(3,3,3,3);
			hm.add(noneFound);
			results.add(hm);
		}
	}

}
