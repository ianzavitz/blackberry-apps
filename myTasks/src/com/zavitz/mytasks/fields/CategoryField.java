package com.zavitz.mytasks.fields;

import com.zavitz.mytasks.*;
import com.zavitz.mytasks.functions.CalUtils;
import com.zavitz.mytasks.functions.TaskUtils;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Characters;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Status;

public class CategoryField extends Field {

	public static final int ALL = 0;
	public static final int TODAY = 1;
	public static final int TOMORROW = 2;
	public static final int SOON = 3;
	public static final int OVERDUE = 4;
	public static final int NO_DUE_DATE = 5;
	public static final int COMPLETED = 6;
	public static final int SEARCH = 7;

	public static final String[] DEFAULT_STYLES = new String[] { "All Tasks",
			"Today", "Tomorrow", "Soon", "Overdue", "No Due Date", "Completed",
			"Search" };

	static Bitmap[] ICONS = new Bitmap[8];

	static {
		ICONS[ALL] = Bitmap.getBitmapResource("all.png");
		ICONS[TODAY] = Bitmap.getBitmapResource("today.png");
		ICONS[TOMORROW] = Bitmap.getBitmapResource("tomorrow.png");
		ICONS[SOON] = Bitmap.getBitmapResource("soon.png");
		ICONS[OVERDUE] = Bitmap.getBitmapResource("overdue.png");
		ICONS[NO_DUE_DATE] = Bitmap.getBitmapResource("no_due_date.png");
		ICONS[COMPLETED] = Bitmap.getBitmapResource("completed.png");
		ICONS[SEARCH] = Bitmap.getBitmapResource("search.png");
	}

	private int style;

	public CategoryField(int style) {
		super(FOCUSABLE);
		this.style = style;
	}

	public int getPreferredWidth() {
		return Display.getWidth();
	}

	public int getPreferredHeight() {
		int h = getFont().getHeight() + 10; // 4 px for

		if (h < 6 + ICONS[style].getHeight())
			return 6 + ICONS[style].getHeight();
		return h;
	}

	protected void layout(int width, int height) {
		setExtent(getPreferredWidth(), getPreferredHeight());
	}

	public void drawFocus(Graphics graphics, boolean on) {
		graphics.setGlobalAlpha(19);
		graphics.setBackgroundColor(0);
		graphics.clear();
		graphics.setGlobalAlpha(255);
		paint(graphics);
	}

	protected void paint(Graphics graphics) {
		if (style < SEARCH) {
			String count = "";
			Font f = getFont();
			graphics.setFont(f.derive(Font.BOLD));
			if (style != COMPLETED)
				count = String.valueOf(TaskUtils.getTaskCount(CalUtils
						.getFrom(style), CalUtils.getTo(style)));
			else
				count = String.valueOf(TaskUtils.getCompletedTaskCount());

			int countAdvance = getFont().getAdvance(count) + 16;
			graphics.setColor(0x00777777);
			graphics.fillRoundRect(getPreferredWidth() - countAdvance - 9, 3,
					countAdvance + 6, getPreferredHeight() - 6, 6, 6);
			graphics.setColor(0x00F2F2F2);
			graphics.drawText(count, getPreferredWidth() - 6 - countAdvance,
					getPreferredHeight() / 2, DrawStyle.VCENTER
							| DrawStyle.HCENTER, countAdvance);
			graphics.setFont(f);
		}
		graphics.setColor(0x00555555);
		graphics.drawBitmap(3,
				(getPreferredHeight() - ICONS[style].getHeight()) / 2,
				ICONS[style].getWidth(), ICONS[style].getHeight(),
				ICONS[style], 0, 0);
		graphics.drawText(DEFAULT_STYLES[style], 8 + ICONS[style].getWidth(),
				getPreferredHeight() / 2, DrawStyle.VCENTER);

		if (style < SEARCH) {
			graphics.setColor(0x00BCBCBC);
			graphics.drawLine(0, getPreferredHeight() - 1, getPreferredWidth(),
					getPreferredHeight() - 1);
		}
	}

	public void open() {
		if (style < SEARCH) {
			int count = 0;
			if (style != COMPLETED)
				count = TaskUtils.getTaskCount(CalUtils.getFrom(style),
						CalUtils.getTo(style));
			else
				count = TaskUtils.getCompletedTaskCount();
			if (count > 0)
				UiApplication.getUiApplication().pushScreen(
						new CategoryScreen(style));
			else
				Status.show("There are no tasks in this category");
		} else
			UiApplication.getUiApplication().pushScreen(new SearchScreen());
	}

	public boolean keyChar(char key, int status, int time) {
		if (key == Characters.ENTER) {
			open();
			return true;
		}
		return super.keyChar(key, status, time);
	}

	public boolean navigationClick(int status, int time) {
		open();
		return true;
	}

}
