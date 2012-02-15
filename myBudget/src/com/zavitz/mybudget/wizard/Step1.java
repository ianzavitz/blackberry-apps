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

public class Step1 extends PopupScreen {
	
	private ButtonField next; 
	public AutoTextEditField income, savings;
	
	public Step1() {
		super(new VerticalFieldManager(VERTICAL_SCROLL | VERTICAL_SCROLLBAR));
		
		LabelField header = new LabelField("myBudget Wizard - Step 1");
		header.setFont(getFont().derive(Font.BOLD));
		LabelField sheader = new LabelField("Please enter your monthly income and current savings. Your income will be used to show your projected monthly savings.");
		sheader.setFont(getFont().derive(Font.ITALIC, getFont().getHeight() - 5));
		
		income = new AutoTextEditField("Monthly Income: ", "", 100, AutoTextEditField.FILTER_REAL_NUMERIC);
		savings = new AutoTextEditField("Current Savings: ", "", 100, AutoTextEditField.FILTER_REAL_NUMERIC);

		next = new ButtonField("Next");
		next.setChangeListener(new FieldChangeListener() {
			public void fieldChanged(Field f, int instance) {
				UiApp.activeManager.setIncome(Double.parseDouble(income.getText()));
				UiApp.activeManager.setSavingsTotal(Double.parseDouble(savings.getText()));
				UiApp.save();
				UiApplication.getUiApplication().repaint();
				setDirty(false);
				close();
				UiApplication.getUiApplication().pushScreen(new Step2());
			}
		});
		
		
		add(header);
		add(sheader);
		add(income);
		add(savings);
		add(next);
	}
	
}
