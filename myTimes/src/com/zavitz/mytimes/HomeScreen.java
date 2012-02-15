package com.zavitz.mytimes;

import java.util.*;

import net.rim.device.api.system.*;
import net.rim.device.api.ui.*;
import net.rim.device.api.ui.container.*;
import net.rim.device.api.ui.component.*;

public class HomeScreen extends MainScreen {

	public static Vector timeZones;
	private Vector timeZoneFields;
	private TimerTask timerTask;
	private VerticalFieldManager _container;
	public static Bitmap worldMap;

	public HomeScreen() {
		VerticalFieldManager internalManager = new VerticalFieldManager(
				Manager.NO_VERTICAL_SCROLL | Manager.NO_VERTICAL_SCROLLBAR) {
			public void paintBackground(Graphics g) {
				g.clear();
				int color = g.getColor();
				g.setColor(0x0);
				g.fillRect(0, 0, Display.getWidth(), Display.getHeight());

				if (HomeScreen.worldMap == null)
					HomeScreen.worldMap = Images.getScaledImage("worldmap.png",
							Display.getWidth());

				int x = (Display.getWidth() - worldMap.getWidth()) / 2;
				if (x < 0)
					x = 0;
				int y = (Display.getHeight() - worldMap.getHeight()) / 2;
				if (y < 0)
					y = 0;

				g.drawBitmap(x, y, worldMap.getWidth(), worldMap.getHeight(),
						worldMap, 0, 0);

				g.setColor(color);
			}

			protected void sublayout(int width, int height) {
				super.sublayout(Display.getWidth(), Display.getHeight());
				setExtent(Display.getWidth(), Display.getHeight());
			}
		};

		TitleField title = new TitleField();
		internalManager.add(title);

		_container = new VerticalFieldManager(Manager.VERTICAL_SCROLL
				| Manager.VERTICAL_SCROLLBAR);
		internalManager.add(_container);
		super.add(internalManager);

		timeZones = PersistentUtils.getTimeZones();
		timeZoneFields = new Vector();

		for (int i = 0; i < timeZones.size(); i++) {
			TimeField newField = new TimeField(timeZones.elementAt(i));
			add(newField);
			timeZoneFields.addElement(newField);
		}

		Timer timer = new Timer();
		timerTask = new TimerTask() {
			public void run() {
				for (int i = 0; i < timeZoneFields.size(); i++)
					((TimeField) timeZoneFields.elementAt(i)).invalidate();
			}
		};
		timer.scheduleAtFixedRate(timerTask, 1000, 1000);
	}

	public void close() {
		if (Options.SCREENSAVER_TIMEOUT == 0) {
			setDirty(false);
			super.close();
			return;
		}
		switch (Dialog.ask("Closing stops the screensaver", new String[] {
				"Close", "Hide", "Cancel" }, 1)) {
		case 0:
			setDirty(false);
			super.close();
			break;
		case 1:
			onClose();
			break;
		case 2:
			return;
		}
	}

	public boolean onClose() {
		UiApplication.getUiApplication().requestBackground();
		return false;
	}

	public boolean keyChar(char key, int status, int time) {
		if (key == Characters.BACKSPACE
				&& _container.getFieldWithFocus() instanceof TimeField) {
			if (Dialog.ask(Dialog.D_YES_NO, "Confirm delete?", Dialog.YES) == Dialog.YES)
				removeItem((TimeField) _container.getFieldWithFocus());
			return true;
		} else if (key == 't' && timeZoneFields.size() > 0) {
			((TimeField) timeZoneFields.elementAt(0)).setFocus();
			return true;
		} else if (key == 'b' && timeZoneFields.size() > 1) {
			((TimeField) timeZoneFields.elementAt(timeZoneFields.size() - 1))
					.setFocus();
			return true;
		}
		return super.keyChar(key, status, time);
	}

	public void addItem(String s) {
		if (timeZones.contains(s))
			return;
		TimeField newField = new TimeField(s);
		add(newField);
		timeZones.addElement(s);
		timeZoneFields.addElement(newField);
		PersistentUtils.save();
	}

	public void addCustom(TZone zone) {
		TimeField newField = new TimeField(zone);
		add(newField);
		timeZones.addElement(zone);
		timeZoneFields.addElement(newField);
		PersistentUtils.save();
	}

	public void removeItem(TimeField field) {
		_container.delete(field);
		if (field.getType() == TimeField.NORMAL) {
			while (timeZones.contains(field.getZone()))
				timeZones.removeElement(field.getZone());
		} else {
			while (timeZones.contains(field.getTZone()))
				timeZones.removeElement(field.getTZone());
		}
		timeZoneFields.removeElement(field);

		PersistentUtils.save();
	}

