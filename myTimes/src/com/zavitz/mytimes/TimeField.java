package com.zavitz.mytimes;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;

public class TimeField extends Field {

	public static final int NORMAL = 0;
	public static final int CUSTOM = 1;

	private TZone tZone;
	private String zone;
	private String lastText;
	private int type;
	private boolean hasFocus;

	public TimeField(Object o) {
		if (o instanceof String) {
			zone = (String) o;
			hasFocus = false;
			type = NORMAL;
		} else {
			tZone = (TZone) o;
			type = CUSTOM;
		}
	}

	public TimeField(String pZone) {
		zone = pZone;
		hasFocus = false;
		type = NORMAL;
	}

	public TimeField(TZone zone2) {
		tZone = zone2;
		type = CUSTOM;
	}

	public int getType() {
		return type;
	}

	protected void layout(int width, int height) {
		setExtent(getPreferredWidth(), getPreferredHeight());
	}

	public boolean isFocusable() {
		return true;
	}

	public int getPreferredWidth() {
		return Display.getWidth();
	}

	public int getPreferredHeight() {
		return 60;
	}

	public Object getObject() {
		try {
			return (zone != null) ? (Object) zone : (Object) tZone;
		} catch (Exception e) {
			return null;
		}
	}

	public String getZone() {
		return zone;
	}

	public TZone getTZone() {
		return tZone;
	}

	public void invalidate() {
		super.invalidate();
	}

	public void drawFocus(Graphics graphics, boolean flag) {
		hasFocus = true;
		paint(graphics);
		hasFocus = false;
	}

