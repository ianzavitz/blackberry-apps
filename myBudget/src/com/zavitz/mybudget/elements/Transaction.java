package com.zavitz.mybudget.elements;

import com.zavitz.mybudget.Utilities;

public class Transaction {

	private long 	date;
	private double 	amount;
	private String 	comments;
	
	public Transaction() {
		this(0, 0.00, "");
	}
	public Transaction(long _date, double _amount) {
		this(_date, _amount, "");
	}
	public Transaction(long _date, double _amount, String _comments) {
		date = _date;
		amount = _amount;
		comments = _comments;
	}
	
	public void setDate(long _date) {
		date = _date;
	}
	
	public void setAmount(double _amount) {
		amount = _amount;
	}
	
	public void setComments(String _comments) {
		comments = _comments;
	}
	
	public long getDate() {
		return date;
	}
	
	public double getAmount() {
		return amount;
	}
	
	public String getComments() {
		return comments;
	}
	
	public String serialize() {
		String s = "<transaction>";
		s += "<date>" + Utilities.getFormatted(date, Utilities.MDY) + "</date>";
		s += "<amount>" + amount + "</amount>";
		s += "<comments>" + comments + "</comments>";
		s += "</transaction>";
		return s;
	}
	
}
