package com.zavitz.mytasks;

import java.util.Timer;
import java.util.TimerTask;

import com.zavitz.mytasks.functions.PersistentUtils;

import net.rim.device.api.applicationcontrol.ApplicationPermissions;
import net.rim.device.api.applicationcontrol.ApplicationPermissionsManager;
import net.rim.device.api.i18n.DateFormat;
import net.rim.device.api.i18n.SimpleDateFormat;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;

public class MTUiApplication extends UiApplication {

	public static final String VERSION = "2.0.0";
	static final boolean TRIAL = false;

	public MTUiApplication() {
		long validUntil = PersistentUtils.trialValid();
		if (!TRIAL || validUntil >= 0) {
			setPermissions();
			pushScreen(new MTScreen());
			if (TRIAL) {
				final PopupScreen popupScreen = new PopupScreen(
						new VerticalFieldManager());
				popupScreen.add(new RichTextField("Trial will expire: "
						+ new SimpleDateFormat(DateFormat.DATETIME_DEFAULT)
								.formatLocal(validUntil)));
				pushScreen(popupScreen);
				Timer timer = new Timer();
				TimerTask task = new TimerTask() {
					public void run() {
						synchronized (UiApplication.getEventLock()) {
							popupScreen.close();
						}
					}
				};
				timer.schedule(task, 5000);
			}
		} else {
			final PopupScreen popupScreen = new PopupScreen(
					new VerticalFieldManager());
			popupScreen
					.add(new RichTextField(
							"Trial has expired. Purchase myTasks on any MobiHand affiliate store or via BlackBerry App World"));
			pushScreen(popupScreen);
			Timer timer = new Timer();
			TimerTask task = new TimerTask() {
				public void run() {
					synchronized (UiApplication.getEventLock()) {
						popupScreen.close();
					}
				}
			};
			timer.schedule(task, 5000);
		}
	}

	public void setPermissions() {
		try {
			ApplicationPermissionsManager permissionsManager = ApplicationPermissionsManager
					.getInstance();
			ApplicationPermissions permissions = new ApplicationPermissions();
			boolean request = false;
			if (permissionsManager.getPermission(17) == ApplicationPermissions.VALUE_DENY) {
				permissions.addPermission(17);
				request = true;
			}
			if (permissionsManager.getPermission(16) == ApplicationPermissions.VALUE_DENY) {
				permissions.addPermission(16);
				request = true;
			}
			if (request) {
				boolean response = permissionsManager
						.invokePermissionsRequest(permissions);
				while (!response)
					response = permissionsManager
							.invokePermissionsRequest(permissions);
			}
		} catch (IllegalArgumentException e) {

		}
	}

	public static void main(String[] args) {
		new MTUiApplication().enterEventDispatcher();
	}

}
