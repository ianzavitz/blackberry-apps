package com.zavitz.mytasks.fields;

import com.zavitz.mytasks.ProjectScreen;
import com.zavitz.mytasks.MTScreen;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Characters;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.*;

public class EmptyField extends Field {

	public static final int PROJECT = 0;
	public static final int TASK = 1;

	static Bitmap add;
	Screen callback;

	int type;

	static {
		add = Bitmap.getBitmapResource("add.png");
	}

	public EmptyField(Screen screen, int type) {
		super(FOCUSABLE);
		callback = screen;
		this.type = type;
	}

	public int getPreferredHeight() {
		int h = getFont().getHeight() + 4;
		if (h < add.getHeight() + 4)
			return add.getHeight() + 4;
		return h;
	}

	public int getPreferredWidth() {
		return Display.getWidth();
	}

	protected void layout(int width, int height) {
		setExtent(getPreferredWidth(), getPreferredHeight());
	}

	protected void drawFocus(Graphics graphics, boolean on) {
		graphics.setGlobalAlpha(19);
		graphics.setBackgroundColor(0);
		graphics.clear();
		graphics.setGlobalAlpha(255);
		paint(graphics);
	}

	protected void paint(Graphics graphics) {
		graphics.setColor(0x00555555);
		graphics.drawBitmap(3, (getPreferredHeight() - add.getHeight()) / 2,
				add.getWidth(), add.getHeight(), add, 0, 0);
		graphics
				.drawText("Add New " + (type == PROJECT ? "Project" : "Task"),
						8 + add.getWidth(), getPreferredHeight() / 2,
						DrawStyle.VCENTER);
	}

	public void open() {
		switch(type) {
		case PROJECT:
			if(callback instanceof MTScreen)
				((MTScreen) callback).createProject();
			else
				((ProjectScreen) callback).createProject();
			break;
		case TASK:
			((ProjectScreen) callback).createTask();
			break;
		}
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