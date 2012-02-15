package com.zavitz.mybudget.wizard;

import com.zavitz.mybudget.UiApp;
import com.zavitz.mybudget.Utilities;
import com.zavitz.mybudget.elements.*;
import com.zavitz.mybudget.screens.AddBudgetScreen;

import net.rim.device.api.i18n.DateFormat;
import net.rim.device.api.system.Characters;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.container.*;
import net.rim.device.api.ui.component.*;

public class Step3 extends PopupScreen {
	
	private ButtonField next, back; 
	private AutoTextEditField amounts[];
	
	public Step3(final String[] categories) {
		super(new VerticalFieldManager(VERTICAL_SCROLL | VERTICAL_SCROLLBAR));
		
		LabelField header = new LabelField("myBudget Wizard - Step 3");
		header.setFont(getFont().derive(Font.BOLD));
		LabelField sheader = new LabelField("Enter the amount you would like to budget for each category.");
		sheader.setFont(getFont().derive(Font.ITALIC, getFont().getHeight() - 5));
		add(header);
		add(sheader);
		
		amounts = new AutoTextEditField[categories.length];
		for(int i = 0; i < categories.length; i++) {
			amounts[i] = new AutoTextEditField(categories[i] + ": ", "", 100,
				AutoTextEditField.FILTER_REAL_NUMERIC);
			add(new SeparatorField());
			add(amounts[i]);
		}

		next = new ButtonField("Ok");
		next.setChangeListener(new FieldChangeListener() {
			public void fieldChanged(Field f, int instance) {
				for(int i = 0; i < amounts.length; i++) {
					if(amounts[i].getText().equals("")) {
						Status.show("Please enter an amount for " + categories[i] + ".", 1000);
						amounts[i].setFocus();
						return;
					}
				}
				for(int i = 0; i < amounts.length; i++) {
					Budget budget = new Budget();
					budget.setName(categories[i]);
					budget.setRecurring(true);
					budget.setAmount(Double.parseDouble(amounts[i].getText()));
					budget.setStart(Utilities.getMonthStartLong());
					budget.setLength(0, 1, 0);
					UiApp.hScreen.addBudget(budget);
				}
				UiApp.save();
				UiApplication.getUiApplication().repaint();
				setDirty(false);
				close();
			}
		});

		back = new ButtonField("Back");
		back.setChangeListener(new FieldChangeListener() {
			public void fieldChanged(Field f, int instance) {
				setDirty(false);
				close();
				Step2 s = new Step2();
				for(int i = 0; i < categories.length; i++) {
					for(int j = 0; j < s.categoryFields.length; j++)
						if(s.categoryFields[j].getLabel().startsWith(categories[i]))
							s.categoryFields[j].setChecked(true);
				}
				UiApplication.getUiApplication().pushScreen(s);
			}
		});
		
		HorizontalFieldManager cmd = new HorizontalFieldManager();
		cmd.add(back);
		cmd.add(next);
		add(cmd);
	}
	
}
