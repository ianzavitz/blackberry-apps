package com.zavitz.mytasks;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.media.Player;
import javax.microedition.media.PlayerListener;

import java.io.*;
import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.ui.container.*;

public class NoteScreen extends PopupScreen {

	String note;
	ButtonField play, close;
	Play playThread;

	public NoteScreen(String note) {
		super(new VerticalFieldManager());
		this.note = note;

		if (note.startsWith("VN:"))
			voiceNote();
		else
			textNote();
	}

	private void voiceNote() {
		HorizontalFieldManager buttons = new HorizontalFieldManager();

		play = new ButtonField("Play");
		play.setChangeListener(new FieldChangeListener() {
			public void fieldChanged(Field f, int instsance) {
				if (play.getLabel().equals("Play")) {
					playThread = new Play();
					playThread.start();
					play.setLabel("Stop");
				} else {
					playThread.stop();
					play.setLabel("Play");
				}
			}
		});
		ButtonField close = new ButtonField("Close");
		close.setChangeListener(new FieldChangeListener() {
			public void fieldChanged(Field f, int instance) {
				setDirty(false);
				if (playThread != null)
					playThread.stop();
				close();
			}
		});
		buttons.add(play);
		buttons.add(close);
		add(buttons);
	}

	private void textNote() {
		add(new RichTextField(note));
		ButtonField close = new ButtonField("Close");
		close.setChangeListener(new FieldChangeListener() {
			public void fieldChanged(Field f, int instance) {
				setDirty(false);
				close();
			}
		});
		add(close);
	}

	class Play extends Thread implements PlayerListener {

		Player player;

		public void run() {
			try {
				FileConnection fconn = (FileConnection) Connector.open(note
						.substring(3));
				if (!fconn.exists())
					return;
				InputStream inputstream = fconn.openInputStream();
				player = javax.microedition.media.Manager.createPlayer(
						inputstream, "audio/mpeg");
				player.addPlayerListener(this);
				player.realize();
				player.prefetch();
				player.start();
			} catch (Exception e) {
			}
		}

		public void stop() {
			try {
				player.stop();
				player.close();
			} catch (Exception e) {
			}
		}

		public void playerUpdate(Player player, String event, Object eventData) {
			if (event.equals("endOfMedia")) {
				synchronized (UiApplication.getEventLock()) {
					play.setLabel("Play");
				}
			}
		}

	}

}
