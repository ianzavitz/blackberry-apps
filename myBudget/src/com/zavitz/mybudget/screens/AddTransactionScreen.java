package com.zavitz.mybudget.screens;

import com.zavitz.mybudget.UiApp;
import com.zavitz.mybudget.elements.*;
import com.zavitz.mybudget.fields.*;

import net.rim.device.api.i18n.DateFormat;
import net.rim.device.api.system.Characters;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.container.*;
import net.rim.device.api.ui.component.*;

public class AddTransactionScreen extends PopupScreen {
	
	private ObjectChoiceField budget;
	private ButtonField ok, close; 
	private AutoTextEditField amount, comments;
	private DateField date;
	private TransactionList list;

	public AddTransactionScreen(Budget b) {
		this();
		budget.setSelectedIndex(b.getName());
	}
	
	public AddTransactionScreen() {
		super(new VerticalFieldManager());
		
		LabelField header = new LabelField("Add a Transaction");
		header.setFont(getFont().derive(Font.BOLD));
		
		budget = new ObjectChoiceField("Budget: ", UiApp.activeManager.getNames());	
		date = new DateField("Date:", System.currentTimeMillis(), DateFormat.DATE_MEDIUM);
		amount = new AutoTextEditField("Amount: ", "", 100, AutoTextEditField.FILTER_REAL_NUMERIC);
		comments = new AutoTextEditField("Comments: ", "");

		ok = new ButtonField("Ok");
		ok.setChangeListener(new FieldChangeListener() {
			public void fieldChanged(Field f, int instance) {
				boolean flag = true;
				Budget budgetItem = UiApp.activeManager.getFromName((String) budget.getChoice(budget.getSelectedIndex()));
				if(budgetItem != null) {
					Transaction transaction = new Transaction(date.getDate(), Double.parseDouble(amount.getText()), comments.getText());
					if(budgetItem.getAmountSpent() + transaction.getAmount() > budgetItem.getAmount())
						flag = Dialog.ask(Dialog.D_OK_CANCEL, "Adding this transaction will put you over your monthly budget. The excess amount will be deducted from your savings.") == Dialog.OK;
					if(flag) {
						budgetItem.addTransaction(transaction);
						UiApp.save();
						if(list != null)
							list.update();
						UiApplication.getUiApplication().repaint();
					}
				}
				if(flag) {
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
		add(budget);
		add(date);
		add(amount);
		add(comments);
		add(buttons);
	}
	
	public void setTransactionList(TransactionList l) {
		list = l;
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
	
}
