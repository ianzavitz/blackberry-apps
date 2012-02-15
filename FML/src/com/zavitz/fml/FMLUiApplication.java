package com.zavitz.fml;

import com.zavitz.fml.data.Config;
import com.zavitz.fml.data.PStore;

import net.rim.blackberry.api.homescreen.HomeScreen;
import net.rim.device.api.ui.UiApplication;

public class FMLUiApplication extends UiApplication {

	public FMLUiApplication() {
		if(!Config.REQUIRE_REGISTRATION || PStore.hasRegistered()) {
			PStore.load();
			pushScreen(new FMLMainScreen());
		} else {
			pushScreen(new FMLRegisterScreen());
		}
	}
	
	public static void main(String args[]) {
		new FMLUiApplication().enterEventDispatcher();
	}
	
	public void restart() {
		while(getScreenCount() > 0)
			popScreen(getActiveScreen());
		pushScreen(new FMLMainScreen());
	}
	
}
