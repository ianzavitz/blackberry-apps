package com.zavitz.fml.fields;

import java.util.Vector;

import com.zavitz.fml.data.Config;
import net.rim.device.api.system.*;
import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.container.*;

public class SearchToolbar extends HorizontalFieldManager {
	
	private static Bitmap gradient;
	private ToolbarListener listener;
	private EditField editField;
	
	static {
		gradient = Bitmap.getBitmapResource("gradient.png");
	}
	
	public SearchToolbar(ToolbarListener _listener) {
		super(FOCUSABLE);
		this.listener = _listener;
		editField = new EditField() {
			public void paint(Graphics g) {
				g.setColor(Config.GRAY);
				super.paint(g);
			}
			public boolean navigationClick(int status, int time) {
				listener.toolbarPressed(getText());
				return true;
			}
			
			public boolean keyChar(char character, int status, int time) {
				if(character == Characters.ENTER) {
					listener.toolbarPressed(getText());
					return true;
				}
				return super.keyChar(character,status,time);
			}
		};
		editField.setFont(editField.getFont().derive(Font.PLAIN, Config.FONT_SIZE));
		
		add(editField);
	}
	
	
	public int getPreferredWidth() {
		return Display.getWidth();
	}
	
	public int getPreferredHeight() {
		return Config.FONT_SIZE + 20;
	}
	
	public int textWidth() {
		return getPreferredWidth() - 20;
	}
	
	public int textHeight() {
		return getPreferredHeight() - 20;
	}
	
	public void paint(Graphics g) {
		g.setFont(g.getFont().derive(Font.BOLD, Config.FONT_SIZE));
		
		// Draw Background
		g.setColor(Config.WHITE);
		g.fillRect(0,0,getPreferredWidth(),getPreferredHeight());
		g.drawBitmap(0, 0, getPreferredWidth(), getPreferredHeight() < gradient.getHeight() ? getPreferredHeight() : gradient.getHeight(), gradient, 0, 0);
		
		// Draw "Textbox"
		g.setColor(Config.WHITE);
		g.fillRoundRect(5, 5, getPreferredWidth() - 10, getPreferredHeight() - 10, 10, 10);
		g.setColor(0x0095a0b0);
		g.drawRoundRect(5, 5, getPreferredWidth() - 10, getPreferredHeight() - 10, 10, 10);
		
		// Draw Lines
		g.setColor(0x0095a0b0);
		g.drawLine(0,0,getPreferredWidth(),0);
		g.setColor(0x0095a0b0);
		g.drawLine(0,getPreferredHeight() - 1,getPreferredWidth(),getPreferredHeight() - 1);
		subpaint(g);
	}
	
	protected void sublayout(int w, int h) {
		Field edit = getField(0);
		layoutChild(edit, getPreferredWidth() - 20, getPreferredHeight() - 20);
		setPositionChild(edit, 10, 10);
		
		setExtent(getPreferredWidth(), getPreferredHeight());
	}
	
}
