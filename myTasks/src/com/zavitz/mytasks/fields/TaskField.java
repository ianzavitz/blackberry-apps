package com.zavitz.mytasks.fields;

import com.zavitz.mytasks.TaskScreen;
import com.zavitz.mytasks.elements.Task;

import net.rim.device.api.i18n.DateFormat;
import net.rim.device.api.i18n.SimpleDateFormat;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Characters;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.UiApplication;

public class TaskField extends Field {

	Task task;
	final Font nameFont = getFont()
			.derive(Font.BOLD, getFont().getHeight() - 1);
	final Font subTextFont = getFont().derive(Font.PLAIN,
			getFont().getHeight() - 4);
	String subText = "";

	static Bitmap arrow, checkbox, note, low_priority, high_priority,
			completed, in_progress, deferred, waiting, notes;

	static {
		checkbox = Bitmap.getBitmapResource("check.png");
		arrow = Bitmap.getBitmapResource("arrow.png");
		note = Bitmap.getBitmapResource("note.png");
		low_priority = Bitmap.getBitmapResource("low_priority.png");
		high_priority = Bitmap.getBitmapResource("high_priority.png");
		completed = Bitmap.getBitmapResource("completed.png");
		in_progress = Bitmap.getBitmapResource("progress.png");
		deferred = Bitmap.getBitmapResource("deferred.png");
		notes = Bitmap.getBitmapResource("note.png");
		waiting = Bitmap.getBitmapResource("waiting.png");
	}

	public TaskField(Task task) {
		super(FOCUSABLE);
		this.task = task;

		if (task.getDateDue() > 0)
			subText = new SimpleDateFormat(DateFormat.DATETIME_DEFAULT)
					.formatLocal(task.getDateDue());
		else
			subText = "No scheduled due date";
	}

	public void onUnfocus() {
		if (subText.equals(dateText()))
			return;
		subText = dateText();
		invalidate();
	}

	public void alternate() {
		if (subText.equals(descriptionText()))
			setSubText(dateText());
		else if (!descriptionText().equals("This task has no description"))
			setSubText(descriptionText());
	}

	private String descriptionText() {
		return task.getDescription().length() > 0 ? task.getDescription()
				: "This task has no description";
	}

	private String dateText() {
		if (task.getDateDue() > 0)
			return new SimpleDateFormat(DateFormat.DATETIME_DEFAULT)
					.formatLocal(task.getDateDue());
		else
			return "No scheduled due date";
	}

	public Task getTask() {
		return task;
	}

	public int getPreferredWidth() {
		return Display.getWidth();
	}

	public int getPreferredHeight() {
		int height = 6; // 6 is the space on both sides
		height += nameFont.getHeight() + 2; // 2 for space below
		height += subTextFont.getHeight();
		return height;
	}

	protected void layout(int width, int height) {
		setExtent(getPreferredWidth(), getPreferredHeight());
	}

	public void setSubText(String s) {
		subText = s;
		invalidate();
	}

	boolean dFocus = false;

	public void drawFocus(Graphics graphics, boolean on) {
		dFocus = true;
		paint(graphics);
		dFocus = false;
	}

	protected void paint(Graphics graphics) {
		graphics.setBackgroundColor(0x00FFFFFF);
		graphics.clear();
		if (dFocus) {
			graphics.setBackgroundColor(task.getColor());
			graphics.clear();
			if (task.getColor() != 0x00FFFFFF) {
				graphics.setGlobalAlpha(150);
				graphics.setBackgroundColor(0x00FFFFFF);
				graphics.clear();
				graphics.setGlobalAlpha(255);
			} else {
				graphics.setGlobalAlpha(19);
				graphics.setBackgroundColor(0);
				graphics.clear();
				graphics.setGlobalAlpha(255);
			}
		} else {
			graphics.setBackgroundColor(task.getColor());
			graphics.clear();//0, 0, getPreferredWidth() - 18, getPreferredHeight());
			graphics.setGlobalAlpha(200);
			graphics.setBackgroundColor(0x00FFFFFF);
			graphics.clear();
			graphics.setGlobalAlpha(255);
		}

		graphics.drawBitmap(3,
				(getPreferredHeight() - checkbox.getHeight()) / 2, checkbox
						.getWidth(), checkbox.getHeight(), checkbox, 0, 0);
		Bitmap statusIcon = null;
		switch (task.getStatus()) {
		case Task.COMPLETED:
			statusIcon = completed;
			break;
		case Task.IN_PROGRESS:
			statusIcon = in_progress;
			break;
		case Task.WAITING:
			statusIcon = waiting;
			break;
		case Task.DEFERRED:
			statusIcon = deferred;
			break;
		}
		if (statusIcon != null)
			graphics.drawBitmap(7, ((getPreferredHeight() - checkbox
					.getHeight()) / 2) + 4, 16, 16, statusIcon, 0, 0);

		graphics.setFont(nameFont);
		graphics.setColor(0x00555555);
		int l_offset = 0;
		int r_offset = 0;
		if (task.getPriority() != Task.NORMAL_PRIORITY) {
			int x = 6 + checkbox.getWidth();
			int y = 3 + (getFont().getHeight() - 16) / 2;
			graphics.drawBitmap(x, y, 16, 16,
					task.getPriority() == Task.LOW_PRIORITY ? low_priority
							: high_priority, 0, 0);
			l_offset += 19;
		}
		if (task.getNotes().length > 0) {
			graphics.drawBitmap(getPreferredWidth() - (5 + arrow.getWidth()) - 5 - notes.getWidth(), (getPreferredHeight() - notes.getHeight()) / 2, 16, 16, notes, 0, 0);
			r_offset += 21;
		}
		graphics.drawText(task.getName(), 6 + checkbox.getHeight() + l_offset, 3,
				DrawStyle.TOP | DrawStyle.ELLIPSIS, getPreferredWidth() - (6 + checkbox.getHeight() + l_offset) - (10 + arrow.getWidth() + r_offset));
		graphics.setFont(subTextFont);
		graphics.setColor(0x00777777);
		graphics.drawText(subText, 8 + checkbox.getHeight(), 3 + nameFont
				.getHeight(), DrawStyle.TOP | DrawStyle.ELLIPSIS,
				getPreferredWidth() - (8 + checkbox.getHeight())
						- (10 + arrow.getWidth() + r_offset));
		graphics.drawBitmap(getPreferredWidth() - 5 - arrow.getWidth(),
				(getPreferredHeight() - arrow.getHeight()) / 2, arrow
						.getWidth(), arrow.getHeight(), arrow, 0, 0);
		graphics.setColor(0x00BCBCBC);
		graphics.drawLine(0, getPreferredHeight() - 1, getPreferredWidth(),
				getPreferredHeight() - 1);
	}

	public void invalidate() {
		super.invalidate();
	}

	public void open() {
		UiApplication.getUiApplication().pushScreen(new TaskScreen(task));
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
