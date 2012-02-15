package com.zavitz.mytasks.fields;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;

public class HeaderLabel extends Field {
	
	String text;
	int foreground = 0x00F2F2F2;
	
	public HeaderLabel(String s) {
		super(NON_FOCUSABLE);
		text = s;
	}
	
	public void setFont(int height) {
		setFont(getFont().derive(Font.BOLD, height));
	}
	
	public void setForeground(int i) {
		foreground = i;
		invalidate();
	}
	
	public int getPreferredHeight() {
		return getFont().getHeight() + 4;
	}
	
	public int getPreferredWidth() {
		return Display.getWidth();
	}

	protected void layout(int width, int height) {
		setExtent(getPreferredWidth(), getPreferredHeight());
	}

	protected void paint(Graphics graphics) {
		graphics.setBackgroundColor(0x00777777);
		graphics.clear();
		
		graphics.setColor(foreground);
		graphics.drawText(text, 3, getPreferredHeight() / 2, DrawStyle.VCENTER);
	}

}
