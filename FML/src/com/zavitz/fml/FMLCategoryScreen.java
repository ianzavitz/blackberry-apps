package com.zavitz.fml;

import net.rim.device.api.system.Characters;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.*;
import com.zavitz.fml.data.*;
import com.zavitz.fml.fields.*;

public class FMLCategoryScreen extends MainScreen {

	private VerticalFieldManager internalManager, manager;
	private CategoryReceiver _receiver;

	public FMLCategoryScreen(CategoryReceiver receiver) {
		super(Manager.NO_VERTICAL_SCROLL | Manager.NO_VERTICAL_SCROLLBAR);
		_receiver = receiver;

		internalManager = new VerticalFieldManager(Manager.NO_VERTICAL_SCROLL
				| Manager.NO_VERTICAL_SCROLLBAR) {
			public void paintBackground(Graphics g) {
				g.clear();
				int color = g.getColor();
				g.setColor(Config.BACKGROUND_BLUE);
				g.fillRect(0, 0, Display.getWidth(), Display.getHeight());
				g.setColor(color);
			}

			protected void sublayout(int width, int height) {
				super.sublayout(Display.getWidth(), Display.getHeight());
				setExtent(Display.getWidth(), Display.getHeight());
			}
		};
		super.add(internalManager);
		internalManager.add(new TitleField());

		manager = new VerticalFieldManager(Manager.VERTICAL_SCROLL
				| Manager.VERTICAL_SCROLLBAR);
		internalManager.add(manager);

		manager.add(new Message("Select a Category"));
		manager.add(new Category("Newest"));
		manager.add(new Category("Top FML"));
		manager.add(new Category("Flop FML"));
		manager.add(new Category("Search"));
		if(!PStore.TOKEN.equals(""))
			manager.add(new Category("Favorites"));
		manager.add(new Category("Random"));
		manager.add(new Category("Love"));
		manager.add(new Category("Money"));
		manager.add(new Category("Kids"));
		manager.add(new Category("Work"));
		manager.add(new Category("Health"));
		manager.add(new Category("Sex"));
		manager.add(new Category("Miscellaneous"));
		manager.add(new Category("Close"));
	}

	public void makeMenu(Menu menu, int instance) {
		menu.add(new MenuItem("Options", 110, 1) {
			public void run() {
				getUi().pushScreen(new FMLOptionsScreen());
			}
		});
		menu.add(new MenuItem("Close",110,1) {
			public void run() {
				setDirty(false);
				close();
			}
		});
	}
	
	private UiApplication getUi() {
		return UiApplication.getUiApplication();
	}

	private void exit() {
		setDirty(false);
		close();
	}

	class Message extends Field {

		private String text = "";

		public Message(String t) {
			super(NON_FOCUSABLE);
			setFont(getFont().derive(Font.BOLD, Config.FONT_SIZE));
			text = t;
		}

		public int getPreferredHeight() {
			return 20 + 10 + Config.FONT_SIZE;
		}

		public int getPreferredWidth() {
			return Display.getWidth();
		}

		protected void layout(int width, int height) {
			setExtent(getPreferredWidth(), getPreferredHeight());
		}

		private boolean focus = false;

		public void drawFocus(Graphics g, boolean on) {
			focus = true;
			paint(g);
			focus = false;
		}

		protected void paint(Graphics g) {
			g.setColor(Config.WHITE);
			g.fillRoundRect(5, 5, getPreferredWidth() - 10,
					getContentHeight() - 10, 15, 15);
			g.setColor(Config.DEFAULT_BLUE);
			if (focus)
				g.drawRoundRect(5, 5, getPreferredWidth() - 10,
						getContentHeight() - 10, 15, 15);
			else
				g.setColor(Config.GRAY);
			g.setFont(getFont());
			g.drawText(text, 0, getPreferredHeight() / 2, DrawStyle.HCENTER
					| DrawStyle.VCENTER, getPreferredWidth());
		}

	}

	class Category extends Field {

		private String name;
		private int color = Config.GRAY;

		public Category(String s) {
			super(FOCUSABLE);
			name = s;
			setFont(getFont().derive(Font.BOLD, Config.FONT_SIZE));
		}
		
		public Category(String s, int c) {
			this(s);
			color = c;
		}

		public int getPreferredHeight() {
			return 10 + 10 + Config.FONT_SIZE;
		}

		public int getPreferredWidth() {
			return Display.getWidth();
		}

		protected void layout(int width, int height) {
			setExtent(getPreferredWidth(), getPreferredHeight());
		}

		private boolean focus = false;

		public void drawFocus(Graphics g, boolean on) {
			focus = true;
			paint(g);
			focus = false;
		}

		protected void paint(Graphics g) {
			g.setColor(Config.WHITE);
			g.fillRoundRect(5, 5, getPreferredWidth() - 10,
					getContentHeight() - 10, 15, 15);
			g.setColor(Config.DEFAULT_BLUE);
			if (focus)
				g.drawRoundRect(5, 5, getPreferredWidth() - 10,
						getContentHeight() - 10, 15, 15);
			else
				g.setColor(color);
			g.setFont(getFont());
			g.drawText(name, 0, getPreferredHeight() / 2, DrawStyle.HCENTER
					| DrawStyle.VCENTER, getPreferredWidth());
		}

		public boolean navigationClick(int status, int time) {
			exit();
			_receiver.categoryReceived(name);
			return true;
		}

		public boolean keyChar(char character, int status, int time) {
			if (character == Characters.ENTER) {
				exit();
				_receiver.categoryReceived(name);
				return true;
			}
			return super.keyChar(character, status, time);
		}

	}

}
