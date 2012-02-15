package com.zavitz.mytasks;

import java.io.InputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.media.Player;

import net.rim.device.api.util.Persistable;

public class Note implements Persistable {
	
	public static final int VOICE = 0x00A;
	public static final int DEFAULT = 0x00B;
	
	private int type;
	private String note, path;
	
	public Note() {
		type = DEFAULT;
	}
	
	public Note(int i) {
		type = i;
	}
	
	public Note(String s) {
		type = DEFAULT;
		note = s;
	}
	
	public Note(String s, int i) {
		type = i;
		switch(type) {
		case VOICE:
			path = s;
			break;
		default:
			note = s;
		}
	}
	
	public int getType() {
		return type;
	}
	
	public void set(String s) {
		switch(type) {
		case VOICE:
			path = s;
			break;
		default:
			note = s;
		}
	}
	
	public String getNote() {
		switch(type) {
		case VOICE:
			return parseName();
		default:
			return note;
		}
	}
	
	public String getTrueNote() {
		switch(type) {
		case VOICE:
			return "[VOICE]" + path;
		default:
			return note;
		}
	}
	
	public String getPath() {
		return path;
	}
	
	private String parseName() {
		String name = path.substring(path.lastIndexOf('/') + 1,path.length() - 4);
		String ret = name.substring(0,2) + "-" + name.substring(2,4) + "-" + name.substring(4,8) + " at " + name.substring(8,10) + ":" + name.substring(10,12);
		return ret;
	}
	
	public boolean play() throws Exception {
		FileConnection fconn = (FileConnection) Connector.open(path);
		if (!fconn.exists())
			return false;
		InputStream inputstream = fconn.openDataInputStream();
		Player player = javax.microedition.media.Manager.createPlayer(inputstream, "audio/mpeg");
		player.realize();
		player.prefetch();
		player.start();
		return true;
	}
	
}
