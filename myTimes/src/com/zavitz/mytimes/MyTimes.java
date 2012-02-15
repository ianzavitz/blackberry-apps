package com.zavitz.mytimes;

import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.ui.UiApplication;

public class MyTimes extends UiApplication {

	public MyTimes() {
		new ScreenSaverThread().start();
		pushScreen(new HomeScreen());
	}

	public static void main(String args[]) {
		Options.loadOptions();
		if (ApplicationManager.getApplicationManager().inStartup()) {
			if (Options.SCREENSAVER_TIMEOUT == 0) {
				System.exit(0);
			}
		}
		MyTimes myTimes = new MyTimes();
		myTimes.enterEventDispatcher();

	}

	private MyTimes getInstance() {
		return this;
	}

	private class ScreenSaverThread extends Thread implements Runnable {

		public void run() {
			long startTime = System.currentTimeMillis();
			while (true) {
				try {
					Thread.sleep(1000);
					if (Options.SCREENSAVER_TIMEOUT > 0) {
						if (DeviceInfo.getIdleTime() > Options.SCREENSAVER_TIMEOUT
								&& System.currentTimeMillis() - startTime > Options.SCREENSAVER_TIMEOUT) {
							synchronized (UiApplication.getEventLock()) {
								if (!Screensaver.getInstance().isShowing()) {
									UiApplication.getUiApplication().pushGlobalScreen(Screensaver
											.getInstance(), 1,
											UiApplication.GLOBAL_QUEUE);
									Screensaver.getInstance().pushed();
								}
							}
						}
					}
				} catch (Exception e) {
				}
			}
		}

	}

}
