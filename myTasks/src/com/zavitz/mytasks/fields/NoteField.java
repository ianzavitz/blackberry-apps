package com.zavitz.mytasks.fields;

import java.util.Calendar;
import com.zavitz.mytasks.*;
import net.rim.device.api.i18n.DateFormat;
import net.rim.device.api.i18n.SimpleDateFormat;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Characters;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.UiApplication;

public class NoteField extends Field {
	
	public String note;
	
	static Bitmap voiceIcon, textIcon;
	Bitmap icon;	
	
	static {
		voiceIcon = Bitmap.getBitmapResource("voice.png");
		textIcon = Bitmap.getBitmapResource("note.png");
	}
	
	public NoteField(String note) {
		super(FOCUSABLE);
		this.note = note;
		icon = note.startsWith("VN:") ? voiceIcon : textIcon;
	}	
	
	public int getPreferredWidth() {
		return Display.getWidth();
	}

	public int getPreferredHeight() {
		int h = getFont().getHeight() + 4; // 4 px for

		if (h < 4 + icon.getHeight())
			return 4 + icon.getHeight();
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

	public void paint(Graphics graphics) {
		graphics.setColor(0x00777777);

		Bitmap icon = note.startsWith("VN:") ? voiceIcon : textIcon;
		graphics.drawBitmap(3, (getPreferredHeight() - icon.getHeight()) / 2, 16,
				16, icon, 0, 0);

		graphics.drawText(note.startsWith("VN:") ? parseName() : note,
				8 + icon.getWidth(), (getPreferredHeight() / 2),
				DrawStyle.VCENTER);
		
		graphics.setColor(0x00BCBCBC);
		graphics.drawLine(0, getPreferredHeight() - 1, getPreferredWidth(),
				getPreferredHeight() - 1);
	}
	
	private String parseName() {
		String name = note.substring(note.lastIndexOf('/') + 1,note.length() - 4);
		int month = Integer.parseInt(name.substring(0, 2));
		int day = Integer.parseInt(name.substring(2,4));
		int year = Integer.parseInt(name.substring(4,8));
		int hour = Integer.parseInt(name.substring(8,10));
		int minute = Integer.parseInt(name.substring(10,12));
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MONTH, month - 1);
		calendar.set(Calendar.DAY_OF_MONTH, day);
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND,0);
		
		return new SimpleDateFormat(DateFormat.DATETIME_DEFAULT).formatLocal(calendar.getTime().getTime());
	}
	
	public void invalidate() {
		super.invalidate();
	}
	
	public void open() {
		UiApplication.getUiApplication().pushScreen(new NoteScreen(note));
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
