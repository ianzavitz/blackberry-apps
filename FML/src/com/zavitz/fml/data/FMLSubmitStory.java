package com.zavitz.fml.data;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import com.zavitz.fml.FMLSubmitScreen.LoadingField;

public class FMLSubmitStory extends Thread {

	private LoadingField _loading;
	private SubmitReceiver _receiver;
	private String _author, _category, _text;
	
	public FMLSubmitStory(SubmitReceiver receiver, LoadingField loading, String author, String category, String text) {
		_receiver = receiver;
		_loading = loading;
		_author = author;
		_category = category;
		_text = text;
		
		_receiver.submitLoading();
		start();
	}

	public void run() {
		try {
			Timer loadingTimer = new Timer();
			TimerTask animate = new TimerTask() {
				public void run() {
					_loading.animate();
				}
			};
			loadingTimer.scheduleAtFixedRate(animate, 1000 / 16, 1000 / 16);
			
			URL url = new URL("/submit/");
			url.addVar("author", _author);
			url.addVar("cat", _category);
			url.addVar("text",_text);
			HttpConnection conn = (HttpConnection) Connector.open(url.construct());
			InputStream is = conn.openInputStream();
			final ByteArrayOutputStream os = new ByteArrayOutputStream();
			copy(is, os);
			conn.close();
			loadingTimer.cancel();
			_receiver.submitSubmitted();
		} catch(final Exception e) { 
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