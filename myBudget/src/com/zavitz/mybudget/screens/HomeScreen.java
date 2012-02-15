package com.zavitz.mybudget.screens;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Characters;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;

import com.zavitz.mybudget.UiApp;
import com.zavitz.mybudget.elements.*;
import com.zavitz.mybudget.fields.*;
import com.zavitz.mybudget.wizard.*;
import com.zavitz.mybudget.screens.AddBudgetScreen.AddBudget;

import net.rim.device.api.ui.component.*;
import net.rim.device.api.ui.*;
import net.rim.device.api.util.Arrays;

public class HomeScreen extends MainScreen implements AddBudget {

	private VerticalFieldManager internalManager, manager;
	public static Bitmap bg;
	public static TitleField title;

	static {
		bg = Bitmap.getBitmapResource("main_bg.png");
	}

	public HomeScreen() {
		super(Manager.NO_VERTICAL_SCROLL | Manager.NO_VERTICAL_SCROLLBAR);
		internalManager = new VerticalFieldManager(Manager.NO_VERTICAL_SCROLL
				| Manager.NO_VERTICAL_SCROLLBAR) {
			public void paintBackground(Graphics g) {
				g.setBackgroundColor(0x00204f6a);
				g.clear();

				XYRect rect = new XYRect();
				rect.height = Display.getHeight() < bg.getHeight() ? Display
						.getHeight() : bg.getHeight();
				rect.width = Display.getWidth() < bg.getWidth() ? Display
						.getWidth() : bg.getWidth();
				// for the x position, check if the display width is greater
				// than the image, if so you need to put it it at half of the
				// distance
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
		title = new TitleField();
		internalManager.add(title);
		internalManager.add(manager);

		for (int i = 0; i < UiApp.activeManager.getLength(); i++) {
			add(new BudgetField(UiApp.activeManager.getBudgetItem(i)));
		}
	}

	public void add(Field field) {
		manager.add(field);
		if (field instanceof BudgetField)
			((BudgetField) field).setEven(manager.getFieldCount() % 2 == 0);
	}

	private HomeScreen instance() {
		return this;
	}

	public void makeMenu(Menu menu, int instance) {
		menu.add(m_addBudgetItem);
		if (getLeafFieldWithFocus() instanceof BudgetField) {
			menu.add(m_modifyBudget);
			menu.add(m_moveBudgetItem);
			menu.add(m_removeBudgetItem);
			menu.addSeparator();
			menu.add(m_addTransaction);
			menu.add(new MenuItem("View Transactions ("
					+ ((BudgetField) getLeafFieldWithFocus()).getBudget()
							.getTransactions().length + ")", 110, 1) {
				public void run() {
					if (((BudgetField) getLeafFieldWithFocus()).getBudget()
							.getTransactions().length == 0)
						Status.show("You have no transactions.");
					else
						UiApplication.getUiApplication().pushScreen(
								new TransactionScreen(
										((BudgetField) getLeafFieldWithFocus())
												.getBudget()));
				}
			});
			menu.setDefault(m_addTransaction);
		}
		menu.addSeparator();
		menu.add(m_viewArchive);
		menu.add(m_modifySavings);
		menu.add(m_modifyIncome);
		menu.addSeparator();
		menu.add(m_runWizard);
		super.makeMenu(menu, instance);
	}

	private MenuItem m_addBudgetItem = new MenuItem("Add Budget", 110, 1) {
		public void run() {
			UiApplication.getUiApplication().pushScreen(
					new AddBudgetScreen(instance()));
		}
	};

	private MenuItem m_addTransaction = new MenuItem("Add Transaction", 110, 1) {
		public void run() {
			BudgetField field = (BudgetField) getLeafFieldWithFocus();
			UiApplication.getUiApplication().pushScreen(
					new AddTransactionScreen(field.getBudget()));
		}
	};

	private MenuItem m_removeBudgetItem = new MenuItem("Remove Budget", 110, 1) {
		public void run() {
			BudgetField field = (BudgetField) getLeafFieldWithFocus();
			removeBudget(field.getBudget());
			delete(field);
		}
	};

	private MenuItem m_moveBudgetItem = new MenuItem("Move Budget", 110, 1) {
		public void run() {
			BudgetField field = (BudgetField) getLeafFieldWithFocus();
			UiApplication.getUiApplication().pushScreen(
					new MovePopup(field.getBudget()));
		}
	};

	private MenuItem m_viewArchive = new MenuItem("View Archive", 110, 1) {
		public void run() {
			UiApplication.getUiApplication().pushScreen(
					new ArchiveChoiceScreen());
		}
	};

	private MenuItem m_modifySavings = new MenuItem("Modify Savings Total",
			110, 1) {
		public void run() {
			UiApplication.getUiApplication().pushScreen(
					new ModifySavingsScreen());
		}
	};

	private MenuItem m_modifyIncome = new MenuItem("Modify Monthly Income",
			110, 1) {
		public void run() {
			UiApplication.getUiApplication().pushScreen(
					new ModifyIncomeScreen());
		}
	};

	private MenuItem m_modifyBudget = new MenuItem("Modify Budget", 110, 1) {
		public void run() {
			UiApplication.getUiApplication()
					.pushScreen(
							new ModifyBudgetScreen(
									((BudgetField) getLeafFieldWithFocus())
											.getBudget()));
		}
	};

	private MenuItem m_runWizard = new MenuItem("Run Wizard", 110, 1) {
		public void run() {
			int d = Dialog.ask(
					"Running the wizard will clear all current data.",
					new String[] { "Continue", "Cancel" }, 1);
			if (d == 0) {
				manager.deleteAll();
				UiApp.clear();
				UiApplication.getUiApplication().pushScreen(new Step1());
			}
		}
	};

	public void moveBudget(Budget budget, int direction) {
		int currentPosition = Arrays.getIndex(UiApp.activeManager
				.getBudgetItems(), budget);
		if (currentPosition + direction >= UiApp.activeManager.getLength()
				|| currentPosition + direction < 0)
			return;

		Arrays.removeAt(UiApp.activeManager.getBudgetItems(), currentPosition);
		Arrays.insertAt(UiApp.activeManager.getBudgetItems(), budget,
				currentPosition + direction);

		int idx = -1;
		for (int i = 0; i < manager.getFieldCount(); i++) {
			if (((BudgetField) manager.getField(i)).getBudget() == budget)
				idx = i;
		}

		manager.deleteAll();
		for (int i = 0; i < UiApp.activeManager.getLength(); i++) {
			add(new BudgetField(UiApp.activeManager.getBudgetItem(i)));
		}
		manager.getField(idx + direction).setFocus();
	}

	public void removeBudget(Budget budget) {
		UiApp.activeManager.deleteBudgetItem(budget);
		UiApp.save();
	}

	public void addBudget(Budget budget) {
		UiApp.activeManager.addBudgetItem(budget);
		UiApp.save();

		add(new BudgetField(budget));
	}

	class MovePopup extends PopupScreen implements FieldChangeListener {
		Budget budget;
		ButtonField up, set, down;

		public MovePopup(Budget budget) {
			super(new VerticalFieldManager());
			this.budget = budget;

			up = new ButtonField("U\u0332p", Field.FIELD_HCENTER
					| ButtonField.CONSUME_CLICK | DrawStyle.HCENTER);
			up.setChangeListener(this);
			set = new ButtonField("S\u0332et", Field.FIELD_HCENTER
					| ButtonField.CONSUME_CLICK | DrawStyle.HCENTER);
			set.setChangeListener(this);
			down = new ButtonField("D\u0332own", Field.FIELD_HCENTER
					| ButtonField.CONSUME_CLICK | DrawStyle.HCENTER);
			down.setChangeListener(this);

			add(up);
			add(set);
			add(down);
		}

		public void sublayout(int width, int height) {
			super.sublayout(width, height);
			setPosition(Display.getWidth() - getWidth(), Display.getHeight()
					- getHeight());
		}

		public void fieldChanged(Field field, int context) {
			if (field == up) {
				moveBudget(budget, -1);
			} else if (field == set) {
				close();
			} else if (field == down) {
				moveBudget(budget, 1);
			}
		}

		public boolean keyChar(char key, int status, int time) {
			if (key == Characters.ESCAPE) {
				close();
				return true;
			} else {
				switch (key) {
				case 'u':
					fieldChanged(up, 0);
					return true;
				case 's':
					fieldChanged(set, 0);
					return true;
				case 'd':
					fieldChanged(down, 0);
					return true;
				}
			}
			return super.keyChar(key, status, time);
		}

	}

}
