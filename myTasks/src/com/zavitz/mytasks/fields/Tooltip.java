package com.zavitz.mytasks.fields;

import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiEngine;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.container.PopupScreen;

public class Tooltip extends PopupScreen {
	int _x;
	int _y;
	TooltipThread _tooltipThread;

	private Tooltip(Manager manager) {
		super(manager);
	}

	public void sublayout(int width, int height) {
		super.sublayout(width, height);
		setPosition(_x, _y);
		System.out.println("Tooltip x: " + Integer.toString(_x) + ", y: "
				+ Integer.toString(_y));
	}

	protected void applyTheme() {
		// Overriden to suppress Border etc.
	}

	public void removeToolTip() {
		if (_tooltipThread != null) {
			_tooltipThread.dismiss();
		}
	}

	private void display(UiApplication uiApp, int x, int y, int displayTime) {
		_x = x;
		_y = y;
		_tooltipThread = new TooltipThread(uiApp, this, displayTime);
		_tooltipThread.start();
	}

	public static Tooltip addToolTip(UiApplication uiApp,
			String toolTipString, int x, int y, int displayTime) {
		VerticalFieldManager manager = new VerticalFieldManager(
				Manager.FIELD_VCENTER | Manager.NON_FOCUSABLE) {
			protected void paint(Graphics graphics) {
				graphics.setColor(0x00FFFFE0); // White
				graphics.fillRect(0, 0, getWidth(), getHeight());
				graphics.setColor(0x00000000); // Black
				graphics.drawRect(0, 0, getWidth(), getHeight());
				super.paint(graphics);
			}
		};
		Tooltip toolTip = new Tooltip(manager);
		LabelField label = new LabelField(' ' + toolTipString + ' ',
				LabelField.NON_FOCUSABLE);
		label.setFont(Font.getDefault().derive(Font.PLAIN, 16));
		toolTip.add(label);
		toolTip.display(uiApp, x, y, displayTime);
		return toolTip;
	}

	class TooltipThread extends Thread {

		Object _notifyObject = new Object(); // Used to allow user to dismiss
												// this Tooltip
		PopupScreen _tooltip; // Screen we are going to display
		UiApplication _ourApplication; // access to pushGlobalScreen and
										// dismissStatus from our Application
		int _displayTime; // in seconds

		public TooltipThread(UiApplication ourApplication, PopupScreen tooltip,
				int displayTime) {
			_tooltip = tooltip;
			_ourApplication = ourApplication;
			_displayTime = displayTime;
		}

		public void run() {
			_ourApplication.pushGlobalScreen(_tooltip, 999, UiEngine.GLOBAL_MODAL);
			synchronized (_notifyObject) {
				try {
					_notifyObject.wait(_displayTime * 1000);
				} catch (Exception e) {
				}
			}
			;
			_ourApplication.popScreen(_tooltip);
		}

		public void dismiss() {
			// notify the waiting object to stop the Thread waiting
			synchronized (_notifyObject) {
				_notifyObject.notify();
			}
		}

	}

}