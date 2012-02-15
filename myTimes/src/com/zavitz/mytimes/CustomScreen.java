package com.zavitz.mytimes;

import net.rim.device.api.system.Characters;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.component.AutoTextEditField;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.NumericChoiceField;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;

public class CustomScreen extends PopupScreen {
	
	AutoTextEditField name;
	ObjectChoiceField offsetHr;
	ObjectChoiceField offsetMin;
	HomeScreen screen;

	public CustomScreen(HomeScreen _screen) {
		super(new VerticalFieldManager(VERTICAL_SCROLL | VERTICAL_SCROLLBAR));
		screen = _screen;
		
		LabelField header = new LabelField("Custom Time Zone");
		header.setFont(getFont().derive(Font.BOLD));
		
		name = new AutoTextEditField("Name: ","");
		String hourChoices[] = new String[26];
		for(int i = -12; i < 14; i++)
			hourChoices[i + 12] = "" + i;
		
		offsetHr = new ObjectChoiceField("GMT Offset (Hours):", hourChoices, hourChoices[12]);
		String minuteChoices[] = new String[60];
		for(int i = 0; i < 60; i++)
			minuteChoices[i] = (i < 10 ? "0" : "") + i;
		offsetMin = new ObjectChoiceField("GMT Offset (Minutes):", minuteChoices);
		
		add(header);
		add(name);
		add(offsetHr);
		add(offsetMin);

		ButtonField ok = new ButtonField("Save");
		ok.setChangeListener(new FieldChangeListener() {
			public void fieldChanged(Field f, int instance) {
				save();
				setDirty(false);
				close();
			}
		});
		ButtonField close = new ButtonField("Close");
		close.setChangeListener(new FieldChangeListener() {
			public void fieldChanged(Field f, int instance) {
				setDirty(false);
				close();
			}
		});

		HorizontalFieldManager buttons = new HorizontalFieldManager();
		buttons.add(ok);
		buttons.add(close);
		add(buttons);
	}
	
	public void save() {
		int hour = Integer.parseInt((String)offsetHr.getChoice(offsetHr.getSelectedIndex()));
		TZone zone = new TZone(name.getText(), hour >= 0, Math.abs(hour), Integer.parseInt((String) offsetMin.getChoice(offsetMin.getSelectedIndex())));
		screen.addCustom(zone);
	}
	
	public boolean keyChar(char key, int status, int time) {
		boolean retval = false;
		switch (key) {
		case Characters.ENTER:
			save();
			setDirty(false);
			close();
			retval = true;
			break;
		case Characters.ESCAPE:
			close();
			break;
		default:
			retval = super.keyChar(key, status, time);
		}
		return retval;
	}
	
}