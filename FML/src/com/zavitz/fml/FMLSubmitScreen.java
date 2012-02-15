package com.zavitz.fml;

import java.util.Timer;
import com.zavitz.fml.data.Config;
import com.zavitz.fml.data.FMLSubmitStory;
import com.zavitz.fml.data.PStore;
import com.zavitz.fml.data.SubmitReceiver;
import com.zavitz.fml.fields.TitleField;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Characters;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.AutoTextEditField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;

public class FMLSubmitScreen extends MainScreen implements SubmitReceiver {

	private VerticalFieldManager internalManager, manager;
	private LoadingField loadingField;
	private static Bitmap iconBtmp;
	private Timer loadingTimer;
	private Form form;

	static {
		iconBtmp = Bitmap.getBitmapResource("FMLIcon_32.png");
	}

	public FMLSubmitScreen() {
		super(Manager.NO_VERTICAL_SCROLL | Manager.NO_VERTICAL_SCROLLBAR);

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

		loadingField = new LoadingField();
		form = new Form();

		add(form);
		add(new PostButton());
	}

	public void add(Field field) {
		manager.add(field);
	}

	public void submitSubmitted() {
		synchronized (getUi().getEventLock()) {
			manager.deleteAll();
			manager.add(new Message("Story successfully posted."));
			manager.add(new CloseButton());
		}
	}

	public void submitLoading() {
		synchronized (getUi().getEventLock()) {
			manager.deleteAll();
			manager.add(loadingField);
		}
	}

	public void makeMenu(Menu menu, int instance) {
		menu.add(new MenuItem("Options", 110, 1) {
			public void run() {
				getUi().pushScreen(new FMLOptionsScreen());
			}
		});
		menu.add(new MenuItem("Close", 110, 1) {
			public void run() {
				setDirty(false);
				close();
			}
		});
	}

	public SubmitReceiver getInstance() {
		return this;
	}

