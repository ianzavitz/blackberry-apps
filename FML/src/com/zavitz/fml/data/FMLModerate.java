package com.zavitz.fml.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

import net.rim.device.api.xml.parsers.*;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

public class FMLModerate extends Thread {

	private ModReceiver modReceiver;
	private String previousId;
	private boolean previousAnswer;
	public static Vector availableMods;

	static {
		availableMods = new Vector();
	}

	public FMLModerate(ModReceiver modReceiver) {
		this.modReceiver = modReceiver;

		start();
	}

	public FMLModerate(ModReceiver modReceiver, String id, boolean yes) {
		this.modReceiver = modReceiver;
		previousId = id;
		previousAnswer = yes;

		start();
	}

	public void run() {
		modReceiver.modLoading();
		if (previousId != null)
			answerMod(previousId, previousAnswer);
//		if (availableMods.isEmpty())
//			receiveMods();
		modReceiver.modReceived(getNextMod());
	}

	private void answerMod(String id, boolean yes) {
		try {
			final URL url = new URL("/mod/" + (yes ? "yes" : "no") + "/" + id);
			HttpConnection conn = (HttpConnection) Connector.open(url
					.construct());
			InputStream is = conn.openInputStream();
			final ByteArrayOutputStream os = new ByteArrayOutputStream();
			copy(is, os);
			conn.close();
		} catch (Exception e) {
		}
	}

	private void receiveMods() {
		try {
			URL url = new URL("/mod/view/");
			HttpConnection conn = (HttpConnection) Connector.open(url
					.construct());
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			copy(conn.openInputStream(), output);
			conn.close();
			ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
			
			String _node, _element;
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
					.newInstance();
			docBuilderFactory.setCoalescing(true);
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			docBuilder.isValidating();
			Document doc = docBuilder.parse(input);
			doc.getDocumentElement().normalize();
			NodeList list = doc.getElementsByTagName("*");
			_node = new String();
			_element = new String();
			
			for (int i = 0; i < list.getLength(); i++) {
				Node node = list.item(i);
				_node = node.getNodeName();
				if (_node.equals("item")) {
					NodeList children = node.getChildNodes();
					Node child = children.item(0);
					availableMods.addElement(child.getNodeValue());
				}
			}
			
			conn.close();
			System.out.println(output.toString());
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	private ModPost getNextMod() {
		try {
			URL url = new URL("/mod/last/");

			HttpConnection conn = (HttpConnection) Connector.open(url
					.construct());
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			copy(conn.openInputStream(), output);
			conn.close();
			ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
			
			String _node, _element;
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
					.newInstance();
			docBuilderFactory.setCoalescing(true);
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			docBuilder.isValidating();
			Document doc = docBuilder.parse(input);
			doc.getDocumentElement().normalize();
			NodeList list = doc.getElementsByTagName("*");
			_node = new String();
			_element = new String();
			ModPost post = new ModPost();
			System.out.println(url.construct() + "\n" + output.toString());

			for (int i = 0; i < list.getLength(); i++) {
				Node value = list.item(i).getChildNodes().item(0);
				_node = list.item(i).getNodeName();
				_element = value.getNodeValue();
				if (_node.equals("item")) {
					try {
						post.id = list.item(i).getAttributes().item(0)
								.getNodeValue();
					} catch (Exception e) {
					}
				} else if (_node.equals("date"))
					post.date = _element;
				else if (_node.equals("text"))
					post.text = FMLComments.replace(FMLComments.replace(_element, "&quot;", "\""), "&amp;", "&");
				if (post.isComplete()) {
					return post;
				}
			}
			conn.close();
		} catch (Exception e) {
		}

		return new ModPost();
	}

	private ModPost oldGetNextMod() {
		try {
			String s = (String) availableMods.elementAt(0);
			URL url = new URL("/mod/view/" + s);
			availableMods.removeElementAt(0);

			HttpConnection conn = (HttpConnection) Connector.open(url
					.construct());
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			copy(conn.openInputStream(), output);
			conn.close();
			ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
			
			String _node, _element;
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
					.newInstance();
			docBuilderFactory.setCoalescing(true);
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			docBuilder.isValidating();
			Document doc = docBuilder.parse(input);
			doc.getDocumentElement().normalize();
			NodeList list = doc.getElementsByTagName("*");
			_node = new String();
			_element = new String();
			ModPost post = new ModPost();
			System.out.println(url.construct() + "\n" + output.toString());

			for (int i = 0; i < list.getLength(); i++) {
				Node value = list.item(i).getChildNodes().item(0);
				_node = list.item(i).getNodeName();
				_element = value.getNodeValue();
				if (_node.equals("item")) {
					try {
						post.id = list.item(i).getAttributes().item(0)
								.getNodeValue();
					} catch (Exception e) {
					}
				} else if (_node.equals("date"))
					post.date = _element;
				else if (_node.equals("text"))
					post.text = FMLComments.replace(_element, "&quot;", "\"");
				if (post.isComplete()) {
					return post;
				}
			}
			conn.close();
		} catch (Exception e) {
		}

		return new ModPost();
	}

	public class ModPost {
		public String id;
		public String text;
		public String date;

		public boolean isComplete() {
			return id != null && text != null && date != null;
		}
	}

	public static void copy(InputStream inputStream, OutputStream outputStream)
			throws IOException {
		byte[] s_copyBuffer = new byte[65536];
		synchronized (s_copyBuffer) {
			for (int bytesRead = inputStream.read(s_copyBuffer); bytesRead >= 0; bytesRead = inputStream
					.read(s_copyBuffer))
				if (bytesRead > 0)
					outputStream.write(s_copyBuffer, 0, bytesRead);
		}
	}

}
