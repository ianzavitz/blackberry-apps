package com.zavitz.fml;

import com.zavitz.fml.data.Config;
import com.zavitz.fml.data.PStore;
import com.zavitz.fml.data.URL;
import com.zavitz.fml.fields.TitleField;

import net.rim.device.api.system.Characters;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.component.RadioButtonField;
import net.rim.device.api.ui.component.RadioButtonGroup;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;

public class FMLOptionsScreen extends MainScreen {

	private VerticalFieldManager internalManager, manager;
	public ConnectionForm connectionForm;
	private WapConnectionForm wapConnectionForm;
	private RandomForm randomForm;
	private boolean restartRequired = false;

	// Add options for default category
	public FMLOptionsScreen() {
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

		wapConnectionForm = new WapConnectionForm();
		connectionForm = new ConnectionForm();
		randomForm = new RandomForm();
		manager.add(connectionForm);
		manager.add(new DiagnoseButton());
		manager.add(randomForm);
		manager.add(new CloseButton());

		connectionForm.connectionGroup.setSelectedIndex(Config.CONNECTION_TYPE);
	}

	public void makeMenu(Menu menu, int instance) {
		menu.add(new MenuItem("Save", 110, 1) {
			public void run() {
				save();
				setDirty(false);
				close();
			}
		});
		menu.addSeparator();
		menu.add(new MenuItem("Close", 110, 1) {
			public void run() {
				close();
			}
		});
	}

	private FMLOptionsScreen getInstance() {
		return this;
	}

	private UiApplication getUi() {
		return UiApplication.getUiApplication();
	}

	public void save() {
		int i = Config.FONT_SIZE;
		Config.FONT_SIZE = Integer.parseInt((String) randomForm.font
				.getChoice(randomForm.font.getSelectedIndex()));
		if (i != Config.FONT_SIZE)
			restartRequired = true;
		Config.CONNECTION_TYPE = connectionForm.connectionGroup
				.getSelectedIndex();
		if (Config.CONNECTION_TYPE == 5) {
			URL.CARRIER = wapConnectionForm.connectionGroup.getSelectedIndex();
			if (URL.CARRIER == URL.CUSTOM) {
				URL.wapGatewayAPN = wapConnectionForm.accessPoint.getText();
				URL.wapGatewayIP = wapConnectionForm.gatewayIP.getText();
				URL.wapGatewayPort = wapConnectionForm.gatewayPort.getText();
				URL.tunnelAuthUsername = wapConnectionForm.username.getText();
				URL.tunnelAuthPassword = wapConnectionForm.password.getText();
			}
		}
		PStore.store();
	}

	public void close() {
		super.close();

		if (restartRequired)
			((FMLUiApplication) getUi()).restart();
	}
	
	class DiagnoseButton extends Field {

		public DiagnoseButton() {
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
			g.drawText("Diagnose Connection", 0, getPreferredHeight() / 2,
					DrawStyle.HCENTER | DrawStyle.VCENTER, getPreferredWidth());
		}

		public boolean navigationClick(int status, int time) {
			getUi().pushScreen(new FMLDiagnoseScreen());
			return true;
		}

