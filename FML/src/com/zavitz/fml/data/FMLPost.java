package com.zavitz.fml.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.xml.parsers.*;

import org.w3c.dom.*;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class FMLPost extends Thread {

	private PostReceiver _receiver;
	private String _category;
	private int _page;
	private static long failedTimeout = 3000;
	
	public FMLPost(PostReceiver receiver, String category, int page) {
		_receiver = receiver;
		_category = category;
		_page = page;

		if (_category.equals(""))
			_category = "last";

		start();
	}

	public void run() {
		_receiver.postsLoading();
		Vector v = getAnecdotes();
		if(v == null) {
			_receiver.postsFailed(failedTimeout);
			failedTimeout += 3000;
		} else {
			failedTimeout = 3000;
			_receiver.postsReceived(v);
		}
	}

	private Vector getAnecdotes() {
		Vector v = new Vector();
		String request = _category + "/" + (_page <= 0 ? "" : "" + _page);
		if (request.startsWith("search="))
			request = "search";

		final URL url = new URL("/view/" + request);

		if (_category.startsWith("search="))
			url.addVar("search", _category.substring(7));

		try {
			HttpConnection conn = (HttpConnection) Connector.open(url.construct());
			if(conn.getResponseCode() != HttpConnection.HTTP_OK)
				return null;
			ByteArrayOutputStream output = new ByteArrayOutputStream();
				copy(conn.openInputStream(), output);
				conn.close();
			ByteArrayInputStream input = new ByteArrayInputStream(output
					.toByteArray());
			
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
					.newInstance();
			docBuilderFactory.setCoalescing(true);
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			docBuilder.isValidating();
			Document doc = docBuilder.parse(input);
			doc.getDocumentElement().normalize();
			NodeList list = doc.getElementsByTagName("*");
			
			Post post = null;
			for (int i = 0; i < list.getLength(); i++) {
				String _node = list.item(i).getNodeName();
				String _element = "null";
				if (list.item(i).hasChildNodes()) {
					_element = list.item(i).getChildNodes().item(0)
							.getNodeValue();
				}
				if (_node.equals("item")) {
					post = new Post();
					post.id = list.item(i).getAttributes().item(0)
							.getNodeValue();
				}
				if (post != null) {
					if (_node.equals("author"))
						post.author = _element;
					if (_node.equals("category"))
						post.category = _element;
					if (_node.equals("date"))
						post.date = _element;
					if (_node.equals("agree"))
						post.agree = _element.equals("null") ? String.valueOf(0) : _element;
					if (_node.equals("deserved"))
						post.deserve = _element.equals("null") ? String.valueOf(0) : _element;
					if (_node.equals("comments"))
						post.comments = _element.equals("null") ? String.valueOf(0) : _element;
					if (_node.equals("text"))
						post.text = _element;
					if(post.isComplete()) {
						v.addElement(post);
						post = null;
					}
				}
			}
		} catch(IOException e) {
			UiApplication.getUiApplication().invokeAndWait(new Runnable() {
				public void run() {
				//Dialog.inform("Connector");
				}
			});return null;
		} catch (SAXParseException e) {
			UiApplication.getUiApplication().invokeAndWait(new Runnable() {
				public void run() {
					//Dialog.inform("SAXPARSEEXCEPTION");
				}
			});return null;
		} catch (SAXException e) {
			UiApplication.getUiApplication().invokeAndWait(new Runnable() {
				public void run() {
					//Dialog.inform("SAXEXCEPTION");
				}
			});return null;
		} catch (ParserConfigurationException e) {
			UiApplication.getUiApplication().invokeAndWait(new Runnable() {
				public void run() {
					//Dialog.inform("ParserConfigurationException");
				}
			});return null;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return v;
	}

	public class Post {
		public String id;
		public String author;
		public String category;
		public String date;
		public String agree;
		public String deserve;
		public String comments;
		public String text;
		public boolean agreed = false;
		public boolean deserved = false;

		public boolean isComplete() {
			return id != null && author != null && category != null
					&& date != null && agree != null && deserve != null
					&& comments != null && text != null;
		}

		public Post duplicate() {
			Post dupe = new Post();
			dupe.id = new String(id);
			dupe.author = new String(author);
			dupe.category = new String(category);
			dupe.date = new String(date);
			dupe.agree = new String(agree);
			dupe.deserve = new String(deserve);
			dupe.comments = new String(comments);
			dupe.text = new String(text);
			return dupe;
		}
	}

	public static void copy(InputStream inputStream, OutputStream outputStream)
			throws Exception {
		byte[] s_copyBuffer = new byte[65536];
		synchronized (s_copyBuffer) {
			for (int bytesRead = inputStream.read(s_copyBuffer); bytesRead >= 0; bytesRead = inputStream
					.read(s_copyBuffer))
				if (bytesRead > 0)
					outputStream.write(s_copyBuffer, 0, bytesRead);
		}
	}

}