	protected void paint(Graphics graphics) {
		Time time = type == NORMAL ? TimeUtils.getTime(zone) : TimeUtils
				.getTime(tZone);

		graphics.setColor(hasFocus ? 0x000c0b42 : 0x000e0e0e);
		graphics.fillRect(0, 0, getPreferredWidth(), getPreferredHeight());

		// fill in gradient
		graphics.setColor(0x00FFFFFF);
		for (double i = getPreferredHeight(); i > 1; i--) {
			double height = getPreferredHeight();
			double alpha = 50 * (i / height);
			graphics.setGlobalAlpha((int) alpha);
			graphics.drawLine(0, (int) (height - i), getPreferredWidth(),
					(int) (height - i));
		}
		graphics.setGlobalAlpha(60);
		graphics.drawLine(0, 0, getPreferredWidth(), 0);

		graphics.setGlobalAlpha(255);
		graphics.setFont(getFont().derive(Font.BOLD, 20));
		String zoneText = type == NORMAL ? Utils.replace(zone, "_", " ")
				: tZone.name;
		if (zoneText.indexOf("/") > -1)
			zoneText = zoneText.substring(zoneText.indexOf("/") + 1);
		graphics.drawText(zoneText, 5, (getPreferredHeight() / 2) + 1,
				DrawStyle.BOTTOM);
		graphics.setColor(0x00909090);
		graphics.setFont(getFont().derive(Font.PLAIN, 14));

		String day = time.getString();
		// switch(time.getDay()) {
		// case Time.TODAY:
		// day = "Today";
		// break;
		// case Time.TOMORROW:
		// day = "Tomorrow";
		// break;
		// case Time.YESTERDAY:
		// day = "Yesterday";
		// break;
		// }
		graphics
				.drawText(day, 8, 2 + (getPreferredHeight() / 2), DrawStyle.TOP);

		int extraWidth = 0;

		if (Options.SHOW_ANALOG_CLOCK) {
			graphics.drawBitmap(getPreferredWidth() - 5
					- Images.clock.getWidth(),
					(getPreferredHeight() - Images.clock.getHeight()) / 2,
					Images.clock.getWidth(), Images.clock.getHeight(),
					Images.clock, 0, 0);

			int center[] = new int[] {
					getPreferredWidth() - 5 - Images.clock.getWidth() + 26,
					(getPreferredHeight() / 2) };

			graphics.setGlobalAlpha(190);
			// Draw hours line:
			float angle = ((360 / 12) * time.getHours())
					+ (((float) 30 / (float) 60) * time.getMinutes());
			float radius = 13;
			int point_hours[] = new int[] {
					(int) (center[0] + (radius * (float) Math.sin(Math
							.toRadians(angle)))),
					(int) (center[1] + (-1 * radius * (float) Math.cos(Math
							.toRadians(angle)))) };

			graphics.setColor(0x0);
			graphics.drawLine(center[0], center[1], point_hours[0],
					point_hours[1]);
			graphics.drawLine(center[0] - 1, center[1], point_hours[0] - 1,
					point_hours[1]);

			graphics.setGlobalAlpha(50);
			graphics.drawLine(center[0] + 1, center[1], point_hours[0] + 1,
					point_hours[1]);
			graphics.drawLine(center[0] - 2, center[1], point_hours[0] - 2,
					point_hours[1]);
			graphics.setGlobalAlpha(190);

			// Draw minutes line:
			angle = 6 * time.getMinutes();
			radius = 17;
			int point_minutes[] = new int[] {
					(int) (center[0] + (radius * (float) Math.sin(Math
							.toRadians(angle)))),
					(int) (center[1] + (-1 * radius * (float) Math.cos(Math
							.toRadians(angle)))) };
			graphics.setColor(0x0);
			graphics.drawLine(center[0], center[1], point_minutes[0],
					point_minutes[1]);
			graphics.drawLine(center[0] - 1, center[1], point_minutes[0] - 1,
					point_minutes[1]);

			graphics.setGlobalAlpha(50);
			graphics.drawLine(center[0] + 1, center[1], point_minutes[0] + 1,
					point_minutes[1]);
			graphics.drawLine(center[0] - 2, center[1], point_minutes[0] - 2,
					point_minutes[1]);
			graphics.setGlobalAlpha(255);

			// Draw circles:
			graphics.setColor(0x00181818);
			graphics.fillEllipse(center[0], center[1], center[0] + 2,
					center[1], center[0], center[1] + 2, 0, 0);
			graphics.setGlobalAlpha(30);
			graphics.fillEllipse(center[0], center[1], center[0] + 3,
					center[1], center[0], center[1] + 3, 0, 0);
			graphics.setGlobalAlpha(210);
			graphics.setColor(0x00FF0000);
			graphics.fillEllipse(center[0], center[1], center[0] + 1,
					center[1], center[0], center[1] + 1, 0, 0);

			graphics.setGlobalAlpha(190);

			// Draw seconds line:
			angle = 6 * time.getSeconds();
			radius = 20;
			int point_seconds[] = new int[] {
					(int) (center[0] + (radius * (float) Math.sin(Math
							.toRadians(angle)))),
					(int) (center[1] + (-1 * radius * (float) Math.cos(Math
							.toRadians(angle)))) };
			graphics.drawLine(center[0], center[1], point_seconds[0],
					point_seconds[1]);

			graphics.setGlobalAlpha(50);
			graphics.drawLine(center[0] + 1, center[1], point_seconds[0] + 1,
					point_seconds[1]);
			graphics.drawLine(center[0] - 1, center[1], point_seconds[0] - 1,
					point_seconds[1]);
			graphics.setGlobalAlpha(255);
			extraWidth = 5 + Images.clock.getWidth();
		}

		if (Options.SHOW_DIGITAL_CLOCK) {
			String t = time.getTime();

			Font mainFont = getFont().derive(Font.PLAIN, 32);
			int width = mainFont.getAdvance(t);
			int x = getPreferredWidth() - width - 15 - extraWidth;

			graphics.setColor(hasFocus ? 0x000c0b42 : 0x000e0e0e);
			graphics.fillRoundRect(x - 10, 5, width + 20,
					getPreferredHeight() - 10, 15, 15);

			graphics.setColor(0x00888888);
			graphics.setFont(mainFont);
			graphics.drawText(t, x + 1, getPreferredHeight() / 2,
					DrawStyle.VCENTER);
			graphics.setColor(0x00FFFFFF);
			graphics.drawText(t, x - 1, getPreferredHeight() / 2 - 1,
					DrawStyle.VCENTER);
			graphics.setGlobalAlpha(20);
			graphics.setColor(0x00FFFFFF);
			graphics.drawRoundRect(x - 10, 5, width + 20,
					getPreferredHeight() - 10, 15, 15);
			graphics.setGlobalAlpha(255);
		}

	}

	public boolean navigationClick(int status, int time) {
		return true;
	}

}