		public boolean keyChar(char character, int status, int time) {
			if (character == Characters.ENTER) {
				getUi().pushScreen(new FMLDiagnoseScreen());
			}
			return true;
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

	class ConnectionForm extends VerticalFieldManager {

		public RadioButtonGroup connectionGroup;

		public ConnectionForm() {
			super(FOCUSABLE);

			LabelField header = new LabelField("Connection Information") {
				public void paint(Graphics g) {
					g.setColor(Config.DEFAULT_BLUE);
					super.paint(g);
				}
			};
			header
					.setFont(header.getFont().derive(Font.BOLD,
							Config.FONT_SIZE));

			LabelField active = new LabelField("Active Connection: "
					+ URL.getConnectionType()) {
				public void paint(Graphics g) {
					g.setColor(Config.GRAY);
					super.paint(g);
				}
			};
			active.setFont(header.getFont().derive(Font.PLAIN));

			LabelField choose = new LabelField("Select Preferred Connection:") {
				public void paint(Graphics g) {
					g.setColor(Config.GRAY);
					super.paint(g);
				}
			};
			choose.setFont(header.getFont().derive(Font.PLAIN));

			add(header);
			add(active);
			add(choose);

			RadioButtonField auto = new RadioButtonField("Auto") {
				public void paint(Graphics g) {
					g.setColor(Config.GRAY);
					super.paint(g);
				}
			};
			auto.setFont(choose.getFont());
			RadioButtonField bis = new RadioButtonField(
					"BlackBerry Internet Service") {
				public void paint(Graphics g) {
					g.setColor(Config.GRAY);
					super.paint(g);
				}
			};
			bis.setFont(choose.getFont());
			RadioButtonField bes = new RadioButtonField(
					"BlackBerry Enterprise Server") {
				public void paint(Graphics g) {
					g.setColor(Config.GRAY);
					super.paint(g);
				}
			};
			bes.setFont(choose.getFont());
			RadioButtonField tcpip = new RadioButtonField("TCP/IP") {
				public void paint(Graphics g) {
					g.setColor(Config.GRAY);
					super.paint(g);
				}
			};
			tcpip.setFont(choose.getFont());
			RadioButtonField wap2 = new RadioButtonField("WAP 2.0") {
				public void paint(Graphics g) {
					g.setColor(Config.GRAY);
					super.paint(g);
				}
			};
			wap2.setFont(choose.getFont());
			final RadioButtonField wap1 = new RadioButtonField("WAP 1.0") {
				public void paint(Graphics g) {
					g.setColor(Config.GRAY);
					super.paint(g);
				}
			};
			wap1.setFont(choose.getFont());
			RadioButtonField wifi = new RadioButtonField("WiFi") {
				public void paint(Graphics g) {
					g.setColor(Config.GRAY);
					super.paint(g);
				}
			};
			wifi.setFont(choose.getFont());

			connectionGroup = new RadioButtonGroup();
			connectionGroup.setChangeListener(new FieldChangeListener() {

				public void fieldChanged(Field field, int context) {
					if (field == wap1) {
						boolean flag = true;
						for (int i = 0; i < manager.getFieldCount(); i++)
							if (manager.getField(i) == wapConnectionForm)
								flag = false;
						if (flag) {
							synchronized (UiApplication.getEventLock()) {
								manager.insert(wapConnectionForm, 1);
								wapConnectionForm.setFocus();
							}
						}
					} else {
						boolean flag = true;
						for (int i = 0; i < manager.getFieldCount(); i++)
							if (manager.getField(i) == wapConnectionForm)
								flag = false;
						if (!flag)
							synchronized (UiApplication.getEventLock()) {
								manager.delete(wapConnectionForm);
							}
					}
				}

			});
			connectionGroup.add(auto);
			add(auto);
			connectionGroup.add(bis);
			add(bis);
			connectionGroup.add(bes);
			add(bes);
			connectionGroup.add(wap2);
			add(wap2);
			connectionGroup.add(tcpip);
			add(tcpip);
			connectionGroup.add(wap1);
			add(wap1);
			connectionGroup.add(wifi);
			add(wifi);
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

	class WapConnectionForm extends VerticalFieldManager {

		public RadioButtonGroup connectionGroup;
		public EditField accessPoint, gatewayIP, gatewayPort, username,
				password;

		public WapConnectionForm() {
			super(FOCUSABLE);

			LabelField header = new LabelField("WAP Settings") {
				public void paint(Graphics g) {
					g.setColor(Config.DEFAULT_BLUE);
					super.paint(g);
				}
			};
			header
					.setFont(header.getFont().derive(Font.BOLD,
							Config.FONT_SIZE));

			LabelField choose = new LabelField("Select Carrier:") {
				public void paint(Graphics g) {
					g.setColor(Config.GRAY);
					super.paint(g);
				}
			};
			choose.setFont(header.getFont().derive(Font.PLAIN));

			add(header);
			add(choose);

			RadioButtonField custom = new RadioButtonField("Custom") {
				public void paint(Graphics g) {
					g.setColor(Config.GRAY);
					super.paint(g);
				}
			};
			custom.setFont(choose.getFont());
			RadioButtonField att = new RadioButtonField("AT&T (USA)") {
				public void paint(Graphics g) {
					g.setColor(Config.GRAY);
					super.paint(g);
				}
			};
			att.setFont(choose.getFont());
			RadioButtonField bell = new RadioButtonField("Bell (Canada)") {
				public void paint(Graphics g) {
					g.setColor(Config.GRAY);
					super.paint(g);
				}
			};
			bell.setFont(choose.getFont());
			RadioButtonField cingular = new RadioButtonField("Cingular (USA)") {
				public void paint(Graphics g) {
					g.setColor(Config.GRAY);
					super.paint(g);
				}
			};
			cingular.setFont(choose.getFont());
			RadioButtonField o2 = new RadioButtonField("O2 (UK)") {
				public void paint(Graphics g) {
					g.setColor(Config.GRAY);
					super.paint(g);
				}
			};
			o2.setFont(choose.getFont());
			RadioButtonField rogers = new RadioButtonField("Rogers (Canada)") {
				public void paint(Graphics g) {
					g.setColor(Config.GRAY);
					super.paint(g);
				}
			};
			rogers.setFont(choose.getFont());
			RadioButtonField sfr = new RadioButtonField("SFR (France)") {
				public void paint(Graphics g) {
					g.setColor(Config.GRAY);
					super.paint(g);
				}
			};
			sfr.setFont(choose.getFont());
			RadioButtonField sunrise = new RadioButtonField(
					"Sunrise (Switzerland)") {
				public void paint(Graphics g) {
					g.setColor(Config.GRAY);
					super.paint(g);
				}
			};
			sunrise.setFont(choose.getFont());
			RadioButtonField telefonica = new RadioButtonField(
					"Telefonica Movistar (Spain)") {
				public void paint(Graphics g) {
					g.setColor(Config.GRAY);
					super.paint(g);
				}
			};
			telefonica.setFont(choose.getFont());
			RadioButtonField tmobile = new RadioButtonField("T-Mobile (USA)") {
				public void paint(Graphics g) {
					g.setColor(Config.GRAY);
					super.paint(g);
				}
			};
			tmobile.setFont(choose.getFont());
			RadioButtonField vodafone_germany = new RadioButtonField(
					"Vodafone (Germany)") {
				public void paint(Graphics g) {
					g.setColor(Config.GRAY);
					super.paint(g);
				}
			};
			vodafone_germany.setFont(choose.getFont());
			RadioButtonField vodafone_ireland = new RadioButtonField(
					"Vodafone (Ireland)") {
				public void paint(Graphics g) {
					g.setColor(Config.GRAY);
					super.paint(g);
				}
			};
			vodafone_ireland.setFont(choose.getFont());
			RadioButtonField vodafone_sweden = new RadioButtonField(
					"Vodafone (Sweden)") {
				public void paint(Graphics g) {
					g.setColor(Config.GRAY);
					super.paint(g);
				}
			};
			vodafone_sweden.setFont(choose.getFont());
			RadioButtonField vodafone_uk = new RadioButtonField("Vodafone (UK)") {
				public void paint(Graphics g) {
					g.setColor(Config.GRAY);
					super.paint(g);
				}
			};
			vodafone_uk.setFont(choose.getFont());

			connectionGroup = new RadioButtonGroup();
			connectionGroup.add(custom);
			add(custom);
			connectionGroup.add(att);
			add(att);
			connectionGroup.add(bell);
			add(bell);
			connectionGroup.add(cingular);
			add(cingular);
			connectionGroup.add(o2);
			add(o2);
			connectionGroup.add(rogers);
			add(rogers);
			connectionGroup.add(sfr);
			add(sfr);
			connectionGroup.add(sunrise);
			add(sunrise);
			connectionGroup.add(telefonica);
			add(telefonica);
			connectionGroup.add(tmobile);
			add(tmobile);
			connectionGroup.add(vodafone_germany);
			add(vodafone_germany);
			connectionGroup.add(vodafone_ireland);
			add(vodafone_ireland);
			connectionGroup.add(vodafone_sweden);
			add(vodafone_sweden);
			connectionGroup.add(vodafone_uk);
			add(vodafone_uk);

			connectionGroup.setSelectedIndex(URL.CARRIER);

			LabelField customSettings = new LabelField("Custom Settings") {
				public void paint(Graphics g) {
					g.setColor(Config.GRAY);
					super.paint(g);
				}
			};
			customSettings.setFont(choose.getFont().derive(Font.BOLD));

			accessPoint = new EditField("Access Point: ", URL.wapGatewayAPN) {
				public void paint(Graphics g) {
					g.setColor(Config.GRAY);
					super.paint(g);
				}
			};
			accessPoint.setFont(choose.getFont());
			gatewayIP = new EditField("Gateway IP: ", URL.wapGatewayIP) {
				public void paint(Graphics g) {
					g.setColor(Config.GRAY);
					super.paint(g);
				}
			};
			gatewayIP.setFont(choose.getFont());
			gatewayPort = new EditField("Gateway Port: ", URL.wapGatewayPort) {
				public void paint(Graphics g) {
					g.setColor(Config.GRAY);
					super.paint(g);
				}
			};
			gatewayPort.setFont(choose.getFont());
			username = new EditField("Username: ", URL.tunnelAuthUsername) {
				public void paint(Graphics g) {
					g.setColor(Config.GRAY);
					super.paint(g);
				}
			};
			username.setFont(choose.getFont());
			password = new EditField("Password: ", URL.tunnelAuthPassword) {
				public void paint(Graphics g) {
					g.setColor(Config.GRAY);
					super.paint(g);
				}
			};
			password.setFont(choose.getFont());
			add(customSettings);
			add(accessPoint);
			add(gatewayIP);
			add(gatewayPort);
			add(username);
			add(password);
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

	class RandomForm extends VerticalFieldManager {

		public ObjectChoiceField font;

		public RandomForm() {
			super(FOCUSABLE);
			LabelField header = new LabelField("Other Information") {
				public void paint(Graphics g) {
					g.setColor(Config.DEFAULT_BLUE);
					super.paint(g);
				}
			};
			header
					.setFont(header.getFont().derive(Font.BOLD,
							Config.FONT_SIZE));

			font = new ObjectChoiceField("Font Size: ", new String[] {
					"10", "11", "12", "13", "14", "15", "16", "17", "18", "19",
					"20" }) {
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
			font.setFont(header.getFont().derive(Font.PLAIN));
			String current = String.valueOf(Config.FONT_SIZE);
			for (int i = 0; i < font.getSize(); i++)
				if (((String) font.getChoice(i)).equals(current))
					font.setSelectedIndex(i);

			add(header);
			add(font);
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
			return true;
		}

		public boolean keyChar(char character, int status, int time) {
			if (character == Characters.ENTER) {
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
			onClose();
			return true;
		}

		public boolean keyChar(char character, int status, int time) {
			if (character == Characters.ENTER) {
				onClose();
				return true;
			}
			return super.keyChar(character, status, time);
		}

	}

}