	public void update() {
		for (int i = 0; i < timeZones.size(); i++) {
			TimeField newField = timeZones.elementAt(i) instanceof String ? new TimeField(
					(String) timeZones.elementAt(i))
					: new TimeField((TZone) timeZones.elementAt(i));
			add(newField);
			timeZoneFields.addElement(newField);
		}

		PersistentUtils.save();
	}

	public void update(Object o) {
		for (int i = 0; i < timeZones.size(); i++) {
			TimeField newField = timeZones.elementAt(i) instanceof String ? new TimeField(
					(String) timeZones.elementAt(i))
					: new TimeField((TZone) timeZones.elementAt(i));
			add(newField);
			if (newField.getObject().equals(o))
				newField.setFocus();
			timeZoneFields.addElement(newField);
		}

		PersistentUtils.save();
	}

	public void add(Field field) {
		_container.add(field);
	}

	public HomeScreen getInstance() {
		return this;
	}

	private MenuItem addTimeZone = new MenuItem("Add Zone", 110, 1) {
		public void run() {
			UiApplication.getUiApplication().pushScreen(new ChoiceScreen());
		}
	};

	private MenuItem removeTimeZone = new MenuItem("Delete Zone", 110, 1) {
		public void run() {
			if (Dialog.ask(Dialog.D_YES_NO, "Confirm delete?", Dialog.YES) == Dialog.YES)
				removeItem((TimeField) _container.getFieldWithFocus());
		}
	};

	private MenuItem moveTimeZone = new MenuItem("Move Zone", 110, 1) {
		public void run() {
			UiApplication.getUiApplication()
					.pushScreen(
							new MovePopup(((TimeField) getLeafFieldWithFocus())));
		}
	};

	private MenuItem options = new MenuItem("Options", 110, 1) {
		public void run() {
			UiApplication.getUiApplication().pushScreen(new OptionsScreen());
		}
	};

	public void makeMenu(Menu menu, int context) {
		menu.add(addTimeZone);
		if (getLeafFieldWithFocus() instanceof TimeField) {
			menu.add(removeTimeZone);
			menu.add(moveTimeZone);
		}
		menu.addSeparator();
		menu.add(options);
		menu.addSeparator();
		menu.add(new MenuItem("Close", 110, 1) {
			public void run() {
				close();
			}
		});
	}

	public void moveField(TimeField field, int move) {
		int pos = timeZones.indexOf(field.getObject()) + move;
		if (pos > -1 && pos < timeZones.size()) {
			timeZones.removeElement(field.getObject());
			timeZones.insertElementAt(field.getObject(), pos);
			while (!timeZoneFields.isEmpty()) {
				_container.delete((TimeField) timeZoneFields.elementAt(0));
				timeZoneFields.removeElementAt(0);
			}
			update(field.getObject());
		}
	}

	class MovePopup extends PopupScreen implements FieldChangeListener {

		public void sublayout(int width, int height) {
			super.sublayout(width, height);
			setPosition(Display.getWidth() - getWidth(), Display.getHeight()
					- getHeight());
		}

		public void fieldChanged(Field field, int context) {
			if (field == t_up)
				moveField(this.field, -1);
			else if (field == t_set)
				close();
			else if (field == t_down)
				moveField(this.field, 1);
		}

		public boolean keyChar(char c, int status, int time) {
			if (c == '\033') {
				close();
				return true;
			}
			switch (c) {
			case 117: // 'u'
				fieldChanged(t_up, 0);
				return true;

			case 115: // 's'
				fieldChanged(t_set, 0);
				return true;

			case 100: // 'd'
				fieldChanged(t_down, 0);
				return true;
			}
			return super.keyChar(c, status, time);
		}

		ButtonField t_up;
		ButtonField t_set;
		ButtonField t_down;
		TimeField field;

		public MovePopup(TimeField field) {
			super(new VerticalFieldManager());
			this.field = field;
			t_up = new ButtonField("U\u0332p", 0x300010004L);
			t_up.setChangeListener(this);
			t_set = new ButtonField("S\u0332et", 0x300010004L);
			t_set.setChangeListener(this);
			t_down = new ButtonField("D\u0332own", 0x300010004L);
			t_down.setChangeListener(this);
			add(t_up);
			add(t_set);
			add(t_down);
		}

	}

