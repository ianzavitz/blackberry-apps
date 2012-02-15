package com.zavitz.fml.data;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.LabelField;

public class FMLVote extends Thread {

	private LabelField _receiver;
	private String _id;
	private boolean _fyl;
	private String _follow;

	public FMLVote(LabelField receiver, String id, boolean fyl, String follow) {
		_receiver = receiver;
		_id = id;
		_fyl = fyl;
		_follow = follow;

		start();
	}

	public void run() {
		try {
			UiApplication.getUiApplication().invokeAndWait(new Runnable() {
				public void run() {
					_receiver.setText("...");
				}
			});
			URL url = new URL("/vote/" + _id + "/"
					+ (_fyl ? "agree" : "deserved") + "/");
			HttpConnection conn = (HttpConnection) Connector.open(url.construct());
			InputStream is = conn.openInputStream();
			final ByteArrayOutputStream os = new ByteArrayOutputStream();
			copy(is, os);
			conn.close();
			UiApplication.getUiApplication().invokeAndWait(new Runnable() {
				public void run() {
					_receiver.setText(_follow);
				}
			});
		} catch(Exception e) { }
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
