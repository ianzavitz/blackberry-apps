package com.zavitz.mytasks.functions;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.media.PlayerListener;
import javax.microedition.media.control.RecordControl;

import net.rim.device.api.i18n.SimpleDateFormat;

public class NoteUtils extends Thread implements PlayerListener {

	Player player;
	RecordControl rcontrol;
	ByteArrayOutputStream output = new ByteArrayOutputStream();
	boolean recording;
	String savedTo;
	byte[] _data;

	public boolean play(String s) {
		try {
			FileConnection fconn = (FileConnection) Connector.open(s);
			if (!fconn.exists())
				return false;
			InputStream inputstream = fconn.openInputStream();
			player = javax.microedition.media.Manager.createPlayer(inputstream,
					"audio/mpeg");
			player.addPlayerListener(this);
			player.realize();
			player.prefetch();
			player.start();
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	public void stopPlay() {
		try {
			player.stop();
		} catch (MediaException e) { }
	}

	public void playerUpdate(Player player, String event, Object eventData) {
		if (event.equals("endOfMedia")) {
			System.out.println("End.");
		}
	}

	public void reset() {
		try {
			output.reset();
			recording = false;
		} catch (Exception e) {
		}
	}

	public String getSavedTo() {
		return savedTo;
	}

	public boolean save() {
		try {
			savedTo = System.getProperty("fileconn.dir.tones")
					+ new SimpleDateFormat("MMddyyyyHHmmss").formatLocal(System
							.currentTimeMillis()) + ".amr";
			FileConnection fconn = (FileConnection) Connector.open(savedTo);
			if (!fconn.exists())
				fconn.create();

			OutputStream pushData = fconn.openDataOutputStream();
			pushData.write(_data);
			pushData.flush();
			pushData.close();
			return true;
		} catch (Exception e) {
		}
		return false;
	}

	public void playBack() {
		new Thread(new Runnable() {
			public void run() {
				try {
					InputStream inputstream = new ByteArrayInputStream(_data);
					Player player = javax.microedition.media.Manager
							.createPlayer(inputstream, "audio/mpeg");
					player.realize();
					player.prefetch();
					player.start();
				} catch (Exception e) {
				}
			}
		});
	}

	public void run() {
		try {
			output = new ByteArrayOutputStream();
			player = Manager.createPlayer("capture://audio?encoding=amr");
			player.realize();
			rcontrol = (RecordControl) player.getControl("RecordControl");
			rcontrol.setRecordStream(output);
			rcontrol.startRecord();
			player.start();
		} catch (Exception e) {
		}
	}

	public void stop() {
		try {
			recording = false;
			rcontrol.commit();
			_data = output.toByteArray();
			output.close();
			player.stop();
			player.close();
		} catch (Exception e) {
		}
	}

}