	private class OptionsScreen extends PopupScreen implements
			FieldChangeListener {

		private ObjectChoiceField showTimeField, screensaverField;
		private CheckboxField lockKeysField;
		private ButtonField save, cancel;

		public OptionsScreen() {
			super(new VerticalFieldManager());

			showTimeField = new ObjectChoiceField("Show Time: ", new Object[] {
					"Analog Clock", "Digital Clock", "Both" },
					Options.SHOW_ANALOG_CLOCK && Options.SHOW_DIGITAL_CLOCK ? 2
							: (Options.SHOW_ANALOG_CLOCK ? 0 : 1),
					Field.FIELD_LEFT | DrawStyle.LEFT | Field.FOCUSABLE);

			screensaverField = new ObjectChoiceField(
					"Screensaver: ",
					new Object[] { "Off", "20 Seconds", "30 Seconds",
							"1 Minute", "3 Minutes", "5 Minutes", "10 Minutes" },
					defaultScreensaverChoice(), Field.FIELD_LEFT
							| DrawStyle.LEFT | Field.FOCUSABLE);
			screensaverField.setChangeListener(this);

			lockKeysField = new CheckboxField("Lock keys in screensaver",
					Options.LOCK_KEYS);

			save = new ButtonField("Save", ButtonField.CONSUME_CLICK);
			save.setChangeListener(this);
			cancel = new ButtonField("Cancel", ButtonField.CONSUME_CLICK);
			cancel.setChangeListener(this);
			HorizontalFieldManager cmds = new HorizontalFieldManager();
			cmds.add(save);
			cmds.add(cancel);

			add(showTimeField);
			add(screensaverField);
			if (screensaverField.getSelectedIndex() > 0)
				add(lockKeysField);
			add(cmds);
		}

		private int defaultScreensaverChoice() {
			switch (Options.SCREENSAVER_TIMEOUT) {
			case 0:
				return 0;
			case 20:
				return 1;
			case 30:
				return 2;
			case 60:
				return 3;
			case 180:
				return 4;
			case 300:
				return 5;
			case 600:
				return 6;
			}
			return 0;
		}

		private int screensaverChoice() {
			switch (screensaverField.getSelectedIndex()) {
			case 0:
				return 0;
			case 1:
				return 20;
			case 2:
				return 30;
			case 3:
				return 60;
			case 4:
				return 180;
			case 5:
				return 300;
			case 6:
				return 600;
			}
			return 0;
		}

		public void add(Field field) {
			super.add(field);
		}

		public void close() {
			super.close();
		}

		public void fieldChanged(Field field, int context) {
			if (field == screensaverField) {
				if (screensaverField.getSelectedIndex() == 0) {
					try {
						delete(lockKeysField);
					} catch (Exception e) {
					}
				} else {
					try {
						if (getFieldCount() < 4)
							insert(lockKeysField, getFieldCount() - 1);
					} catch (Exception e) {
					}
				}
			} else if (field == cancel) {
				setDirty(false);
				close();
			} else if (field == save) {
				switch (showTimeField.getSelectedIndex()) {
				case 0:
					Options.SHOW_ANALOG_CLOCK = (true);
					Options.SHOW_DIGITAL_CLOCK = (false);
					break;
				case 1:
					Options.SHOW_ANALOG_CLOCK = (false);
					Options.SHOW_DIGITAL_CLOCK = (true);
					break;
				case 2:
					Options.SHOW_ANALOG_CLOCK = (true);
					Options.SHOW_DIGITAL_CLOCK = (true);
					break;
				}
				Options.SCREENSAVER_TIMEOUT = screensaverChoice();
				Options.LOCK_KEYS = lockKeysField.getChecked();
				Options.save();
				close();
			}
		}

	}

	public class ChoiceScreen extends PopupScreen {

		public ChoiceScreen() {
			super(
					new VerticalFieldManager(VERTICAL_SCROLL
							| VERTICAL_SCROLLBAR));

			ButtonField ok = new ButtonField("Preset Zone", 0x300010004L);
			ok.setChangeListener(new FieldChangeListener() {
				public void fieldChanged(Field f, int instance) {
					UiApplication.getUiApplication().pushScreen(
							new AddTimeZone(getInstance()));
					setDirty(false);
					close();
				}
			});
			ButtonField close = new ButtonField("Custom Zone", 0x300010004L);
			close.setChangeListener(new FieldChangeListener() {
				public void fieldChanged(Field f, int instance) {
					UiApplication.getUiApplication().pushScreen(
							new CustomScreen(getInstance()));
					setDirty(false);
					close();
				}
			});

			add(ok);
			add(close);
		}

		public boolean keyChar(char key, int status, int time) {
			boolean retval = false;
			switch (key) {
			case Characters.ESCAPE:
				close();
				break;
			default:
				retval = super.keyChar(key, status, time);
			}
			return retval;
		}

	}

}