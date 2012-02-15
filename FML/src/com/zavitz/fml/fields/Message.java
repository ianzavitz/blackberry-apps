package com.zavitz.fml.fields;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;

import com.zavitz.fml.data.Config;

public class Message extends Field {

	private String text = "";

	public Message(String t) {
		super(NON_FOCUSABLE);
		setFont(getFont().derive(Font.BOLD, Config.FONT_SIZE));
		text = t;
	}

	public int getPreferredHeight() {
		return 20 + 10 + Config.FONT_SIZE;
	}

	public int getPreferredWidth() {
		return Display.getWidth();
	}

	protected void layout(int width, int height) {
		setExtent(getPreferredWidth(), getPreferredHeight());
	}

	private boolean focus = false;

	public void drawFocus(Graphics g, boolean on) {
		focus = true;
		paint(g);
		focus = false;
	}

	protected void paint(Graphics g) {
		g.setColor(Config.WHITE);
		g.fillRoundRect(5, 5, getPreferredWidth() - 10,
				getContentHeight() - 10, 15, 15);
		g.setColor(Config.DEFAULT_BLUE);
		if (focus)
			g.drawRoundRect(5, 5, getPreferredWidth() - 10,
					getContentHeight() - 10, 15, 15);
		else
			g.setColor(Config.GRAY);
		g.setFont(getFont());
		g.drawText(text, 0, getPreferredHeight() / 2, DrawStyle.HCENTER
				| DrawStyle.VCENTER, getPreferredWidth());
	}
	
}

