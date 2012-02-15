package com.zavitz.mybudget.screens;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;

import com.zavitz.mybudget.UiApp;
import com.zavitz.mybudget.Utilities;
import com.zavitz.mybudget.elements.*;
import com.zavitz.mybudget.fields.*;
import com.zavitz.mybudget.screens.AddBudgetScreen.AddBudget;

import net.rim.device.api.ui.component.*;
import net.rim.device.api.ui.*;

public class ArchiveScreen extends MainScreen {

	private VerticalFieldManager internalManager, manager;
	public static Bitmap bg;
	public static TitleField title;
	private long from;
	
	static {
		bg = Bitmap.getBitmapResource("main_bg.png");
	}
	
	public ArchiveScreen(long from) {
		super(Manager.NO_VERTICAL_SCROLL | Manager.NO_VERTICAL_SCROLLBAR);
		this.from = from;
		internalManager = new VerticalFieldManager(Manager.NO_VERTICAL_SCROLL
				| Manager.NO_VERTICAL_SCROLLBAR) {
			public void paintBackground(Graphics g) {
				g.setBackgroundColor(0x00204f6a);
				g.clear();
				
				XYRect rect = new XYRect();
				rect.height = Display.getHeight() < bg.getHeight() ? Display.getHeight() : bg.getHeight();
				rect.width = Display.getWidth() < bg.getWidth() ? Display.getWidth() : bg.getWidth();
				// for the x position, check if the display width is greater than the image, if so you need to put it it at half of the distance
				rect.x = (Display.getWidth() - bg.getWidth()) / 2;
				rect.y = Display.getHeight() - bg.getHeight();				
				
				g.drawBitmap(rect, bg, 0, 0);
			}

			protected void sublayout(int width, int height) {
				super.sublayout(Display.getWidth(), Display.getHeight());
				setExtent(Display.getWidth(), Display.getHeight());
			}
		};
		super.add(internalManager);
		manager = new VerticalFieldManager(Manager.VERTICAL_SCROLL
				| Manager.VERTICAL_SCROLLBAR);
		title = new TitleField(Utilities.getFormatted(from, "MMMM yyyy"));
		internalManager.add(title);
		internalManager.add(manager);
		
		Budget[] items = UiApp.archiveManager.getBudgets(from);
		for(int i = 0; i < items.length; i++) {
			add(new BudgetField(items[i]));
		}
		if(items.length == 0) {
			LabelField header = new LabelField("Sorry! You have no data for the selected month.") {
				public void paint(Graphics g) {
					g.setColor(0x00FFFFFF);
					super.paint(g);
				}
			};
			header.setFont(getFont().derive(Font.ITALIC, getFont().getHeight() - 5));
			header.setPadding(5, 5, 5, 5);
			add(header);
		}
	}
	
	public void add(Field field) {
		manager.add(field);
		if(field instanceof BudgetField)
			((BudgetField) field).setEven(manager.getFieldCount() % 2 == 0);
	}
	
	private ArchiveScreen instance() {
		return this;
	}
	
	public void makeMenu(Menu menu, int instance) {
		if(getLeafFieldWithFocus() instanceof BudgetField) {
			menu.add(new MenuItem("View Transactions (" + ((BudgetField) getLeafFieldWithFocus()).getBudget().getTransactions().length + ")", 110, 1) {
				public void run() {
					if(((BudgetField) getLeafFieldWithFocus()).getBudget().getTransactions().length == 0)
						Status.show("You have no transactions.");
					else {
						TransactionScreen screen = new TransactionScreen(((BudgetField) getLeafFieldWithFocus()).getBudget());
						screen.setEditable(false);
						UiApplication.getUiApplication().pushScreen(screen);
					}
				}
			});
		}
		super.makeMenu(menu, instance);
	}
	
}
