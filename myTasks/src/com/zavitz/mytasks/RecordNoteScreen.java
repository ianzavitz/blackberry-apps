package com.zavitz.mytasks;

import java.io.*;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.media.Manager;
import javax.microedition.media.Player;
import javax.microedition.media.PlayerListener;
import javax.microedition.media.control.RecordControl;

import com.zavitz.mytasks.functions.*;
import net.rim.device.api.system.Characters;
import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.ui.container.*;

public class RecordNoteScreen extends PopupScreen {

	HorizontalFieldManager commands_1 = new HorizontalFieldManager( Field.FIELD_HCENTER
			| ButtonField.CONSUME_CLICK | DrawStyle.HCENTER);
	HorizontalFieldManager commands_2 = new HorizontalFieldManager( Field.FIELD_HCENTER
			| ButtonField.CONSUME_CLICK | DrawStyle.HCENTER);
	ButtonField record, stop, playback, save, close;
	Record recordThread;
	Play play;
	TaskScreen taskScreen;

	public RecordNoteScreen(TaskScreen callback) {
		super(new VerticalFieldManager());
		
		taskScreen = callback;

		record = new ButtonField("Record",  Field.FIELD_HCENTER
				| ButtonField.CONSUME_CLICK | DrawStyle.HCENTER);
		record.setChangeListener(new FieldChangeListener() {
			public void fieldChanged(Field field, int instance) {
				recordThread = new Record();
				recordThread.start();

				setMenu(new ButtonField[][] { {stop}, {close} });
			}
		});

		stop = new ButtonField("Stop",  Field.FIELD_HCENTER
				| ButtonField.CONSUME_CLICK | DrawStyle.HCENTER);
		stop.setChangeListener(new FieldChangeListener() {
			public void fieldChanged(Field field, int instance) {
				recordThread.stop();

				setMenu(new ButtonField[][] { {record, playback} , {save, close} });
			}
		});

		playback = new ButtonField("Playback",  Field.FIELD_HCENTER
				| ButtonField.CONSUME_CLICK | DrawStyle.HCENTER);
		playback.setChangeListener(new FieldChangeListener() {
			public void fieldChanged(Field field, int instance) {
				if (playback.getLabel().equals("Playback")) {
					play = new Play(recordThread.getBytes());
					play.start();
					playback.setLabel("Stop");
				} else {
					play.stop();
					playback.setLabel("Playback");
				}
			}
		});

		save = new ButtonField("Save",  Field.FIELD_HCENTER
				| ButtonField.CONSUME_CLICK | DrawStyle.HCENTER);
		save.setChangeListener(new FieldChangeListener() {
			public void fieldChanged(Field field, int instance) {
				String savePath = recordThread.save();
				System.out.println("Saving to : " + savePath);
				taskScreen.getTask().addNote("VN:" + savePath);
				taskScreen.notes.add("VN:" + savePath);
				com.zavitz.mytasks.functions.PersistentUtils.save();
				setDirty(false);
				close();
			}
		});

		close = new ButtonField("Close",  Field.FIELD_HCENTER
				| ButtonField.CONSUME_CLICK | DrawStyle.HCENTER);
		close.setChangeListener(new FieldChangeListener() {
			public void fieldChanged(Field field, int instance) {
				setDirty(false);
				close();
			}
		});

		commands_1.add(record);
		commands_2.add(close);
		add(commands_1);
		add(commands_2);
	}

	public void setMenu(final ButtonField[][] buttons) {
		UiApplication.getUiApplication().invokeAndWait(new Runnable() {
			public void run() {
				commands_1.deleteAll();
				for (int i = 0; i < buttons[0].length; i++)
					commands_1.add(buttons[0][i]);
				commands_2.deleteAll();
				for (int i = 0; i < buttons[1].length; i++)
					commands_2.add(buttons[1][i]);
			}
		});
	}

	public boolean keyChar(char key, int status, int time) {
		boolean retval = false;
		switch (key) {
		case Characters.ESCAPE:
			close();
			break;
		default:
			retval = super.keyChar(key, status, time);
		}
		return retval;
	}

	class Play extends Thread implements PlayerListener {

		Player player;
		byte[] _data;

		public Play(byte[] data) {
			_data = data;
		}

		public void run() {
			try {
				InputStream inputstream = new ByteArrayInputStream(_data);
				player = javax.microedition.media.Manager.createPlayer(
						inputstream, "audio/mpeg");
				player.realize();
				player.prefetch();
				player.start();
			} catch (Exception e) {
				System.out.println("ERRRRRRRRRROR: " + e.getMessage());
			}
		}

		public void stop() {
			System.out.println("MYTAKS: STOPPPIE");
			try {
				player.stop();
				player.close();
			} catch (Exception e) {
				System.out.println("ERRRRRRRRRROR: " + e.getMessage());
			}
		}

		public void playerUpdate(Player player, String event, Object eventData) {
			if(event.equals("endOfMedia")) {
				playback.setLabel("Playback");
			}
		}

	}

	class Record extends Thread {

		   private Player _player;
		   private RecordControl _rcontrol;
		   private ByteArrayOutputStream _output;
		   private byte _data[];

		   public void run() {
		      try {
		          // Create a Player that captures live audio.
		          _player = Manager.createPlayer("capture://audio");
		          _player.realize();

		          // Get the RecordControl, set the record stream,
		          _rcontrol = (RecordControl)_player.getControl("RecordControl");

		          //Create a ByteArrayOutputStream to capture the audio stream.
		          _output = new ByteArrayOutputStream();
		          _rcontrol.setRecordStream(_output);
		          _rcontrol.startRecord();
		          _player.start();

		      } catch (final Exception e) {
		         UiApplication.getUiApplication().invokeAndWait(new Runnable() {
		            public void run() {
		               Dialog.inform(e.toString());
		            }
		         });
		      }
		   }

		   public void stop() {
		      try {
		           //Stop recording, capture data from the OutputStream,
		           //close the OutputStream and player.
		           _rcontrol.commit();
		           _data = _output.toByteArray();
		           _output.close();
		           _player.close();

		      } catch (Exception e) {
		         synchronized (UiApplication.getEventLock()) {
		            Dialog.inform(e.toString());
		         }
		      }
		   }

		public String save() {
			String savedTo = "";
			try {
				savedTo = System.getProperty("fileconn.dir.tones")
						+ CalUtils.format("MMddyyyyHHmmss") + ".amr";
				FileConnection fconn = (FileConnection) Connector.open(savedTo);
				if (!fconn.exists())
					fconn.create();

				OutputStream pushData = fconn.openDataOutputStream();
				pushData.write(_data);
				pushData.flush();
				pushData.close();
				return savedTo;
			} catch (final Exception e) {
			}
			return null;
		}

		public byte[] getBytes() {
			return _data;
		}

	}

}
