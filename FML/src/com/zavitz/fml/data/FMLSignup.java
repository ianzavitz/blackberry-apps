package com.zavitz.fml.data;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

import com.zavitz.fml.FMLSignupScreen.LoadingField;

public class FMLSignup extends Thread {

	private SignupReceiver _receiver;
	private LoadingField _loadingField;
	private String _username, _password, _email;

	public FMLSignup(SignupReceiver receiver, LoadingField loadingField,
			String username, String password, String email) {
		_receiver = receiver;
		_loadingField = loadingField;
		_username = username;
		_password = password;
		_email = email;

		start();
	}

	public void run() {
		_receiver.signupStarting();
		try {
			Timer loadingTimer = new Timer();
			TimerTask animate = new TimerTask() {
				public void run() {
					_loadingField.animate();
				}
			};
			loadingTimer.scheduleAtFixedRate(animate, 1000 / 16, 1000 / 16);

			URL url = new URL("/account/signup");
			url.addVar("mail", _email);
			url.addVar("login", _username);
			url.addVar("pass", _password);
			HttpConnection conn = (HttpConnection) Connector.open(url.construct());
			InputStream is = conn.openInputStream();
			final ByteArrayOutputStream os = new ByteArrayOutputStream();
			copy(is, os);
			conn.close();
			_receiver.signupComplete(os.toString());
			loadingTimer.cancel();
		} catch (final Exception e) { 		}
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
