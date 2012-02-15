package com.zavitz.fml.data;

import java.util.Vector;

public interface PostReceiver {

	public void postsReceived(Vector posts);
	public void postsLoading();
	public void postsFailed(long timeout);
	
}
