package com.zavitz.mybudget.elements;

import net.rim.device.api.util.Arrays;

public class ActiveManager {

	private double income;
	private double savingsTotal;
	private Budget[] budgetItems;
	
	public ActiveManager() {
		this(-1, -1, new Budget[0]);
	}
	public ActiveManager(double _income, double _savingsTotal) {
		this(_income, _savingsTotal, new Budget[0]);
	}
	public ActiveManager(double _income, double _savingsTotal, Budget[] _budgetItems) {
		income = _income;
		savingsTotal = _savingsTotal;
		budgetItems = _budgetItems;
	}
	
	public String[] getNames() {
		String[] temp = new String[getLength()];
		for(int i = 0; i < temp.length; i++) {
			temp[i] = budgetItems[i].getName();
		}
		return temp;
	}
	
	public Budget getFromName(String name) {
		for(int i = 0; i < getLength(); i++)
			if(budgetItems[i].getName().equalsIgnoreCase(name))
				return budgetItems[i];
		
		return null;
	}
	
	public int getLength() {
		return budgetItems.length;
	}
	
	public double getTotalSpent() {
		double d = 0;
		for(int i = 0; i < budgetItems.length; i++) {
			d += budgetItems[i].getAmountSpent();
		}
		return d;
	}
	
	public void setIncome(double _income) {
		income = _income;
	}
	
	public void setSavingsTotal(double _savingsTotal) {
		savingsTotal = _savingsTotal;
	}
	
	public void addBudgetItem(Budget budget) {
		Arrays.add(budgetItems, budget);
	}
	
	public void deleteBudgetItem(Budget budget) {
		Arrays.remove(budgetItems, budget);
	}
	
	public double getIncome() {
		return income;
	}
	
	public double getProjectedSavings() {
		double d = income;
		for(int i = 0; i < budgetItems.length; i++)
			d -= budgetItems[i].getAmount();
		return d;
	}
	
	public double getSavingsTotal() {
		return savingsTotal;
	}
	
	public Budget[] getBudgetItems() {
		return budgetItems;
	}
	
	public Budget getBudgetItem(int index) {
		return budgetItems[index];
	}
	
	public void resetItems(Budget[] items) {
		for(int i = 0; i < items.length; i++)
			for(int j = 0; j < budgetItems.length; j++)
				if(items[i] == budgetItems[j])
					budgetItems[j] = budgetItems[j].reset();
	}
	
	public void deleteAll() {
		budgetItems = new Budget[0];
	}
	
	public void addSavings(Budget[] items) {
		for(int i = 0; i < items.length; i++)
			savingsTotal += items[i].getAmount() - items[i].getAmountSpent();
	}
	
	public String serialize() {
		String s = "<active>";
		s += "<income>" + income + "</income>";
		s += "<savings>" + savingsTotal + "</savings>";
		s += "<items>";
		for(int i = 0; i < budgetItems.length; i++)
			s += budgetItems[i].serialize();
		s += "</items>";
		s += "</active>";
		return s;
	}
	
}