package com.zavitz.mybudget.screens;

import java.util.Calendar;

import com.zavitz.mybudget.Utilities;
import com.zavitz.mybudget.elements.*;

import net.rim.device.api.i18n.DateFormat;
import net.rim.device.api.system.Characters;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.container.*;
import net.rim.device.api.ui.component.*;

public class AddBudgetScreen extends PopupScreen {

	private ButtonField ok, close;
	private ObjectChoiceField category;
	private AutoTextEditField custom, amount;
	private CheckboxField recurring;
	private AddBudget callback;
	private String[] categories = new String[] {
			"Auto/Car", "Bank Fees", "Cash Withdrawls", "Charity/Donations", 
			"Children", "Childcare", "Clothing", "Credit Card", "Dining",
			"Education", "Entertainment", "Groceries", "Health/Fitness",
			"Household", "Insurance", "Loan", "Medical", "Mortgage",
			"Pets", "Rent", "Taxes", "Travel", "Utilities",
			"Custom"
	};

	public AddBudgetScreen() {
		this(null);
	}

	public AddBudgetScreen(AddBudget addBudget) {
		super(new VerticalFieldManager());

		setCallback(addBudget);

		LabelField header = new LabelField("Add a Budget");
		header.setFont(getFont().derive(Font.BOLD));

		category = new ObjectChoiceField("Category: ", categories);
		category.setChangeListener(new ObjectChoiceListener());
		custom = new AutoTextEditField("Custom Category: ", "");
		amount = new AutoTextEditField("Amount: ", "", 100,
				AutoTextEditField.FILTER_REAL_NUMERIC);
		
		recurring = new CheckboxField("Recurring", true);

		ok = new ButtonField("Ok");
		ok.setChangeListener(new FieldChangeListener() {
			public void fieldChanged(Field f, int instance) {
				if (amount.getText().equals("")) {
					Status.show("Please enter a value for amount.", 1000);
					amount.setFocus();
				} else {
					Budget budget = new Budget();
					if(category.getSelectedIndex() == categories.length - 1) 
						budget.setName(custom.getText());
					else
						budget.setName((String) category.getChoice(category.getSelectedIndex()));
					budget.setAmount(Double.parseDouble(amount.getText()));
					budget.setStart(Utilities.getMonthStartLong());
					budget.setLength(0, 1, 0);
					budget.setRecurring(recurring.getChecked());
					
					if (callback != null)
						callback.addBudget(budget);
					setDirty(false);
					close();
				}
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
		add(category);
		add(amount);
		add(recurring);
		add(buttons);
	}

	public void setCallback(AddBudget addBudget) {
		callback = addBudget;
	}

	public boolean keyChar(char key, int status, int time) {
		boolean retval = false;
		switch (key) {
		case Characters.ENTER:
			ok.getChangeListener().fieldChanged(null, 0);
			retval = true;
			break;
		case Characters.ESCAPE:
			close.getChangeListener().fieldChanged(null, 0);
			break;
		default:
			retval = super.keyChar(key, status, time);
		}
		return retval;
	}
	
	public class ObjectChoiceListener implements FieldChangeListener {

		public void fieldChanged(Field field, int context) {
			boolean contained = false;
			int insertAt = 0;
			for(int i = 0; i < getFieldCount(); i++) {
				if(getField(i) == custom)
					contained = true;
				if(getField(i) == category)
					insertAt = i + 1;
			}
			
			if(!contained && category.getSelectedIndex() == category.getSize() - 1)
				insert(custom, insertAt);
			else if(contained && category.getSelectedIndex() != category.getSize() - 1)
				delete(custom);				
		}
		
	}

	public interface AddBudget {
		public void addBudget(Budget budget);
	}

}
