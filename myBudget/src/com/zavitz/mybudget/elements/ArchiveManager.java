package com.zavitz.mybudget.elements;

import com.zavitz.mybudget.Utilities;

import net.rim.device.api.util.Arrays;

public class ArchiveManager {
	
	private Budget[] expiredBudgets;
	
	public ArchiveManager() {
		expiredBudgets = new Budget[0];
	}
	
	public void addBudget(Budget budget) {
		Arrays.add(expiredBudgets, budget);
	}
	
	public void addBudgets(Budget[] budget) {
		Budget[] temp = new Budget[expiredBudgets.length + budget.length];
		for(int i = 0; i < temp.length; i++) {
			if(i < expiredBudgets.length)
				temp[i] = expiredBudgets[i];
			else
				temp[i] = budget[i - expiredBudgets.length];
		}
		expiredBudgets = temp;
	}
	
	public void deleteBudget(Budget budget) {
		Arrays.remove(expiredBudgets, budget);
	}
	
	public void deleteAll() {
		expiredBudgets = new Budget[0];
	}
	
	/*
	 * Grab budgets from archive for a preset from and to.
	 * Should create new budgets and add all data to them.
	 */
	public Budget[] getBudgets(long mo) {
		String month = Utilities.getFormatted(mo, Utilities.MY);
		Budget[] allBudgets = new Budget[0];
		for(int i = 0; i < expiredBudgets.length; i++) {
			if(Utilities.getFormatted(expiredBudgets[i].getStart(), Utilities.MY).equals(month))
				Arrays.add(allBudgets, expiredBudgets[i]);
		}
		return allBudgets;
	}
	
	public Budget[] getBudgets() {
		return expiredBudgets;
	}
	
	public Budget getBudget(int index) {
		return expiredBudgets[index];
	}
	
	public int getLength() {
		return expiredBudgets.length;
	}
	
	public String serialize() {
		String s = "<archive>";
		for(int i = 0; i < expiredBudgets.length; i++)
			s += expiredBudgets[i].serialize();
		s += "</archive>";
		return s;
	}
	
}
