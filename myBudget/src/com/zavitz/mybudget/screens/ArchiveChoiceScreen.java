package com.zavitz.mybudget.screens;

import java.util.Calendar;
import net.rim.device.api.i18n.*;

import com.zavitz.mybudget.elements.Budget;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.DateField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.Status;
import net.rim.device.api.ui.container.*;

/* This popup will allow you to pick from your previous months */
public class ArchiveChoiceScreen extends PopupScreen {

	private DateField month;
	private ButtonField ok, close;

	public ArchiveChoiceScreen() {
		super(new VerticalFieldManager());
		LabelField header = new LabelField("View Budget Archive");
		header.setFont(getFont().derive(Font.BOLD));

		month = new DateField("Month:", Calendar.getInstance().getTime()
				.getTime(), new SimpleDateFormat("MMMM yyyy"), DateField.DATE);
		
		ok = new ButtonField("Ok");
		ok.setChangeListener(new FieldChangeListener() {
			public void fieldChanged(Field f, int instance) {
				UiApplication.getUiApplication().pushScreen(new ArchiveScreen(month.getDate()));
				setDirty(false);
				close();
			}
		});
		close = new ButtonField("Close");
		close.setChangeListener(new FieldChangeListener() {
			public void fieldChanged(Field f, int instance) {
				setDirty(false);
				close();
			}
		});

		HorizontalFieldManager buttons = new HorizontalFieldManager();
		buttons.add(ok);
		buttons.add(close);

		add(header);
		add(month);
		add(buttons);
	}
	
}
