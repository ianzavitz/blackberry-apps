package com.zavitz.mytimes;

import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import net.rim.device.api.system.*;
import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.*;

public class Screensaver extends MainScreen {

	private static Screensaver instance;
	private boolean showing;
	private Timer timer;
	private VerticalFieldManager internalManager;
	private int currentX = 10;
	private int currentY = 10;
	private int movingX = 1;
	private int movingY = 1;
	private long lockInfo = 0;
	private int width;
	private int height;
	private int fontSize = 52;

	protected Screensaver() {
		super(Manager.NO_VERTICAL_SCROLL | Manager.NO_VERTICAL_SCROLLBAR);
		width = Display.getWidth();
		height = Display.getHeight();
		int count = (480 - width) / 10;
		fontSize -= count;

		internalManager = new VerticalFieldManager(Manager.NO_VERTICAL_SCROLL
				| Manager.NO_VERTICAL_SCROLLBAR) {

			private long lastBounce = 0;

			public void paintBackground(Graphics g) {
				g.clear();
				int color = g.getColor();
				g.setColor(0x0);
				g.fillRect(0, 0, Display.getWidth(), height);

				if (HomeScreen.worldMap == null)
					HomeScreen.worldMap = Images.getScaledImage("worldmap.png",
							width);

				int x = (width - HomeScreen.worldMap.getWidth()) / 2;
				if (x < 0)
					x = 0;
				int y = (height - HomeScreen.worldMap.getHeight()) / 2;
				if (y < 0)
					y = 0;

				g.drawBitmap(x, y, HomeScreen.worldMap.getWidth(),
						HomeScreen.worldMap.getHeight(), HomeScreen.worldMap,
						0, 0);
				g.setColor(color);
			}

			protected void sublayout(int w, int h) {
				w = Display.getWidth();
				h = Display.getHeight();
				if (w != width || h != height) {
					width = w;
					height = h;
				}
				super.sublayout(width, height);
				setExtent(width, height);
			}

			public void paint(Graphics g) {
				g.setFont(g.getFont().derive(Font.BOLD, 16));

				int boundsLeftX = 0;
				int boundsLeftY = 0;
				g.setColor(0x00FFFFFF);
				
				String[] zoneTexts = new String[HomeScreen.timeZones.size()];
				int[] boxBounds = new int[2];
				for (int i = 0; i < HomeScreen.timeZones.size(); i++) {
					Time time = TimeUtils.getTime(HomeScreen.timeZones
							.elementAt(i));
					if (HomeScreen.timeZones.elementAt(i) instanceof TZone)
						zoneTexts[i] = ((TZone) HomeScreen.timeZones.elementAt(i)).name;
					else
						zoneTexts[i] = Utils.replace((String) HomeScreen.timeZones
								.elementAt(i), "_", " ");
					if (zoneTexts[i].indexOf("/") > -1)
						zoneTexts[i] = zoneTexts[i]
								.substring(zoneTexts[i].indexOf("/") + 1);
					zoneTexts[i] += " - " + time.getTime();
					int advance = g.getFont().getAdvance(zoneTexts[i]) + 5;
					if(advance > boxBounds[0])
						boxBounds[0] = advance;
				}
				boxBounds[0] += 5;
				boxBounds[1] = HomeScreen.timeZones.size() * 20 + 5;
				g.setGlobalAlpha(100);
				g.fillRoundRect(-15, height - boxBounds[1], boxBounds[0] + 15, boxBounds[1] + 30, 15, 15);
				g.setGlobalAlpha(255);
				for (int i = 0; i < HomeScreen.timeZones.size(); i++) {
					int y = height - 5 - (i * 20);
					g.setColor(0x00666666);
					g.drawText(zoneTexts[i], 4, y,
							DrawStyle.BOTTOM);
					g.setColor(0x00FFFFFF);
					g.drawText(zoneTexts[i], 5, y + 1,
							DrawStyle.BOTTOM);
				}

				if (currentX <= boundsLeftX
						&& currentY + g.getFont().getHeight() + 10 >= boundsLeftY) {
					int distX = Math.abs(currentX - boundsLeftX);
					int distY = Math.abs(currentY + g.getFont().getHeight()
							+ 10 - boundsLeftY);
					if (distX < distY)
						movingX *= -1;
					else
						movingY *= -1;

					if (currentX <= boundsLeftX - 1
							&& currentY + g.getFont().getHeight() >= boundsLeftY + 1) {
						if (System.currentTimeMillis() - lastBounce < 200) {
							currentX = 10;
							currentY = 10;
						}
					}

					lastBounce = System.currentTimeMillis();
				}

				Time time = TimeUtils.getTime(TimeZone.getDefault().getID());
				g.setFont(g.getFont().derive(Font.BOLD, fontSize));
				int advance = g.getFont().getAdvance(time.getTimeLong());

				if (currentX <= 0 || currentX + advance >= width)
					movingX *= -1;
				if (currentY <= 0
						|| currentY + g.getFont().getHeight() >= height)
					movingY *= -1;

				currentX += movingX;
				currentY += movingY;

				g.setColor(0x00FFFFFF);
				g.drawText(time.getTimeLong(), currentX, currentY);

				if (lockInfo > System.currentTimeMillis()) {
					g.setFont(g.getFont().derive(Font.PLAIN, 14));

					advance = g.getFont().getAdvance(
							"To unlock press ALT and SPACE") + 20;
					int x = (width / 2) - (advance / 2);
					int y = 30;

					g.setColor(0x00CCCCCC);
					g.setGlobalAlpha(100);
					g.fillRoundRect(x, y, advance, 30, 15, 15);
					g.setGlobalAlpha(255);
					g.setColor(0x00666666);
					g.drawText("To unlock press ALT and SPACE", -1, y + 14,
							DrawStyle.VCENTER | DrawStyle.HCENTER, width);
					g.setColor(0x00FFFFFF);
					g.drawText("To unlock press ALT and SPACE", 0, y + 15,
							DrawStyle.VCENTER | DrawStyle.HCENTER, width);
				}
			}
		};
		super.add(internalManager);
	}

	public void makeMenu(Menu menu, int context) {
		menu.add(new MenuItem(Options.LOCK_KEYS ? "Unlock" : "Close", 110, 1) {
			public void run() {
				close();
				popped();
			}
		});
	}

	protected boolean navigationMovement(int dx, int dy, int status, int time) {
		if (!Options.LOCK_KEYS) {
			close();
			popped();
		} else
			lockInfo = System.currentTimeMillis() + 3000;
		return true;
	}

	public boolean keyChar(char key, int status, int time) {
		if (!Options.LOCK_KEYS) {
			close();
			popped();
		} else if ((status & KeypadListener.STATUS_ALT) == 1
				&& key == Characters.SPACE) {
			close();
			popped();
		} else
			lockInfo = System.currentTimeMillis() + 3000;
		return true;
	}

	public void popped() {
		showing = false;
		timer.cancel();
	}

	public void pushed() {
		showing = true;
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				if (Backlight.isEnabled())
					invalidate();
			}
		}, 1000 / 10, 1000 / 10);
	}

	public boolean isShowing() {
		return showing;
	}

	public static Screensaver getInstance() {
		if (instance == null)
			instance = new Screensaver();
		return instance;
	}

}
