package com.zavitz.fml;

import java.io.ByteArrayOutputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

import net.rim.device.api.system.Characters;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.*;
import com.zavitz.fml.data.*;
import com.zavitz.fml.fields.*;

public class FMLDiagnoseScreen extends MainScreen {

	private VerticalFieldManager internalManager, manager;
	private Connection bis, bes, wap2, tcpip, wap1, wifi;

	public FMLDiagnoseScreen() {
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

		bis = new Connection("BIS");
		bes = new Connection("BES");
		wap2 = new Connection("WAP 2.0");
		tcpip = new Connection("TCP/IP");
		wap1 = new Connection("WAP 1.0");
		wifi = new Connection("WiFi");

		LabelField categoryLabel = new LabelField("Connection Diagnostics",
				LabelField.HCENTER | LabelField.USE_ALL_WIDTH) {
			public void paint(Graphics g) {
				g.setColor(Config.GRAY);
				super.paint(g);
			}
		};
		categoryLabel
				.setFont(getFont().derive(Font.BOLD, Config.FONT_SIZE + 2));
		categoryLabel.setPadding(5, 0, 0, 0);
		manager.add(categoryLabel);

		manager.add(bis);
		manager.add(bes);
		manager.add(wap2);
		manager.add(tcpip);
		manager.add(wap1);
		manager.add(wifi);
		manager.add(new CloseButton());

		new Diagnose().start();
	}

	public void makeMenu(Menu menu, int instance) {
		Field manSelected = manager.getFieldWithFocus();
		if (manSelected instanceof Connection) {
			final Connection field = (Connection) manSelected;
			if (field.background == 0x00f6ffeb)
				menu.add(new MenuItem("Use This Connection", 110, 1) {
					public void run() {
						field.navigationClick(0, 0);
					}
				});
		}
		menu.add(new MenuItem("Close", 110, 1) {
			public void run() {
				setDirty(false);
				close();
			}
		});
	}

	private UiApplication getUi() {
		return UiApplication.getUiApplication();
	}

	private Screen getInstance() {
		return this;
	}

	private void exit() {
		synchronized (UiApplication.getEventLock()) {
			setDirty(false);
			close();
			((FMLUiApplication) getUi()).restart();
		}
	}

	class Diagnose extends Thread {

		public void run() {
			bis.checking();
			if (getConnection(URL.getSuffix(0))) {
				bis.success();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Config.CONNECTION_TYPE = 1;
				PStore.store();
				exit();
				return;
			} else
				bis.fail();
			bes.checking();
			if (getConnection(URL.getSuffix(1))) {
				bes.success();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Config.CONNECTION_TYPE = 2;
				PStore.store();
				exit();
				return;
			} else
				bes.fail();
			wap2.checking();
			if (getConnection(URL.getSuffix(2))) {
				wap2.success();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Config.CONNECTION_TYPE = 3;
				PStore.store();
				exit();
				return;
			} else
				wap2.fail();
			tcpip.checking();
			if (getConnection(URL.getSuffix(3))) {
				tcpip.success();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Config.CONNECTION_TYPE = 4;
				PStore.store();
				exit();
				return;
			} else
				tcpip.fail();
			wap1.checking();
			if (getConnection(URL.getSuffix(4))) {
				wap1.success();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Config.CONNECTION_TYPE = 5;
				PStore.store();
				exit();
				return;
			} else
				wap1.fail();
			wifi.checking();
			if (getConnection(URL.getSuffix(5))) {
				wifi.success();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Config.CONNECTION_TYPE = 6;
				PStore.store();
				exit();
				return;
			} else
				wifi.fail();
		}

	}

	public boolean getConnection(String param) {
		try {
			HttpConnection conn = (HttpConnection) Connector
					.open("http://www.google.com;ConnectionTimeout=10000"
							+ param);
			if (conn.getResponseCode() != HttpConnection.HTTP_OK)
				return false;
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			FMLPost.copy(conn.openInputStream(), output);
			if (output.size() > 0)
				return true;
			output.close();
			conn.close();
		} catch (Exception e) {
		}
		return false;
	}

	class Connection extends Field {

		private String text = "";
		private final String mainText;
		private int background = Config.WHITE;
		private int foreground = Config.GRAY;

		public Connection(String t) {
			super(FOCUSABLE);
			setFont(getFont().derive(Font.BOLD, Config.FONT_SIZE));
			text = t;
			mainText = text;
		}

		public boolean isFocusable() {
			return background != 0x00f8f0f0;
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

		public void checking() {
			text = mainText + " - Checking...";
			invalidate();
		}

		public void success() {
			background = 0x00f6ffeb;
			text = mainText + " - Success";
			invalidate();
		}

		public void fail() {
			background = 0x00f8f0f0;
			text = mainText + " - Failed";

			if (manager.getFieldWithFocus() == this)
				try {
					for (int i = 0; i < manager.getFieldCount(); i++)
						if (manager.getField(i) == this)
							manager.setFieldWithFocus(manager.getField(i + 1));
				} catch (Exception e) {

				}
			invalidate();
		}

		protected void paint(Graphics g) {
			g.setColor(background);
			g.fillRoundRect(5, 5, getPreferredWidth() - 10,
					getContentHeight() - 10, 15, 15);
			g.setColor(foreground);
			g.setFont(getFont());
			g.drawText(text, 10, getPreferredHeight() / 2, DrawStyle.VCENTER);
			if (focus) {
				g.setColor(Config.DEFAULT_BLUE);
				g.drawRoundRect(5, 5, getPreferredWidth() - 10,
						getContentHeight() - 10, 15, 15);
			}
		}

		public boolean navigationClick(int status, int time) {
			if (background == 0x00f6ffeb) {
				FMLOptionsScreen options = new FMLOptionsScreen();
				for (int i = 1; i < manager.getFieldCount(); i++)
					if (manager.getField(i) == this)
						options.connectionForm.connectionGroup
								.setSelectedIndex(i);
				getUi().pushScreen(options);
				exit();
			}
			return true;
		}

		public boolean keyChar(char character, int status, int time) {
			if (character == Characters.ENTER && background == 0x00f6ffeb) {
				FMLOptionsScreen options = new FMLOptionsScreen();
				for (int i = 1; i < manager.getFieldCount(); i++)
					if (manager.getField(i) == this)
						options.connectionForm.connectionGroup
								.setSelectedIndex(i);
				getUi().pushScreen(options);
				exit();
				return true;
			}
			return super.keyChar(character, status, time);
		}

	}

	class CloseButton extends Field {

		private int background = Config.WHITE;
		private int foreground = Config.GRAY;

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
			g.setColor(background);
			g.fillRoundRect(5, 5, getPreferredWidth() - 10,
					getContentHeight() - 10, 15, 15);
			g.setColor(foreground);
			g.setFont(getFont());
			g.drawText("Close", 0, getPreferredHeight() / 2, DrawStyle.VCENTER
					| DrawStyle.HCENTER, getPreferredWidth());
			if (focus) {
				g.setColor(Config.DEFAULT_BLUE);
				g.drawRoundRect(5, 5, getPreferredWidth() - 10,
						getContentHeight() - 10, 15, 15);
			}
		}

		public boolean navigationClick(int status, int time) {
			exit();
			return true;
		}

		public boolean keyChar(char character, int status, int time) {
			if (character == Characters.ENTER) {
				exit();
				return true;
			}
			return super.keyChar(character, status, time);
		}

	}
}