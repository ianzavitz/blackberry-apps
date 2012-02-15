package com.zavitz.fml.data;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import com.zavitz.fml.FMLFavoritesScreen;

public class FMLFavorites extends Thread {
	
	private FMLFavoritesScreen screen;
	private boolean add;
	private String id;
	
	public FMLFavorites(FMLFavoritesScreen screen, boolean add, String id) {
		this.screen = screen;
		this.add = add;
		this.id = id;
		
		start();
	}

	public void run() {
		try {		
			screen.started();
			URL url = new URL("/account/favorites/" + (add ? "add" : "delete") + "/" + id + "/");
			HttpConnection conn = (HttpConnection) Connector.open(url.construct());
			InputStream is = conn.openInputStream();
			final ByteArrayOutputStream os = new ByteArrayOutputStream();
			copy(is, os);
			conn.close();
			screen.done();
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
