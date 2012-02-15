package com.zavitz.fml.data;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import com.zavitz.fml.FMLLoginScreen;
import com.zavitz.fml.FMLLoginScreen.LoadingField;

public class FMLLogin extends Thread {

	private LoadingField _receiver;
	private String _username, _password;
	private FMLLoginScreen _screen;
	
	public FMLLogin(FMLLoginScreen screen, LoadingField receiver, String username, String password) {
		_screen = screen;
		_receiver = receiver;
		_username = username;
		_password = password;

		start();
	}

	public void run() {
		try {
			Timer loadingTimer = new Timer();
			TimerTask animate = new TimerTask() {
				public void run() {
					_receiver.animate();
				}
			};
			loadingTimer.scheduleAtFixedRate(animate, 1000 / 16, 1000 / 16);
			
			URL url = new URL("/account/login/" + _username + "/" + MD5.get(_password));
			HttpConnection conn = (HttpConnection) Connector.open(url.construct());
			InputStream is = conn.openInputStream();
			final ByteArrayOutputStream os = new ByteArrayOutputStream();
			copy(is, os);
			conn.close();
			loadingTimer.cancel();
			_screen.response(os.toString());
		} catch(final Exception e) { 
			UiApplication.getUiApplication().invokeAndWait(new Runnable() {
				public void run() {
					Dialog.inform(e.toString());
				}
			});
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
