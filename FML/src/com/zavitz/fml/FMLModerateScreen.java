package com.zavitz.fml;

import java.util.Timer;
import java.util.TimerTask;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Characters;
import net.rim.device.api.system.Clipboard;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;

import com.zavitz.fml.data.Config;
import com.zavitz.fml.data.FMLModerate;
import com.zavitz.fml.data.ModReceiver;
import com.zavitz.fml.data.PStore;
import com.zavitz.fml.data.FMLModerate.ModPost;
import com.zavitz.fml.fields.ModField;
import com.zavitz.fml.fields.TitleField;

public class FMLModerateScreen extends MainScreen implements ModReceiver {

	private VerticalFieldManager internalManager, manager;
	private LoadingField loadingField;
	private static Bitmap iconBtmp;
	private Timer loadingTimer;
	private ModPost currentPost;
	private Button yes, no;
	private HorizontalFieldManager buttons;

	static {
		iconBtmp = Bitmap.getBitmapResource("FMLIcon_32.png");
	}

	public FMLModerateScreen() {
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
		yes = new Button("Y\u0332es", 0x00f6ffeb, 0x0087cc7b);
		no = new Button("N\u0332o", 0x00f8f0f0, 0x00ff7071);
		buttons = new HorizontalFieldManager();
		buttons.add(yes);
		buttons.add(no);

		new FMLModerate(this);
	}

	public void modLoading() {
		synchronized (getUi().getEventLock()) {
			manager.deleteAll();
			manager.add(loadingField);
			loadingTimer = new Timer();
			TimerTask animate = new TimerTask() {
				public void run() {
					loadingField.animate();
				}
			};
			loadingTimer.scheduleAtFixedRate(animate, 1000 / 16, 1000 / 16);
		}
	}

	public void modReceived(ModPost mod) {
		currentPost = mod;
		loadingTimer.cancel();

		synchronized (getUi().getEventLock()) {
			manager.deleteAll();
			manager.add(new ModField(currentPost));
			manager.add(buttons);
		}
	}

	public void makeMenu(Menu menu, int instance) {
		if (manager.getFieldCount() == 2) {
			menu.add(new MenuItem("Vote Y\u0332es", 110, 1) {
				public void run() {
					new FMLModerate(getInstance(), currentPost.id, true);
				}
			});
			menu.add(new MenuItem("Vote N\u0332o", 110, 1) {
				public void run() {
					new FMLModerate(getInstance(), currentPost.id, false);
				}
			});
			menu.addSeparator();

			menu.add(new MenuItem("Share Story", 110, 1) {
				public void run() {
					getUi().pushScreen(new FMLShareScreen(currentPost.text));
				}
			});

			menu.add(new MenuItem("Copy Story", 110, 1) {
				public void run() {
					Clipboard.getClipboard().put(currentPost.text);
				}
			});
		}
		//		
		// if(PStore.TOKEN.equals(""))
		// menu.add(new MenuItem("Login",110,1) {
		// public void run() {
		// getUi().pushScreen(new FMLLoginScreen(true));
		// }
		// });
		// else
		// menu.add(new MenuItem("Logout",110,1) {
		// public void run() {
		// PStore.TOKEN = "";
		// PStore.store();
		// getUi().pushScreen(new FMLLoginScreen(false));
		// }
		// });

		menu.addSeparator();
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

	private UiApplication getUi() {
		return UiApplication.getUiApplication();
	}

	public boolean keyChar(char character, int status, int time) {
		if (character == 'y') {
			if (manager.getFieldCount() == 2)
				new FMLModerate(getInstance(), currentPost.id, true);
			return true;
		}
		if (character == 'n') {
			if (manager.getFieldCount() == 2)
				new FMLModerate(getInstance(), currentPost.id, false);
			return true;
		}
		return super.keyChar(character, status, time);
	}

	public class LoadingField extends Field {

		private String text = "Retrieving story...";
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
			setText("Retrieving story...");
		}

	}

	public FMLModerateScreen getInstance() {
		return this;
	}

	class Button extends Field {

		private String text = "";
		private int background = Config.WHITE;
		private int border = 0x0;

		public Button(String t, int bg, int b) {
			super(FOCUSABLE);
			setFont(getFont().derive(Font.BOLD, Config.FONT_SIZE));
			background = bg;
			border = b;
			text = t;
		}

		public int getPreferredHeight() {
			return 20 + 10 + Config.FONT_SIZE;
		}

		public int getPreferredWidth() {
			return Display.getWidth() / 2;
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
			g.setColor(border);
			if (focus)
				g.drawRoundRect(5, 5, getPreferredWidth() - 10,
						getContentHeight() - 10, 15, 15);
			g.setColor(Config.GRAY);
			g.setFont(getFont());
			g.drawText(text, 0, getPreferredHeight() / 2, DrawStyle.HCENTER
					| DrawStyle.VCENTER, getPreferredWidth());
		}

		public boolean navigationClick(int status, int time) {
			new FMLModerate(getInstance(), currentPost.id, text.equals("Yes"));
			return true;
		}

		public boolean keyChar(char character, int status, int time) {
			if (character == Characters.ENTER) {
				new FMLModerate(getInstance(), currentPost.id, text
						.equals("Yes"));
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
