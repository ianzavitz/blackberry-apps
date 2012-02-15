package com.zavitz.fml.fields;

import java.util.Vector;

import com.zavitz.fml.data.Config;
import net.rim.device.api.system.*;
import net.rim.device.api.ui.*;

public class Toolbar extends Field {
	
	private static Bitmap gradient, sgradient;
	private ToolbarListener listener;
	private Vector buttons;
	private int index = 0;
	private int returnTo = 0;
	
	static {
		gradient = Bitmap.getBitmapResource("gradient.png");
		sgradient = Bitmap.getBitmapResource("sgradient.png");
	}
	
	public Toolbar(ToolbarListener listener) {
		super(FOCUSABLE);
		this.listener = listener;
		
		buttons = new Vector();
	}
	
	public void addOption(String s) {
		buttons.addElement(s);
		invalidate();
	}
	
	public void clear() {
		buttons.removeAllElements();
		invalidate();
	}
	
	public int getPreferredWidth() {
		return Display.getWidth();
	}
	
	public int getPreferredHeight() {
		return Config.FONT_SIZE + 10;
	}
	
	public void onFocus(final int direction) {
		index = returnTo;
		invalidate();
	}
	
	public void paint(Graphics g) {
		g.setFont(g.getFont().derive(Font.BOLD, Config.FONT_SIZE));
		
		// Draw Background
		g.setColor(Config.WHITE);
		g.fillRect(0,0,getPreferredWidth(),getPreferredHeight());
		g.drawBitmap(0, 0, getPreferredWidth(), getPreferredHeight() < gradient.getHeight() ? getPreferredHeight() : gradient.getHeight(), gradient, 0, 0);
		
		// Draw "Buttons"
		g.setColor(Config.GRAY);
		int total = 0;
		for(int i = 0; i < buttons.size(); i++) {
			String s = (String) buttons.elementAt(i);
			total += g.getFont().getAdvance(s) + 16;
		}
		int start = (getPreferredWidth() - total) / 2;
		for(int i = 0; i < buttons.size(); i++) {
			String s = (String) buttons.elementAt(i);
			int width = g.getFont().getAdvance(s) + 16;
			if(index == i) {
				g.setColor(0x00787878);
				g.fillRect(start, 0, width, getPreferredHeight());
				g.drawBitmap(start, 0, width, getPreferredHeight() < sgradient.getHeight() ? getPreferredHeight() : sgradient.getHeight(), sgradient, 0, 0);
			}
			g.setColor(0x0095a0b0);
			g.drawLine(start, 0, start, getPreferredHeight());
			g.drawLine(start + width, 0, start + width, getPreferredHeight());
			
			// Draw bottom text layer
			if(index != i)
				g.setColor(Config.WHITE);
			else
				g.setColor(Config.GRAY);
			g.drawText(s, start - 1, (getPreferredHeight() / 2) - 1, DrawStyle.HCENTER | DrawStyle.VCENTER, width);	
			
			// Draw top text layer
			if(index == i)
				g.setColor(Config.WHITE);
			else
				g.setColor(Config.GRAY);
			g.drawText(s, start, getPreferredHeight() / 2, DrawStyle.HCENTER | DrawStyle.VCENTER, width);			
			start += width;
		}
		
		// Draw Lines
		g.setColor(0x0095a0b0);
		g.drawLine(0,0,getPreferredWidth(),0);
		g.setColor(0x0095a0b0);
		g.drawLine(0,getPreferredHeight() - 1,getPreferredWidth(),getPreferredHeight() - 1);
	}

	protected void layout(int width, int height) {
		setExtent(getPreferredWidth(), getPreferredHeight());
	}
	
	public boolean navigationMovement(int dx, int dy, int status, int time) {
		if(dx != 0) {
			index += dx;
			if(index >= buttons.size())
				index = buttons.size() - 1;
			else if(index < 0)
				index = 0;
			invalidate();
			return true;
		}
		if(dy > 0) {
			returnTo = index;
			index = -1;
			invalidate();
			return super.navigationMovement(dx,dy,status,time);
		}
		return true;
	}
	
	public boolean navigationClick(int status, int time) {
		if(index >= 0 && index < buttons.size())
			listener.toolbarPressed((String) buttons.elementAt(index));
		return true;
	}
	
	public boolean keyChar(char character, int status, int time) {
		if(character == Characters.ENTER) {
			if(index >= 0 && index < buttons.size())
				listener.toolbarPressed((String) buttons.elementAt(index));
			return true;
		}
		return super.keyChar(character,status,time);
	}
	
}
