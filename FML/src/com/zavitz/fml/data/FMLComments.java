package com.zavitz.fml.data;

import java.util.Vector;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import net.rim.device.api.xml.parsers.*;
import org.w3c.dom.*;

import com.zavitz.fml.data.FMLPost.Post;

public class FMLComments extends Thread {

	private CommentReceiver _receiver;
	private Post _post;

	public FMLComments(CommentReceiver receiver, Post post) {
		_receiver = receiver;
		_post = post;
		
		start();
	}

	public void run() {
		_receiver.commentsLoading();
		_receiver.commentsReceived(getComments());
	}

	private Vector getComments() {
		Vector v = new Vector();

		try {
			URL url = new URL("/view/" + _post.id);
			HttpConnection conn = (HttpConnection) Connector.open(url.construct());

			conn.setRequestMethod(HttpConnection.GET);
			String _node, _element;
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			docBuilderFactory.setCoalescing(true);
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			docBuilder.isValidating();
			Document doc = docBuilder.parse(conn.openInputStream());
			doc.getDocumentElement().normalize();
			NodeList list = doc.getElementsByTagName("*");
			_node = new String();
			_element = new String();
			Comment comment = new Comment();
			
			boolean flag = false;

			for (int i = 0; i < list.getLength(); i++) {
				Node value = list.item(i).getChildNodes().item(0);
				_node = list.item(i).getNodeName();
				_element = value.getNodeValue();
				if(list.item(i).getLocalName().equals("comment"))
					flag = true;
				if(flag) {
					if (_node.equals("comment")) {
						try {
							NamedNodeMap map = list.item(i).getAttributes();
							Node node = map.getNamedItem("pub_id");
							String s = node.getNodeValue();
							comment.id = s;
						} catch(Exception e) { }
					} else if (_node.equals("author"))
						comment.author = (_element);
					else if (_node.equals("date"))
						comment.date = (_element);
					else if (_node.equals("text"))
						comment.text = replace(replace(_element,(char)13,' '),"\n"," ");
					if (comment.isComplete()) {
						v.addElement(comment.duplicate());
						comment = new Comment();
					}
				}
			}
			conn.close();
		} catch (Exception e) {
		}
		return v;
	}
	
	public static String replace(String text, String searchString,
			String replacementString) {
		StringBuffer sBuffer = new StringBuffer();
		int pos = 0;

		while ((pos = text.indexOf(searchString)) != -1) {
			sBuffer.append(text.substring(0, pos) + replacementString);
			text = text.substring(pos + searchString.length());
		}

		sBuffer.append(text);
		return sBuffer.toString();
	}
	
	public static String replace(String text, char search,
			char replacement) {
		StringBuffer sBuffer = new StringBuffer();
		int pos = 0;

		while ((pos = text.indexOf(search)) != -1) {
			char[] characters = text.toCharArray();
			characters[pos] = replacement;
			text = new String(characters);
		}

		sBuffer.append(text);
		return sBuffer.toString();
	}
	
	public class Comment {
		public String id;
		public String author;
		public String date;
		public String text;
		public boolean isComplete() { return id != null && author != null && date != null && text != null; }
		public Comment duplicate() { Comment c = new Comment(); c.id = new String(id); c.author = new String(author); c.date = new String(date); c.text = new String(text); return c; }
	}

}
