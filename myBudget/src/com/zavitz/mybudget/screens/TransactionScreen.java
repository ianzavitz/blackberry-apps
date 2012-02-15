package com.zavitz.mybudget.screens;

import net.rim.device.api.system.Characters;
import net.rim.device.api.ui.container.*;

import net.rim.device.api.ui.component.*;
import net.rim.device.api.ui.*;

import com.zavitz.mybudget.UiApp;
import com.zavitz.mybudget.elements.*;
import com.zavitz.mybudget.fields.*;

public class TransactionScreen extends PopupScreen {

	private Budget budget;
	private TransactionList list;
	private boolean editable;

	public TransactionScreen(Budget _budget) {
		super(new VerticalFieldManager(VERTICAL_SCROLL | VERTICAL_SCROLLBAR), DEFAULT_MENU | DEFAULT_CLOSE);

		budget = _budget;
		list = new TransactionList(budget);
		add(list);
		
		editable = true;
	}

	public void setEditable(boolean flag) {
		editable = flag;
	}

	public void makeMenu(Menu menu, int instance) {
		if (editable) {
			menu.add(m_addTransaction);
			if (list.getSelectedIndex() > -1)
				menu.add(m_deleteTransaction);
		}
		super.makeMenu(menu, instance);
	}

	private MenuItem m_addTransaction = new MenuItem("Add Transaction", 110, 1) {
		public void run() {
			AddTransactionScreen s = new AddTransactionScreen(budget);
			s.setTransactionList(list);
			UiApplication.getUiApplication().pushScreen(s);
		}
	};

	private MenuItem m_deleteTransaction = new MenuItem("Delete Transaction",
			110, 1) {
		public void run() {
			if (Dialog.ask(Dialog.D_DELETE) == Dialog.DELETE) {
				budget.deleteTransactionIndex(list.getSelectedIndex());
				list.update();
				UiApp.save();
				UiApplication.getUiApplication().repaint();
				if (budget.getTransactions().length == 0) {
					close();
					Status.show("You have no transactions.");
				}
			}
		}
	};

	public boolean keyChar(char key, int status, int time) {
		boolean retval = false;
		switch (key) {
		case Characters.ESCAPE:
			setDirty(false);
			close();
			retval = true;
			break;
		default:
			retval = super.keyChar(key, status, time);
		}
		return retval;
	}

}
