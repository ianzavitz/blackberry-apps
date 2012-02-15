package com.zavitz.mybudget.screens;

import com.zavitz.mybudget.UiApp;
import com.zavitz.mybudget.fields.BudgetField;
import com.zavitz.mybudget.fields.TitleField;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;

public class PasswordProtectScreen extends MainScreen {

	private VerticalFieldManager internalManager, manager;
	public static Bitmap bg;
	public static TitleField title;

	static {
		bg = Bitmap.getBitmapResource("main_bg.png");
	}

	public PasswordProtectScreen() {
		super(Manager.NO_VERTICAL_SCROLL | Manager.NO_VERTICAL_SCROLLBAR);
		internalManager = new VerticalFieldManager(Manager.NO_VERTICAL_SCROLL
				| Manager.NO_VERTICAL_SCROLLBAR) {
			public void paintBackground(Graphics g) {
				g.setBackgroundColor(0x00204f6a);
				g.clear();

				XYRect rect = new XYRect();
				rect.height = Display.getHeight() < bg.getHeight() ? Display
						.getHeight() : bg.getHeight();
				rect.width = Display.getWidth() < bg.getWidth() ? Display
						.getWidth() : bg.getWidth();
				// for the x position, check if the display width is greater
				// than the image, if so you need to put it it at half of the
				// distance
				rect.x = (Display.getWidth() - bg.getWidth()) / 2;
				rect.y = Display.getHeight() - bg.getHeight();

				g.drawBitmap(rect, bg, 0, 0);
			}

			protected void sublayout(int width, int height) {
				super.sublayout(Display.getWidth(), Display.getHeight());
				setExtent(Display.getWidth(), Display.getHeight());
			}
		};
		super.add(internalManager);
		manager = new VerticalFieldManager(Manager.VERTICAL_SCROLL
				| Manager.VERTICAL_SCROLLBAR);
		title = new TitleField();
		internalManager.add(title);
		internalManager.add(manager);

		add(new LabelField("PasswordProtectScreen"));
	}

	public void add(Field field) {
		manager.add(field);
	}
	
}
