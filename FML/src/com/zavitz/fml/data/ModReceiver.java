package com.zavitz.fml.data;

import com.zavitz.fml.data.FMLModerate.ModPost;

public interface ModReceiver {
	
	public void modLoading();
	public void modReceived(ModPost mod);
	
}
