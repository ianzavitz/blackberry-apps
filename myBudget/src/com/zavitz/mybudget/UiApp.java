package com.zavitz.mybudget;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Timer;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.xml.parsers.DocumentBuilder;
import net.rim.device.api.xml.parsers.DocumentBuilderFactory;
import net.rim.device.api.xml.parsers.ParserConfigurationException;

import com.zavitz.mybudget.elements.*;
import com.zavitz.mybudget.screens.HomeScreen;
import com.zavitz.mybudget.wizard.Step1;

import net.rim.device.api.system.*;

public class UiApp extends UiApplication {
	
	public static ActiveManager activeManager;
	public static ArchiveManager archiveManager;
	private static final long KEY = 0x5159fdd7a24d4a11L; // com.zavitz.mybudget
	private static PersistentObject object;
	public static HomeScreen hScreen;

	public static void main(String args[]) {
		new UiApp().enterEventDispatcher();
	}

	public UiApp() {
		init();
	}
	
	public static void clear() {
		activeManager.deleteAll();
		activeManager.setIncome(-1);
		activeManager.setSavingsTotal(-1);
		archiveManager.deleteAll();
		object.setContents(null);
		object.commit();
	}
	
	public static void save() {
		String s = "<root>";
		s += activeManager.serialize();
		s += archiveManager.serialize();
		s += "</root>";
		object.setContents(s);
		object.commit();
	}

	private void init() {
		String s = loadXML();
		activeManager = new ActiveManager();
		archiveManager = new ArchiveManager();
		if (s.equals("")) {
			hScreen = new HomeScreen();
			pushScreen(hScreen);
			pushScreen(new Step1());
		} else {
			try {
				hScreen = parseXML(s);
				Timer timer = new Timer();
				timer.scheduleAtFixedRate(new ExpirationChecker(), Utilities.getNextHour().getTime() - Calendar.getInstance().getTime().getTime(), 3600000);
				pushScreen(hScreen);
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private String loadXML() {
		object = PersistentStore.getPersistentObject(KEY);
		Object contents = object.getContents();
		if (contents == null)
			return "";
		else
			return (String) contents;
	}

	private HomeScreen parseXML(String xml)
			throws ParserConfigurationException, SAXException, IOException {
		System.out.println(xml);
		ByteArrayInputStream input = new ByteArrayInputStream(xml.getBytes());

		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
				.newInstance();
		docBuilderFactory.setCoalescing(true);
		DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
		docBuilder.isValidating();
		Document doc = docBuilder.parse(input);
		doc.getDocumentElement().normalize();
		NodeList list = doc.getElementsByTagName("*");
		
		/* Following code is responsible for the ActiveManager and current month */		
		NodeList active = list.item(1).getChildNodes();
		Node income = active.item(0);
		Node savings = active.item(1);
		NodeList budgetItems = active.item(2).getChildNodes();
		
		ActiveManager activeManager = new ActiveManager();
		activeManager.setIncome(Double.valueOf(income.getChildNodes().item(0).getNodeValue()).doubleValue());
		activeManager.setSavingsTotal(Double.valueOf(savings.getChildNodes().item(0).getNodeValue()).doubleValue());
		for(int i = 0; i < budgetItems.getLength(); i++) {
			Budget budget = new Budget();
			
			NodeList budgetItem = budgetItems.item(i).getChildNodes();
			Node name = budgetItem.item(0);
			Node start = budgetItem.item(1);
			Node length = budgetItem.item(2);
			Node amount = budgetItem.item(3);
			Node recurring = budgetItem.item(4);
			NodeList transactions = budgetItem.item(5).getChildNodes();
			for(int j = 0; j < transactions.getLength(); j++) {
				Transaction transaction = new Transaction();
				NodeList transactionItem = transactions.item(j).getChildNodes();
				
				Node t_date = transactionItem.item(0);
				Node t_amount = transactionItem.item(1);
				Node t_comments = transactionItem.item(2);
				
				transaction.setDate(Utilities.parseFormatted(t_date.getChildNodes().item(0).getNodeValue(), Utilities.MDY));
				transaction.setAmount(Double.parseDouble(t_amount.getChildNodes().item(0).getNodeValue()));
				transaction.setComments(t_comments.hasChildNodes() ? t_comments.getChildNodes().item(0).getNodeValue() : "");
				
				budget.addTransaction(transaction);
			}

			budget.setName(nodeVal(name));
			budget.setStart(Long.parseLong(nodeVal(start)));
			budget.setLength(nodeVal(length));
			budget.setAmount(Double.parseDouble(nodeVal(amount)));
			budget.setRecurring(nodeVal(recurring).equals("true"));
			
			activeManager.addBudgetItem(budget);
		}
		
		/* Following code is responsible for the ArchiveManager and all past months */
		int archiveIdx = 0;
		for(int i = 0; i < list.getLength(); i++)
			if(list.item(i).getNodeName().equals("archive"))
				archiveIdx = i;
		NodeList archiveItems = list.item(archiveIdx).getChildNodes();
		ArchiveManager archiveManager = new ArchiveManager();
		for(int i = 0; i < archiveItems.getLength(); i++) {
			Budget budget = new Budget();
			
			NodeList budgetItem = archiveItems.item(i).getChildNodes();
			Node name = budgetItem.item(0);
			Node start = budgetItem.item(1);
			Node length = budgetItem.item(2);
			Node amount = budgetItem.item(3);
			Node recurring = budgetItem.item(4);
			NodeList transactions = budgetItem.item(5).getChildNodes();
			for(int k = 0; k < transactions.getLength(); k++) {
				Transaction transaction = new Transaction();
				NodeList transactionItem = transactions.item(k).getChildNodes();
				
				Node t_date = transactionItem.item(0);
				Node t_amount = transactionItem.item(1);
				Node t_comments = transactionItem.item(2);
				
				transaction.setDate(Utilities.parseFormatted(t_date.getChildNodes().item(0).getNodeValue(), Utilities.MDY));
				transaction.setAmount(Double.parseDouble(t_amount.getChildNodes().item(0).getNodeValue()));
				transaction.setComments(t_comments.hasChildNodes() ? t_comments.getChildNodes().item(0).getNodeValue() : "");
				
				budget.addTransaction(transaction);
			}

			budget.setName(nodeVal(name));
			budget.setStart(Long.parseLong(nodeVal(start)));
			budget.setLength(nodeVal(length));
			budget.setAmount(Double.parseDouble(nodeVal(amount)));
			budget.setRecurring(nodeVal(recurring).equals("true"));
			
			archiveManager.addBudget(budget);
		}
		
		UiApp.activeManager = activeManager;
		UiApp.archiveManager = archiveManager;
		
		new ExpirationChecker().run();
		return new HomeScreen();
	}
	
	private String nodeVal(Node node) {
		if(!node.hasChildNodes())
			return "";
		else
			return node.getChildNodes().item(0).getNodeValue();
	}

}
