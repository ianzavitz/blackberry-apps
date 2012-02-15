package com.zavitz.mytasks;

import net.rim.blackberry.api.browser.Browser;
import net.rim.blackberry.api.browser.BrowserSession;
import net.rim.device.api.system.*;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.ActiveRichTextField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.*;

public class AboutScreen extends PopupScreen {

	public AboutScreen() {
		super(new VerticalFieldManager(VERTICAL_SCROLL | VERTICAL_SCROLLBAR));
		RichTextField name = new RichTextField("myTasks");
		name.setFont(getFont().derive(Font.BOLD));
		add(name);
		add(new RichTextField("Version: 2.0"));
		add(new RichTextField("Developed by: Greg Zavitz"));
		add(new RichTextField("myTasks is an application developed to keep people on task and help them get things done."));
//		RichTextField link = new RichTextField("Icons by famfamfam.com", HIGHLIGHT_FOCUS | FOCUSABLE) {
//			public void paint(Graphics g) {
//				g.setColor(0x000000FF);
//				super.paint(g);
//			}
//			
//			public boolean navigationClick(int status, int time) {
//				BrowserSession site = Browser.getDefaultSession();
//				site.displayPage("http://www.famfamfam.com/lab/icons/silk/");
//				return true;
//			}
//			
//			public boolean keyChar(char character, int status, int time) {
//				if(character == Characters.ENTER)
//					return navigationClick(status,time);
//				return super.keyChar(character,status,time);
//			}
//		};
//		link.setFont(link.getFont().derive(Font.DOTTED_UNDERLINED));
		ActiveRichTextField link = new ActiveRichTextField("Icons by http://famfamfam.com");
		add(link);
	}
	
	public boolean keyChar(char c, int status, int time) {
		if(c == Characters.ESCAPE) {
			close();
			return true;
		}
		return super.keyChar(c, status, time);
	}
	
}
