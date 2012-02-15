package com.zavitz.mytasks.fields;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.FontFamily;
import net.rim.device.api.ui.Graphics;

public class TitleField extends Field {
	
	static Bitmap mt_27, mt_36, image;
	final int LARGER_HEIGHT = 51;
	final int SMALLER_HEIGHT = 38;
	String text = "";
	
	static {
		mt_27 = Bitmap.getBitmapResource("mt-27h.png");
		mt_36 = Bitmap.getBitmapResource("mt-36h.png");
	}
	
	public TitleField(String s) { 
		text = s.toUpperCase();
		FontFamily family = null;
		try {
			family = FontFamily.forName("BBCondensed");//"TBBAlpha Sans Condensed");
		} catch (ClassNotFoundException e) {
			try {
				family = FontFamily.forName("BBCondensed");
			} catch (ClassNotFoundException e1) { }
		}
		if (family != null) {
			setFont(family.getFont(Font.PLAIN, getPreferredHeight() == LARGER_HEIGHT ? 14 : 10));
		}
	}
	
	public int getPreferredHeight() {
		return Display.getVerticalResolution() > 7500 ? LARGER_HEIGHT : SMALLER_HEIGHT;
	}

	protected void layout(int width, int height) {
		setExtent(Display.getWidth(), getPreferredHeight());
	}

	protected void paint(Graphics graphics) {
		graphics.setBackgroundColor(0);
		graphics.clear();
		
		image = getPreferredHeight() == SMALLER_HEIGHT ? mt_27 : mt_36;
		graphics.drawBitmap(0, 0, Display.getWidth(), image.getHeight(), image, 0, 0);
		
		graphics.setColor(0x00444444);
		graphics.fillRect(0, image.getHeight() + 1, Display.getWidth(), getPreferredHeight() - image.getHeight() - 2);
		
		graphics.setColor(0x00D4D4D4);
		graphics.drawText(text, 2, image.getHeight() + 1, DrawStyle.TOP | DrawStyle.ELLIPSIS | DrawStyle.TRUNCATE_BEGINNING, Display.getWidth() - 3);
	}

}
