package com.zavitz.fml;

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
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.EmailAddressEditField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;

import com.zavitz.fml.data.Config;
import com.zavitz.fml.data.FMLSignup;
import com.zavitz.fml.data.PStore;
import com.zavitz.fml.data.SignupReceiver;
import com.zavitz.fml.fields.PostField;
import com.zavitz.fml.fields.TitleField;

public class FMLSignupScreen extends MainScreen implements SignupReceiver {

	private VerticalFieldManager internalManager, manager;
	private LoadingField loadingField;
	private Form form;
	private static Bitmap iconBtmp;

	static {
		iconBtmp = Bitmap.getBitmapResource("FMLIcon_32.png");
	}

	public FMLSignupScreen() {
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
		internalManager.add(new TitleField());

		manager = new VerticalFieldManager(Manager.VERTICAL_SCROLL
				| Manager.VERTICAL_SCROLLBAR) {
			public boolean navigationMovement(int dx, int dy, int status,
					int time) {
				boolean b = super.navigationMovement(dx, dy, status, time);
				for (int i = 0; i < getFieldCount(); i++)
					if (getField(i) instanceof PostField)
						((PostField) getField(i)).invalidate();
				return b;
			}
		};
		internalManager.add(manager);

		loadingField = new LoadingField();

		form = new Form();
		add(form);
		add(new CreateButton());

		super.add(internalManager);
	}

	public void makeMenu(Menu menu, int instance) {
		menu.add(new MenuItem("Options", 110, 1) {
			public void run() {
				getUi().pushScreen(new FMLOptionsScreen());
			}
		});
		menu.add(new MenuItem("Close", 110, 1) {
			public void run() {
				close();
			}
		});
	}

	public void close() {
		setDirty(false);
		super.close();
	}

	public SignupReceiver getInstance() {
		return this;
	}

	public void add(Field field) {
		manager.add(field);
	}

	private UiApplication getUi() {
		return UiApplication.getUiApplication();
	}

	public class LoadingField extends Field {

		private String text = "Creating account... Please wait.";
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
			setText("Creating account... Please wait.");
		}

	}

	class CreateButton extends Field {

		public CreateButton() {
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
			g.drawText("Create Account", 0, getPreferredHeight() / 2,
					DrawStyle.HCENTER | DrawStyle.VCENTER, getPreferredWidth());
		}

		public boolean navigationClick(int status, int time) {
			new FMLSignup(getInstance(), loadingField, form.username.getText(),
					form.password.getText(), form.email.getText());
			return true;
		}

		public boolean keyChar(char character, int status, int time) {
			if (character == Characters.ENTER) {
				new FMLSignup(getInstance(), loadingField, form.username
						.getText(), form.password.getText(), form.email
						.getText());
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

	class Form extends VerticalFieldManager {

		public EditField username, password;
		public EmailAddressEditField email;

		public Form() {
			super(FOCUSABLE);

			LabelField header = new LabelField("Create an Account:");
			header
					.setFont(header.getFont().derive(Font.BOLD,
							Config.FONT_SIZE));

			username = new EditField("Username: ", "") {
				public void paint(Graphics g) {
					g.setColor(Config.GRAY);
					super.paint(g);
				}
			};
			username.setFont(header.getFont().derive(Font.PLAIN));

			password = new EditField("Password: ", "") {
				public void paint(Graphics g) {
					g.setColor(Config.GRAY);
					super.paint(g);
				}
			};
			password.setFont(username.getFont());

			email = new EmailAddressEditField("Email: ", "") {
				public void paint(Graphics g) {
					g.setColor(Config.GRAY);
					super.paint(g);
				}
			};
			email.setFont(username.getFont());

			add(header);
			add(username);
			add(password);
			add(email);
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
			g.setColor(Config.WHITE);
			g.fillRoundRect(5, 5, getPreferredWidth() - 10,
					getContentHeight() - 10, 15, 15);
			g.setColor(Config.DEFAULT_BLUE);
			g.drawRoundRect(5, 5, getPreferredWidth() - 10,
					getContentHeight() - 10, 15, 15);
			g.setFont(getFont());
			subpaint(g);
		}

		public void sublayout(int width, int height) {
			int h = 10;

			for (int i = 0; i < getFieldCount(); i++) {
				Field f = getField(i);
				layoutChild(f, getPreferredWidth() - 10, f.getPreferredHeight());
				setPositionChild(f, 10, h);
				h += f.getPreferredHeight() + 5;
			}

			setExtent(getPreferredWidth(), getPreferredHeight());
		}

	}

	public void signupComplete(String s) {
		if (s.indexOf("<token>") < s.indexOf("</token>")) {
			String token = s.substring(s.indexOf("<token>") + 7, s
					.indexOf("</token>"));
			PStore.TOKEN = token;
			PStore.USERNAME = form.username.getText();
			PStore.PASSWORD = form.password.getText();
			PStore.store();
			synchronized (getUi().getEventLock()) {
				manager.deleteAll();
				add(new com.zavitz.fml.fields.Message(
						"Signed up and logged in!"));
				add(new CloseButton());
				return;
			}
		} else
			synchronized (getUi().getEventLock()) {
				manager.deleteAll();
				manager.add(new com.zavitz.fml.fields.Message(
						"Something is wrong, try again."));
				manager.add(form);
				manager.add(new CreateButton());
			}

	}

	public void signupStarting() {
		synchronized (getUi().getEventLock()) {
			manager.deleteAll();
			manager.add(loadingField);
		}
	}

}