	private UiApplication getUi() {
		return UiApplication.getUiApplication();
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

	public class LoadingField extends Field {

		private String text = "Submitting story...";
		private int alpha = 0;
		private int mod = 20;

		public LoadingField() {
			super();
			setFont(getFont().derive(Font.BOLD, Config.FONT_SIZE));
		}

		public int getPreferredHeight() {
			return 20 + Config.FONT_SIZE + 36;
		}

		public int getPreferredWidth() {
			return Display.getWidth();
		}

		protected void layout(int width, int height) {
			setExtent(getPreferredWidth(), getPreferredHeight());
		}

		protected void paint(Graphics g) {
			g.setColor(Config.WHITE);
			g.fillRoundRect(5, 5, getPreferredWidth() - 10,
					getContentHeight() - 10, 15, 15);
			g.setColor(Config.DEFAULT_BLUE);
			g.drawRoundRect(5, 5, getPreferredWidth() - 10,
					getContentHeight() - 10, 15, 15);

			g.drawBitmap((getPreferredWidth() - iconBtmp.getWidth()) / 2, 10,
					iconBtmp.getWidth(), iconBtmp.getHeight(), iconBtmp, 0, 0);

			g.setGlobalAlpha(alpha);
			g.setColor(Config.WHITE);
			g.fillRect((getPreferredWidth() - iconBtmp.getWidth()) / 2, 10,
					iconBtmp.getWidth(), iconBtmp.getHeight());
			g.setGlobalAlpha(255);

			g.setColor(Config.DEFAULT_BLUE);
			if (!getFont().equals(g.getFont()))
				g.setFont(getFont());
			g.drawText(text, 0, (10 + iconBtmp.getHeight()) + 4,
					DrawStyle.HCENTER | DrawStyle.TOP, getPreferredWidth());

		}

		public void animate() {
			int i = alpha + mod;
			if (i <= 0) {
				mod *= -1;
				i = 0;
			}
			if (i >= 255) {
				mod *= -1;
				i = 255;
			}
			alpha = i;
			invalidate();
		}

		public void setText(String s) {
			text = s;
			invalidate();
		}

		public void resetText() {
			setText("Submitting story...");
		}

	}

	class Form extends VerticalFieldManager {

		public AutoTextEditField author, text;
		public ObjectChoiceField category;

		public Form() {
			super(FOCUSABLE);

			LabelField header = new LabelField("Submit Your Story:") {
				public void paint(Graphics g) {
					g.setColor(Config.DEFAULT_BLUE);
					super.paint(g);
				}
			};
			header
					.setFont(header.getFont().derive(Font.BOLD,
							Config.FONT_SIZE));

			author = new AutoTextEditField("Author: ", PStore.USERNAME) {
				public void paint(Graphics g) {
					g.setColor(Config.GRAY);
					super.paint(g);
				}
			};
			author.setFont(header.getFont().derive(Font.PLAIN));

			category = new ObjectChoiceField("Category: ", new String[] {
					"Love", "Money", "Work", "Health", "Sex", "Miscellaneous" }) {
				public void paint(Graphics g) {
					g.clear();
					if (formFocus() != this)
						g.setColor(Config.GRAY);
					super.paint(g);
				}

				public int getPreferredWidth() {
					return Display.getWidth() - 20;
				}

				protected void layout(int width, int height) {
					width = getPreferredWidth();
					height = getPreferredHeight();
					super.layout(width, height);
					setExtent(width, height);
				}
			};
			category.setFont(author.getFont());

			text = new AutoTextEditField("Text (300 char. remaining):\n", "") {
				public void paint(Graphics g) {
					g.setColor(Config.GRAY);
					super.paint(g);
				}

				public boolean keyChar(char key, int status, int time) {
					if (300 - getText().length() <= 0)
						return true;
					text.setLabel("Text (" + (300 - (getText().length() + 1))
							+ " char. remaining):\n");
					return super.keyChar(key, status, time);
				}
			};
			text.setFont(author.getFont());

			add(header);
			add(author);
			add(category);
			add(text);
		}

		public Field formFocus() {
			return getFieldWithFocus();
		}

		public int getPreferredHeight() {
			int i = 20 + ((getFieldCount() - 1) * 5);
			for (int j = 0; j < getFieldCount(); j++)
				i += getField(j).getContentHeight();
			return i;
		}

		public int getPreferredWidth() {
			return Display.getWidth();
		}

		protected void paint(Graphics g) {
			int c = g.getColor();
			g.setColor(Config.WHITE);
			g.fillRoundRect(5, 5, getPreferredWidth() - 10,
					getContentHeight() - 10, 15, 15);
			g.setColor(Config.DEFAULT_BLUE);
			g.drawRoundRect(5, 5, getPreferredWidth() - 10,
					getContentHeight() - 10, 15, 15);
			g.setFont(getFont());
			g.setColor(c);
			subpaint(g);
		}

		public void sublayout(int width, int height) {
			int h = 10;

			for (int i = 0; i < getFieldCount(); i++) {
				Field f = getField(i);
				layoutChild(f, getPreferredWidth() - 20, f.getPreferredHeight());
				setPositionChild(f, 10, h);
				h += f.getPreferredHeight() + 5;
			}

			setExtent(getPreferredWidth(), getPreferredHeight());
		}

	}

	class PostButton extends Field {

		public PostButton() {
			super(FOCUSABLE);
			setFont(getFont().derive(Font.BOLD, Config.FONT_SIZE));
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
			g.drawText("Submit", 0, getPreferredHeight() / 2, DrawStyle.HCENTER
					| DrawStyle.VCENTER, getPreferredWidth());
		}

		public boolean navigationClick(int status, int time) {
			new FMLSubmitStory(getInstance(), loadingField, form.author
					.getText(), ((String) form.category.getChoice(form.category
					.getSelectedIndex())).toLowerCase(), form.text.getText());
			return true;
		}

		public boolean keyChar(char character, int status, int time) {
			if (character == Characters.ENTER) {
				new FMLSubmitStory(getInstance(), loadingField, form.author
						.getText(), ((String) form.category
						.getChoice(form.category.getSelectedIndex()))
						.toLowerCase(), form.text.getText());
				return true;
			}
			return super.keyChar(character, status, time);
		}

	}

	class CloseButton extends Field {

		public CloseButton() {
			super(FOCUSABLE);
			setFont(getFont().derive(Font.BOLD, Config.FONT_SIZE));
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
			g.drawText("Close", 0, getPreferredHeight() / 2, DrawStyle.HCENTER
					| DrawStyle.VCENTER, getPreferredWidth());
		}

		public boolean navigationClick(int status, int time) {
			setDirty(false);
			close();
			return true;
		}

		public boolean keyChar(char character, int status, int time) {
			if (character == Characters.ENTER) {
				setDirty(false);
				close();
				return true;
			}
			return super.keyChar(character, status, time);
		}

	}

}