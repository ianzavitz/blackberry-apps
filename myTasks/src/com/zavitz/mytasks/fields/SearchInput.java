package com.zavitz.mytasks.fields;

import net.rim.device.api.system.Characters;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.container.HorizontalFieldManager;

public class SearchInput extends HorizontalFieldManager {

	private EditField editField;
	SearchCallback callback;
	
	public SearchInput(SearchCallback _callback) {
		super(FOCUSABLE);
		callback = _callback;
		
		editField = new EditField() {
			public void paint(Graphics g) {
				g.setColor(0x00444444);
				super.paint(g);
			}
			public boolean navigationClick(int status, int time) {
				callback.searchRequest(getText());
				return true;
			}
			
			public boolean keyChar(char character, int status, int time) {
				if(character == Characters.ENTER) {
					callback.searchRequest(getText());
					return true;
				}
				return super.keyChar(character,status,time);
			}
		};
		
		add(editField);
	}
	
	
	public int getPreferredWidth() {
		return Display.getWidth();
	}
	
	public int getPreferredHeight() {
		return getFont().getHeight() + 20;
	}
	
	public int textWidth() {
		return getPreferredWidth() - 20;
	}
	
	public int textHeight() {
		return getPreferredHeight() - 20;
	}
	
	public void paint(Graphics g) {		
		// Draw Background
		g.setColor(0x00CCCCCC);
		g.fillRect(0,0,getPreferredWidth(),getPreferredHeight());
		
		// Draw "Textbox"
		g.setColor(0x00FFFFFF);
		g.fillRoundRect(5, 5, getPreferredWidth() - 10, getPreferredHeight() - 10, 10, 10);
		g.setColor(0x0095a0b0);
		g.drawRoundRect(5, 5, getPreferredWidth() - 10, getPreferredHeight() - 10, 10, 10);
		
		// Draw Lines
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
	
	public interface SearchCallback {
		
		public void searchRequest(String query);
		
	}
	
}
