package com.zavitz.fml;

import java.util.Timer;
import java.util.TimerTask;

import com.zavitz.fml.FMLLoginScreen.CloseButton;
import com.zavitz.fml.FMLLoginScreen.Form;
import com.zavitz.fml.FMLLoginScreen.LoginButton;
import com.zavitz.fml.FMLLoginScreen.Message;
import com.zavitz.fml.data.Config;
import com.zavitz.fml.data.FMLPostComment;
import com.zavitz.fml.data.MD5;
import com.zavitz.fml.data.PStore;
import com.zavitz.fml.fields.PostField;
import com.zavitz.fml.fields.TitleField;

import net.rim.device.api.system.Characters;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.AutoTextEditField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;

public class FMLRegisterScreen extends MainScreen {

	private VerticalFieldManager internalManager, manager;
	private Form form;

	public FMLRegisterScreen() {
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

		form = new Form();
		add(form);
		add(new RegisterButton());

		super.add(internalManager);
	}

	public void add(Field f) {
		manager.add(f);
	}

	private void register() {
		synchronized (UiApplication.getEventLock()) {
			String PIN = Integer.toString(DeviceInfo.getDeviceId(), 16);
			String APP = "FML";
			String KEY = MD5.get(new String(PIN + APP).toLowerCase())
					.substring(0, 9).toUpperCase();

			if (KEY.equalsIgnoreCase(form.key.getText())) {
				Config.REGISTRATION_KEY = KEY;
				PStore.register();
				UiApplication.getUiApplication()
						.pushScreen(new FMLMainScreen());
				UiApplication.getUiApplication().popScreen(this);
			} else {
				manager.insert(new Message("Sorry, that key is invalid."), 0);
			}
		}
	}

	class Form extends VerticalFieldManager {

		public AutoTextEditField key;
		public LabelField notifier;

		public Form() {
			super(FOCUSABLE);

			LabelField header = new LabelField("Enter Your Activation Key:");
			header
					.setFont(header.getFont().derive(Font.BOLD,
							Config.FONT_SIZE));

			key = new AutoTextEditField("", "", 9, 0) {
				public void paint(Graphics g) {
					g.setColor(Config.GRAY);
					super.paint(g);
				}
			};
			key.setFont(header.getFont().derive(Font.PLAIN));

			add(header);
			add(key);
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
				layoutChild(f, getPreferredWidth() - 20, f.getPreferredHeight());
				setPositionChild(f, 10, h);
				h += f.getPreferredHeight() + 5;
			}

			setExtent(getPreferredWidth(), getPreferredHeight());
		}

	}

	class RegisterButton extends Field {

		public RegisterButton() {
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
			g.drawText("Register", 0, getPreferredHeight() / 2,
					DrawStyle.HCENTER | DrawStyle.VCENTER, getPreferredWidth());
		}

		public boolean navigationClick(int status, int time) {
			register();
			return true;
		}

		public boolean keyChar(char character, int status, int time) {
			if (character == Characters.ENTER) {
				register();
				return true;
			}
			return super.keyChar(character, status, time);
		}

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

}