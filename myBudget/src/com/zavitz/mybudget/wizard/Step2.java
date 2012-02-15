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
import net.rim.device.api.util.Arrays;

public class Step2 extends PopupScreen {

	private ButtonField next, back;
	public CheckboxField categoryFields[];
	private String[] categories = new String[] { "Auto/Car", "Bank Fees",
			"Cash Withdrawls", "Charity/Donations", "Children", "Childcare",
			"Clothing", "Credit Card", "Dining", "Education", "Entertainment",
			"Groceries", "Health/Fitness", "Household", "Insurance", "Loan",
			"Medical", "Mortgage", "Pets", "Rent", "Taxes", "Travel",
			"Utilities" };

	public Step2() {
		super(new VerticalFieldManager(VERTICAL_SCROLL | VERTICAL_SCROLLBAR));
		
		LabelField header = new LabelField("myBudget Wizard - Step 2");
		header.setFont(getFont().derive(Font.BOLD));
		LabelField sheader = new LabelField("Select the categories you would like to add a budget for.");
		sheader.setFont(getFont().derive(Font.ITALIC, getFont().getHeight() - 5));
		add(header);
		add(sheader);
		
		categoryFields = new CheckboxField[categories.length];
		for(int i = 0; i < categories.length; i++) {
			categoryFields[i] = new CheckboxField(categories[i], false);
			add(categoryFields[i]);
		}

		next = new ButtonField("Next");
		next.setChangeListener(new FieldChangeListener() {
			public void fieldChanged(Field f, int instance) {
				String[] addCat = new String[0];
				for(int i = 0; i < categoryFields.length; i++)
					if(categoryFields[i].getChecked())
						Arrays.add(addCat, categories[i]);
				
				setDirty(false);
				close();
				UiApplication.getUiApplication().pushScreen(new Step3(addCat));
			}
		});

		back = new ButtonField("Back");
		back.setChangeListener(new FieldChangeListener() {
			public void fieldChanged(Field f, int instance) {
				setDirty(false);
				close();
				Step1 s = new Step1();
				s.income.setText(String.valueOf(UiApp.activeManager.getIncome()));
				s.savings.setText(String.valueOf(UiApp.activeManager.getSavingsTotal()));
				UiApplication.getUiApplication().pushScreen(s);
			}
		});
		
		HorizontalFieldManager cmd = new HorizontalFieldManager();
		cmd.add(back);
		cmd.add(next);
		add(cmd);
	}
	
}
