package com.zavitz.mybudget.screens;

import com.zavitz.mybudget.UiApp;
import com.zavitz.mybudget.Utilities;
import com.zavitz.mybudget.elements.Budget;
import com.zavitz.mybudget.elements.Transaction;

import net.rim.device.api.system.Characters;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.ui.container.*;

public class ModifyBudgetScreen extends PopupScreen {

	private ButtonField ok, close;
	private AutoTextEditField amount;
	private ObjectChoiceField type;
	private CheckboxField recurring;

	public ModifyBudgetScreen(final Budget budget) {
		super(new VerticalFieldManager());

		LabelField header = new LabelField("Modify a Budget");
		header.setFont(getFont().derive(Font.BOLD));
		LabelField sheader = new LabelField("Currently: $" + Utilities.formatDouble(budget.getAmount()));
		sheader.setFont(getFont().derive(Font.ITALIC, getFont().getHeight() - 4));

		type = new ObjectChoiceField("Modification:", new String[] { "None", "Add",
				"Subtract", "Edit" });
		type.setChangeListener(new FieldChangeListener() {
			public void fieldChanged(Field f, int instance) {
				boolean contained = false;
				for(int i = 0; i < getFieldCount(); i++)
					if(getField(i) == amount)
						contained = true;
				switch (type.getSelectedIndex()) {
				case 0:
					if(contained)
						delete(amount);
					break;
				case 1:
					if(!contained) {
						insert(amount, 3);
						contained = true;
					}
					amount.setText("");
				case 2:
					if(!contained)
						insert(amount, 3);
					break;
				case 3:
					if(!contained)
						insert(amount, 3);
					amount.setText(String.valueOf(Utilities.formatDoubleNoCommas(budget.getAmount())));
					break;
				}
			}
		});
		amount = new AutoTextEditField("Amount: ", "", 100,
				AutoTextEditField.FILTER_REAL_NUMERIC);
		recurring = new CheckboxField("Recurring", budget.getRecurring());

		ok = new ButtonField("Ok");
		ok.setChangeListener(new FieldChangeListener() {
			public void fieldChanged(Field f, int instance) {
				switch (type.getSelectedIndex()) {
				case 1:
					budget.setAmount(budget.getAmount()
							+ Double.parseDouble(amount.getText()));
					break;
				case 2:
					budget.setAmount(budget.getAmount()
							- Double.parseDouble(amount.getText()));
					break;
				case 3:
					budget.setAmount(Double.parseDouble(amount.getText()));
					break;
				}
				budget.setRecurring(recurring.getChecked());
				UiApp.save();
				setDirty(false);
				close();
				UiApplication.getUiApplication().repaint();
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
		add(sheader);
		add(type);
		add(recurring);
		add(buttons);
	}

	public boolean keyChar(char key, int status, int time) {
		boolean retval = false;
		switch (key) {
		case Characters.ENTER:
			if (getLeafFieldWithFocus() != close) {
				ok.getChangeListener().fieldChanged(null, 0);
				retval = true;
				break;
			}
		case Characters.ESCAPE:
			close.getChangeListener().fieldChanged(null, 0);
			break;
		default:
			retval = super.keyChar(key, status, time);
		}
		return retval;
	}

}
