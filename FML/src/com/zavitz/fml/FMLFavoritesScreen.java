package com.zavitz.fml;

import java.util.Timer;
import java.util.TimerTask;

import com.zavitz.fml.data.Config;
import com.zavitz.fml.data.FMLFavorites;
import com.zavitz.fml.fields.PostField;
import com.zavitz.fml.fields.TitleField;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Characters;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;

public class FMLFavoritesScreen extends MainScreen {
	
	private VerticalFieldManager internalManager, manager;
	private Timer loadingTimer;
	private LoadingField loadingField;
	private static Bitmap iconBtmp;
	private boolean add;
	private String id;
	
	static {
		iconBtmp = Bitmap.getBitmapResource("FMLIcon_32.png");
	}

	public FMLFavoritesScreen(boolean add, String id) {
		super(Manager.NO_VERTICAL_SCROLL | Manager.NO_VERTICAL_SCROLLBAR);
		this.add = add;
		this.id = id;

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

		super.add(internalManager);
		
		new FMLFavorites(this, add, id);
	}

	public void add(Field field) {
		manager.add(field);
	}
	
	public void started() {
		loadingTimer = new Timer();
		TimerTask animate = new TimerTask() {
			public void run() {
				loadingField.animate();
			}
		};
		loadingTimer.scheduleAtFixedRate(animate, 1000 / 16, 1000 / 16);
		getUi().invokeAndWait(new Runnable() {
			public void run() {
				add(loadingField);
			}
		});
	}
	
	public void done() {
		loadingTimer.cancel();
		getUi().invokeAndWait(new Runnable() {
			public void run() {
				manager.deleteAll();
				add(new Message("Successfully " + (add ? "added" : "removed") + " story " + (add ? "to" : "from") + " favorites."));
				add(new CloseButton());
			}
		});
	}

	private UiApplication getUi() {
		return UiApplication.getUiApplication();
	}
	
	public class LoadingField extends Field {
		
		private String text = add ? "Adding story to favorites..." : "Removing story from favorites...";
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
			

			g.drawBitmap((getPreferredWidth() - iconBtmp.getWidth()) / 2,
					10, iconBtmp.getWidth(), iconBtmp
							.getHeight(), iconBtmp, 0, 0);

			g.setGlobalAlpha(alpha);
			g.setColor(Config.WHITE);
			g.fillRect((getPreferredWidth() - iconBtmp.getWidth()) / 2,
					10, iconBtmp.getWidth(), iconBtmp
							.getHeight());
			g.setGlobalAlpha(255);

			g.setColor(Config.DEFAULT_BLUE);			
			if(!getFont().equals(g.getFont()))
				g.setFont(getFont());
			g.drawText(text, 0, (10 + iconBtmp.getHeight()) + 4, DrawStyle.HCENTER
					| DrawStyle.TOP, getPreferredWidth());

		}
		
		public void animate() {
			int i = alpha + mod;
			if(i <= 0) {
				mod *= -1;
				i = 0;
			}
			if(i >= 255) {
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
			setText(add ? "Adding story to favorites..." : "Removing story from favorites...");
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
			g.drawText("OK", 0, getPreferredHeight() / 2, DrawStyle.HCENTER
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
