package com.zavitz.mytasks.fields;

import com.zavitz.mytasks.ProjectScreen;
import com.zavitz.mytasks.elements.Project;
import com.zavitz.mytasks.elements.Task;
import net.rim.device.api.system.Characters;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.UiApplication;

public class ProjectField extends Field {

	final Font nameFont = getFont().derive(Font.PLAIN, getFont().getHeight());
	final Font countFont = getFont().derive(Font.BOLD, getFont().getHeight() - 2);
	Project project;
	
	public ProjectField(Project project) {
		super(FOCUSABLE);
		this.project = project;
		setFont(nameFont);
	}
	
	public Project getProject() {
		return project;
	}

	public int getPreferredWidth() {
		return Display.getWidth();
	}
	
	public int getPreferredHeight() {
		int h = getFont().getHeight() + 10; // 4 px for 
		return h;
	}
	
	protected void layout(int width, int height) {
		setExtent(getPreferredWidth(), getPreferredHeight());
	}
	
	public void drawFocus(Graphics graphics, boolean on) {
		graphics.setGlobalAlpha(19);
		graphics.setBackgroundColor(0);
		graphics.clear();
		graphics.setGlobalAlpha(255);
		paint(graphics);
	}
	
	protected void paint(Graphics graphics) {
		int completed = 0;
		for(int i = 0; i < project.getTasks().length; i++)
			if(project.getTask(i).getStatus() == Task.COMPLETED)
				completed++;
		 
		String count = String.valueOf(completed + "/" + project.getTasks().length);
		graphics.setFont(countFont);
		
		int countAdvance = graphics.getFont().getAdvance(count) + 10;
		graphics.setColor(0x00777777);
		graphics.fillRoundRect(getPreferredWidth() - countAdvance - 9, 3, countAdvance + 6, getPreferredHeight() - 6, 6, 6);
		graphics.setColor(0x00F2F2F2);
		graphics.drawText(count, getPreferredWidth() - 6 - countAdvance, getPreferredHeight() / 2, DrawStyle.VCENTER | DrawStyle.HCENTER, countAdvance);

		graphics.setFont(nameFont);
		graphics.setColor(0x00555555);
		graphics.drawText(project.getName(), 3, getPreferredHeight() / 2, DrawStyle.VCENTER);
		
		graphics.setColor(0x00BCBCBC);
		graphics.drawLine(0, getPreferredHeight() - 1, getPreferredWidth(), getPreferredHeight() - 1);
	}
	
	public void open() {
		UiApplication.getUiApplication().pushScreen(new ProjectScreen(project));
	}

	public boolean keyChar(char key, int status, int time) {
		if(key == Characters.ENTER) {
			open();
			return true;
		}
		return super.keyChar(key, status, time);
	}
	
	public boolean navigationClick(int status, int time) {
		open();
		return true;
	}

}
