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
import net.rim.device.api.ui.component.AutoTextEditField;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.container.*;

public class ModifySavingsScreen extends PopupScreen {

	private ButtonField ok, close;
	private AutoTextEditField amount;
	private ObjectChoiceField type;

	public ModifySavingsScreen() {
		super(new VerticalFieldManager());

		LabelField header = new LabelField("Modify Savings Total");
		header.setFont(getFont().derive(Font.BOLD));
		LabelField sheader = new LabelField("Currently: $"
				+ Utilities.formatDouble(UiApp.activeManager.getSavingsTotal()));
		sheader.setFont(getFont()
				.derive(Font.ITALIC, getFont().getHeight() - 4));

		type = new ObjectChoiceField("Modification:", new String[] { "Add",
				"Subtract", "Edit" });
		type.setChangeListener(new FieldChangeListener() {
			public void fieldChanged(Field f, int instance) {
				switch (type.getSelectedIndex()) {
				case 0:
					amount.setText("");
				case 1:
					break;
				case 2:
					amount.setText(Utilities
							.formatDoubleNoCommas(UiApp.activeManager
									.getSavingsTotal()));
					break;
				}
			}
		});
		amount = new AutoTextEditField("Amount: ", "", 100,
				AutoTextEditField.FILTER_REAL_NUMERIC);

		ok = new ButtonField("Ok");
		ok.setChangeListener(new FieldChangeListener() {
			public void fieldChanged(Field f, int instance) {
				switch (type.getSelectedIndex()) {
				case 0:
					UiApp.activeManager.setSavingsTotal(UiApp.activeManager
							.getSavingsTotal()
							+ Double.parseDouble(amount.getText()));
					break;
				case 1:
					UiApp.activeManager.setSavingsTotal(UiApp.activeManager
							.getSavingsTotal()
							- Double.parseDouble(amount.getText()));
					break;
				case 2:
					UiApp.activeManager.setSavingsTotal(Double
							.parseDouble(amount.getText()));
					break;
				}
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
		add(amount);
		add(buttons);
		amount.setFocus();
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
