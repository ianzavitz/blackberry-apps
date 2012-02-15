package com.zavitz.mytimes;

import java.util.Random;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;

public class TitleField extends Field {

	protected void layout(int width, int height) {
		setExtent(getPreferredWidth(),getPreferredHeight());
	}
	
	public boolean isFocusable() {
		return false;
	}
	
	public int getPreferredWidth() {
		return Display.getWidth();
	}
	
	public int getPreferredHeight() {
		return 21;
	}

	protected void paint(Graphics graphics) {
		graphics.setColor(0x000e0e0e);
		graphics.fillRect(0, 0, getPreferredWidth(), getPreferredHeight());
		
		Random random = new Random(System.currentTimeMillis());
		int randHeight = -1 * random.nextInt(HomeScreen.worldMap.getHeight() - 60);
		
		graphics.drawBitmap(0, randHeight, HomeScreen.worldMap.getWidth(), HomeScreen.worldMap.getHeight(), HomeScreen.worldMap, 0, 0);
		
		graphics.setFont(getFont().derive(Font.BOLD,18));
		graphics.setColor(0x000e0e0e);
		int w = graphics.drawText("myTimes", 5, (getPreferredHeight() / 2), DrawStyle.VCENTER);
		graphics.setColor(0x00FFFFFF);
		graphics.drawText("myTimes", 4, (getPreferredHeight() / 2) - 1, DrawStyle.VCENTER);
		
		graphics.setFont(getFont().derive(Font.PLAIN,13));
		graphics.setColor(0x00FFFFFF);
		graphics.drawText(" A World Clock", 5 + w, getPreferredHeight() / 2, DrawStyle.VCENTER);
		
		for(double i = getPreferredHeight(); i > 1; i--) {
			double height = getPreferredHeight();
			double alpha = 30 * (i / height);
			graphics.setGlobalAlpha((int)alpha);
			graphics.drawLine(0, (int)(height - i), getPreferredWidth(), (int)(height - i));
		}
		
		graphics.setGlobalAlpha(255);
		graphics.setColor(0x0);
		graphics.drawLine(0,getPreferredHeight() - 1,getPreferredWidth(),getPreferredHeight() - 1);
	}

}
