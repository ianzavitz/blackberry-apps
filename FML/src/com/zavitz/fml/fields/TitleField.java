package com.zavitz.fml.fields;

import com.zavitz.fml.FMLMainScreen;
import com.zavitz.fml.data.Config;

import net.rim.device.api.system.*;
import net.rim.device.api.ui.*;

public class TitleField extends Field {
	
	private static final Bitmap title;
	
	static {
		title = Bitmap.getBitmapResource("header.png");
	}
	
	public TitleField() {
		setFont(getFont().derive(Font.BOLD, Config.FONT_SIZE + 1));
	}
	
	public int getPreferredWidth() {
		return Display.getWidth();
	}
	
	public int getPreferredHeight() {
		return 31;
	}

	protected void layout(int width, int height) {
		setExtent(getPreferredWidth(), getPreferredHeight());
	}

	protected void paint(Graphics graphics) {
		graphics.drawBitmap(0, 0, getPreferredWidth(), getPreferredHeight(), title, 0, 0);
	}

}
