package com.zavitz.mytasks.fields;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;

public class RoundedEdge extends Field {
	
	static Bitmap circle;
	
	static {
		circle = Bitmap.getBitmapResource("circle.png");
	}

	public int getPreferredWidth() {
		return Display.getWidth();
	}
	
	public int getPreferredHeight() {
		return 80;
	}
	
	protected void layout(int width, int height) {
		setExtent(getPreferredWidth(), getPreferredHeight());
	}

	protected void paint(Graphics graphics) {
		int padding = 5;
		
		/* tl */ graphics.drawBitmap(padding, padding, 15, 15, circle, 0, 0);
		/* bl */ graphics.drawBitmap(padding, getPreferredHeight() - 15 - padding, 15, 15, circle, 0, 15);
		/* tr */ graphics.drawBitmap(getPreferredWidth() - (circle.getWidth() / 2) - padding, padding, (circle.getWidth() / 2), (circle.getWidth() / 2), circle, (circle.getWidth() / 2), 0);
		/* br */ graphics.drawBitmap(getPreferredWidth() - (circle.getWidth() / 2) - padding, getPreferredHeight() - (circle.getWidth() / 2) - padding, (circle.getWidth() / 2), (circle.getWidth() / 2), circle, (circle.getWidth() / 2), (circle.getWidth() / 2));
		graphics.setColor(0x00CCCCCC);
		/* top */ graphics.fillRect((circle.getWidth() / 2) + padding, padding, getPreferredWidth() - (2 * (circle.getWidth() / 2)) - (2 * padding), (circle.getWidth() / 2));
		/* bottom */ graphics.fillRect((circle.getWidth() / 2) + padding, getPreferredHeight() - (circle.getWidth() / 2) - padding, getPreferredWidth() - 30 - (2 * padding), (circle.getWidth() / 2));
		/* middle */ graphics.fillRect(padding, (circle.getWidth() / 2) + padding, getPreferredWidth() - (2 * padding), getPreferredHeight() - (2 * (circle.getWidth() / 2)) - (2 * padding));
		graphics.setColor(0x0);
		graphics.drawText("Rounded Edges", 0, getPreferredHeight() / 2, DrawStyle.VCENTER | DrawStyle.HCENTER, getPreferredWidth());
	}

}
