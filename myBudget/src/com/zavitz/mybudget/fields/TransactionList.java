package com.zavitz.mybudget.fields;

import net.rim.device.api.system.Characters;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.XYEdges;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.ui.container.*;

import com.zavitz.mybudget.Utilities;
import com.zavitz.mybudget.elements.*;

/* This class will list all transactions in a tabular view */
public class TransactionList extends HorizontalFieldManager {

	private Budget budget;
	private VerticalFieldManager vDate, vAmount, vComments;
	
	public TransactionList(Budget _budget) {
		super();
		budget = _budget;

		vDate = new VerticalFieldManager(USE_ALL_HEIGHT);
		vAmount = new VerticalFieldManager(USE_ALL_HEIGHT) {
			public void paint(Graphics g) {
				g.drawLine(1, 0, 1, getContentHeight());
				super.paint(g);
			}
		};
		vAmount.setPadding(new XYEdges(0, 0, 0, 3));
		vComments = new VerticalFieldManager(USE_ALL_HEIGHT) {
			public void paint(Graphics g) {
				g.drawLine(0, 0, 0, getContentHeight());
				super.paint(g);
			}
		};
		vComments.setPadding(new XYEdges(0, 0, 0, 3));
		add(vDate);
		add(vAmount);
		add(vComments);
		
		add("Date", "Amount", "Comments", true);
		for(int i = 0; i < budget.getTransactions().length; i++)
			add(budget.getTransaction(i));
	}
	
	public void update() {
		vDate.deleteAll();
		vAmount.deleteAll();
		vComments.deleteAll();
		
		add("Date", "Amount", "Comments", true);
		for(int i = 0; i < budget.getTransactions().length; i++)
			add(budget.getTransaction(i));
	}
	
	public int getSelectedIndex() {
		return vDate.getFieldWithFocusIndex() > 0 ? vDate.getFieldWithFocusIndex() - 1 : -1;
	}
		
	public void add(final Transaction t) {
		LabelField date = new LabelField(Utilities.getFormatted(t.getDate(), Utilities.MDY), LabelField.FOCUSABLE) {
			public boolean navigationClick(int status, int time) {
				Status.show(t.getComments(), 60000);
				return true;
			}
		};
		LabelField amount = new LabelField("$" + Utilities.formatDouble(t.getAmount()));
		amount.setPadding(new XYEdges(0, 0, 0, 5));
		LabelField comments = new LabelField(t.getComments(), LabelField.ELLIPSIS);
		comments.setPadding(new XYEdges(0, 0, 0, 5));
		vDate.add(date);
		vAmount.add(amount);
		vComments.add(comments);
	}
	
	public void add(String s, String d, String f, boolean bold) {
		LabelField date = new LabelField(s);
		LabelField amount = new LabelField(d);
		LabelField comments = new LabelField(f);
		if(bold) {
			Font font = getFont().derive(Font.BOLD);
			date.setFont(font);
			amount.setFont(font);
			comments.setFont(font);
		}
		vDate.add(date);
		vAmount.add(amount);
		vComments.add(comments);
		date.setPadding(new XYEdges(0, 0, 0, 0));
		amount.setPadding(new XYEdges(0, 0, 0, 5));
		comments.setPadding(new XYEdges(0, 0, 0, 5));
	}
}
