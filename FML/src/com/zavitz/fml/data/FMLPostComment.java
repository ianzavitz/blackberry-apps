package com.zavitz.fml.data;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import com.zavitz.fml.FMLPostCommentScreen.LoadingField;
import com.zavitz.fml.data.FMLPost.Post;

public class FMLPostComment extends Thread {

	private LoadingField _loading;
	private PostCommentReceiver _receiver;
	private Post _post;
	private String _comment;
	
	public FMLPostComment(PostCommentReceiver receiver, LoadingField loading, Post post, String comment) {
		_receiver = receiver;
		_loading = loading;
		_post = post;
		_comment = comment;
		
		_receiver.commentPosting();
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
			
			URL url = new URL("/comment/");
			url.addVar("id", _post.id);
			url.addVar("text", _comment);
			url.addVar("url","");
			HttpConnection conn = (HttpConnection) Connector.open(url.construct());
			InputStream is = conn.openInputStream();
			final ByteArrayOutputStream os = new ByteArrayOutputStream();
			copy(is, os);
			conn.close();
			loadingTimer.cancel();
			_receiver.commentPosted(os.toString());
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
