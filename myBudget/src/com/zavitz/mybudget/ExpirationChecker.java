package com.zavitz.mybudget;

import java.util.TimerTask;

import com.zavitz.mybudget.screens.HomeScreen;

public class ExpirationChecker extends TimerTask {
	
	public void run() {
		if(Utilities.budgetCheck(UiApp.activeManager, UiApp.archiveManager) && HomeScreen.title != null)
			HomeScreen.title.invalidate();
	}
	
}
