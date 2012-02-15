package com.zavitz.mytasks.fields;

import javax.microedition.pim.Event;

import com.zavitz.mytasks.elements.Task;
import com.zavitz.mytasks.functions.*;

import net.rim.blackberry.api.invoke.CalendarArguments;
import net.rim.blackberry.api.invoke.Invoke;
import net.rim.device.api.i18n.DateFormat;
import net.rim.device.api.i18n.SimpleDateFormat;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Characters;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.DateField;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.AutoTextEditField;
import net.rim.device.api.ui.component.GaugeField;
import net.rim.device.api.ui.component.Status;
import net.rim.device.api.ui.container.*;

public class BitmapInput extends Field {

	final String initText;
	String text;
	Bitmap image;
	int fontColor;
	public long date;
	int selected;
	boolean editable;
	Task task;

	static Bitmap arrow;

	static {
		arrow = Bitmap.getBitmapResource("arrow.png");
	}

	public BitmapInput(String init, String res) {
		this(init, init, res);
	}

	public BitmapInput(String init, String start, String res) {
		super(FOCUSABLE);

		editable = true;
		text = start;
		initText = init;
		fontColor = 0x00777777;
		if (res != null)
			image = Bitmap.getBitmapResource(res);
		else
			image = null;

		setFont(getFont().derive(Font.PLAIN));
	}

	public boolean keyChar(char c, int status, int time) {
		if (initText.equals("Save") || initText.equals("Close")
				|| initText.equals("View in Calendar")) {
			if (c == Characters.ENTER) {
				open();
				return true;
			}
		} else {
			if (editable) {
				PopupInput input = new PopupInput();
				UiApplication.getUiApplication().pushScreen(input);
				return (c == Characters.ENTER) ? true : input.keyChar(c,
						status, time);
			}
		}
		return super.keyChar(c, status, time);
	}

	public void setTask(Task task) {
		this.task = task;
	}

	public void setEditable(boolean flag) {
		editable = flag;
		invalidate();
	}

	public void setText(String s) {
		text = s;
		fontColor = 0x00555555;
		setFont(getFont().derive(Font.BOLD));
		updateLayout();
		invalidate();
	}

	public void setTextQuiet(String s) {
		text = s;
		updateLayout();
		invalidate();
	}

	public void setSelected(int i) {
		selected = i;
		if (initText.equals("Priority")) {
			switch (selected) {
			case Task.LOW_PRIORITY:
				image = Bitmap.getBitmapResource("low_priority.png");
				setTextQuiet("Low Priority");
				break;
			case Task.NORMAL_PRIORITY:
				setTextQuiet("Normal Priority");
				break;
			case Task.HIGH_PRIORITY:
				image = Bitmap.getBitmapResource("high_priority.png");
				setTextQuiet("High Priority");
				break;
			}
		} else {
			switch (selected) {
			case Task.NOT_STARTED:
				image = Bitmap.getBitmapResource("not_started.png");
				setTextQuiet("Not Started");
				break;
			case Task.IN_PROGRESS:
				image = Bitmap.getBitmapResource("progress.png");
				setTextQuiet("In Progress");
				break;
			case Task.WAITING:
				image = Bitmap.getBitmapResource("waiting.png");
				setTextQuiet("Waiting");
				break;
			case Task.DEFERRED:
				image = Bitmap.getBitmapResource("deferred.png");
				setTextQuiet("Deferred");
				break;
			case Task.COMPLETED:
				image = Bitmap.getBitmapResource("completed.png");
				setTextQuiet("Complete");
				break;
			}
			selectionChange(selected);
		}
	}
	
	public void selectionChange(int status) {
	}

	public int getPreferredWidth() {
		return Display.getWidth();
	}

	public int getPreferredHeight() {
		int h = getFont().getHeight() + 4;
		if (image != null && image.getHeight() + 4 > h)
			return image.getHeight() + 4;
		return h;
	}

	protected void layout(int width, int height) {
		setExtent(getPreferredWidth(), getPreferredHeight());
	}

	protected void drawFocus(Graphics graphics, boolean on) {
		graphics.setGlobalAlpha(19);
		graphics.setBackgroundColor(0);
		graphics.clear();
		graphics.setGlobalAlpha(255);
		paint(graphics);
	}

	protected void paint(Graphics graphics) {
		int offset = 0;
		if (image != null) {
			graphics.drawBitmap(3,
					(getPreferredHeight() - image.getHeight()) / 2, image
							.getWidth(), image.getHeight(), image, 0, 0);
			offset = 5 + image.getWidth();
		}

		graphics.setColor(fontColor);
		graphics.drawText(text, 3 + offset, getPreferredHeight() / 2,
				DrawStyle.VCENTER | DrawStyle.ELLIPSIS, getPreferredWidth()
						- 10 - arrow.getWidth() - offset - 3);

		if (editable)
			graphics.drawBitmap(getPreferredWidth() - 5 - arrow.getWidth(),
					(getPreferredHeight() - arrow.getHeight()) / 2, arrow
							.getWidth(), arrow.getHeight(), arrow, 0, 0);

		graphics.setColor(0x00BCBCBC);
		if (initText.equals("Save"))
			graphics.drawLine(0, 0, getPreferredWidth(), 0);
		graphics.drawLine(0, getPreferredHeight() - 1, getPreferredWidth(),
				getPreferredHeight() - 1);
	}

	public int getSelected() {
		if (initText.equals("Normal"))
			return Task.NORMAL_PRIORITY;
		if (initText.equals("Not Started"))
			return Task.NOT_STARTED;
		return selected;
	}

	public String getText() {
		if (text.equals(initText))
			return "";
		return text;
	}

	public long getDate() {
		if (text.equals(initText))
			return 0;
		return date;
	}

	public void open() {
		if (initText.equals("View in Calendar")) {
			Status.show("Opening in calendar...", 1000);
			Event event = CalUtils.getEvent(task);
			System.out.println(event.toString());
			CalendarArguments args = new CalendarArguments(
					CalendarArguments.ARG_VIEW_DEFAULT, event);
			Invoke.invokeApplication(Invoke.APP_TYPE_CALENDAR, args);
		} else if (editable)
			UiApplication.getUiApplication().pushScreen(new PopupInput());
	}

	public boolean navigationClick(int status, int time) {
		open();
		return true;
	}

	class PopupInput extends PopupScreen {

		AutoTextEditField input;
		DateField dateInput;
		ObjectChoiceField priority, status, color;
		ButtonField ok, close, remove;
		GaugeField gauge;

		public PopupInput() {
			super(new VerticalFieldManager());

			if (initText.equals("Tags")) {
				LabelField label = new LabelField(initText + ":");
				label.setFont(getFont().derive(Font.BOLD));

				input = new AutoTextEditField("", initText.equals(text) ? ""
						: text);
				input.setFont(getFont().derive(Font.PLAIN));
				LabelField separate = new LabelField("Separate tags by commas");
				separate.setFont(getFont().derive(Font.ITALIC,
						getFont().getHeight() - 4));
				add(label);
				add(separate);
				add(input);
			} else if (initText.equals("Task Name")
					|| initText.equals("Description")) {
				LabelField label = new LabelField(initText + ":");
				label.setFont(getFont().derive(Font.BOLD));

				input = new AutoTextEditField("", initText.equals(text) ? ""
						: text);
				if(input.getText().equals("No description"))
					input.setText("");
				input.setFont(getFont().derive(Font.PLAIN));
				add(label);
				add(input);
			} else if (initText.equals("Due Date")
					|| initText.equals("Reminder")) {
				LabelField label = new LabelField(initText + ":");
				label.setFont(getFont().derive(Font.BOLD));

				input = new AutoTextEditField("", initText.equals(text) ? ""
						: text);
				input.setFont(getFont().derive(Font.PLAIN));

				dateInput = new DateField("", 0, new SimpleDateFormat(
						DateFormat.DATETIME_DEFAULT), Field.FIELD_LEFT
						| DrawStyle.LEFT | Field.FOCUSABLE);
				dateInput.setFont(getFont().derive(Font.PLAIN));
				dateInput.setDate(date == 0 ? CalUtils.getTomorrowTime() : date);

				remove = new ButtonField("Remove");
				remove.setChangeListener(new FieldChangeListener() {
					public void fieldChanged(Field f, int instance) {
						date = 0;
						setTextQuiet("No " + initText.toLowerCase() + " set.");
						setDirty(false);
						close();
					}
				});

				add(label);
				add(dateInput);
			} else if (initText.equals("Priority")) {
				priority = new ObjectChoiceField("Priority:", new String[] {
						"High", "Normal", "Low" }, selected);
				priority.setFont(getFont().derive(Font.BOLD));
				add(priority);
			} else if (initText.equals("Status")) {
				status = new ObjectChoiceField("Status:", new String[] {
						"Not Started", "In Progress", "Waiting", "Deferred",
						"Completed" }, selected);
				status.setFont(getFont().derive(Font.BOLD));
				add(status);
			} else if(initText.equals("Color")) {
				color = new ObjectChoiceField("Color:", new String[] {
						"White", "Red", "Green", "Orange", "Yellow", "Teal", "Pink"
				});
				color.setFont(getFont().derive(Font.BOLD));
				add(color);
			} else if(initText.equals("Percent Complete")) {
				LabelField label = new LabelField("Percent Complete:");
				label.setFont(getFont().derive(Font.BOLD));
				LabelField label2 = new LabelField("");
				if(DeviceInfo.getDeviceName().startsWith("95")) {
					label2.setText("Click the gauge below to make changes");
				} else {
					label2.setText("Hold ALT key and scroll trackball to make changes");
				}
				label2.setFont(getFont().derive(Font.ITALIC, getFont().getHeight() - 4));
				
				String inputText = getText();
				if(inputText.indexOf("%") > 0)
					inputText = inputText.substring(0, inputText.indexOf("%"));
				
				gauge = new GaugeField("", 0, 100, Integer.parseInt(inputText), FOCUSABLE | GaugeField.EDITABLE | GaugeField.PERCENT);
				add(label);
				add(label2);
				add(gauge);
			}

			ok = new ButtonField("Ok");
			ok.setChangeListener(new FieldChangeListener() {
				public void fieldChanged(Field f, int instance) {
					if (initText.equals("Tags")) {
						setText(input.getText());
					} else if (initText.equals("Task Name")
							|| initText.equals("Description"))
						setText(input.getText());
					else if (initText.equals("Due Date")
							|| initText.equals("Reminder")) {
						date = dateInput.getDate();
						setText(new SimpleDateFormat(
								DateFormat.DATETIME_DEFAULT).formatLocal(date));
					} else if (initText.equals("Priority")) {
						selected = priority.getSelectedIndex();
						switch (selected) {
						case Task.LOW_PRIORITY:
							setText("Low Priority");
							break;
						case Task.NORMAL_PRIORITY:
							setText("Normal Priority");
							break;
						case Task.HIGH_PRIORITY:
							setText("High Priority");
							break;
						}
					} else if (initText.equals("Status")) {
						selected = status.getSelectedIndex();
						switch (selected) {
						case Task.NOT_STARTED:
							setText("Not Started");
							break;
						case Task.IN_PROGRESS:
							setText("In Progress");
							break;
						case Task.WAITING:
							setText("Waiting");
							break;
						case Task.DEFERRED:
							setText("Deferred");
							break;
						case Task.COMPLETED:
							setText("Complete");
							break;
						}
						selectionChange(selected);
					} else if (initText.equals("Color")) {
						String choice = (String) color.getChoice(color.getSelectedIndex());
						setText(choice);
					} else if (initText.equals("Percent Complete")) {
						setText(gauge.getValue() + "% Complete");
					}
					setDirty(true);
					close();
				}
			});
			close = new ButtonField("Close");
			close.setChangeListener(new FieldChangeListener() {
				public void fieldChanged(Field f, int instance) {
					setDirty(false);
					close();
				}
			});

			HorizontalFieldManager buttons = new HorizontalFieldManager();
			buttons.add(ok);
			buttons.add(close);
			if (remove != null)
				buttons.add(remove);
			add(buttons);
		}

		public boolean keyChar(char key, int _status, int time) {
			boolean retVal = false;
			switch (key) {
			case Characters.ENTER:
				if (initText.equals("Tags")) {
					setText(input.getText());
				} else if (initText.equals("Task Name")
						|| initText.equals("Description"))
					setText(input.getText());
				else if (initText.equals("Due Date")
						|| initText.equals("Reminder")) {
					date = dateInput.getDate();
					setText(new SimpleDateFormat(DateFormat.DATETIME_DEFAULT)
							.formatLocal(date));
				} else if (initText.equals("Priority")) {
					selected = priority.getSelectedIndex();
					switch (selected) {
					case Task.LOW_PRIORITY:
						setText("Low Priority");
						break;
					case Task.NORMAL_PRIORITY:
						setText("Normal Priority");
						break;
					case Task.HIGH_PRIORITY:
						setText("High Priority");
						break;
					}
				} else if (initText.equals("Status")) {
					selected = status.getSelectedIndex();
					switch (selected) {
					case Task.NOT_STARTED:
						setText("Not Started");
						break;
					case Task.IN_PROGRESS:
						setText("In Progress");
						break;
					case Task.WAITING:
						setText("Waiting");
						break;
					case Task.DEFERRED:
						setText("Deferred");
						break;
					case Task.COMPLETED:
						setText("Complete");
						break;
					}
					selectionChange(selected);
				} else if (initText.equals("Color")) {
					String choice = (String) color.getChoice(color.getSelectedIndex());
					setText(choice);
				} else if (initText.equals("Percent Complete")) {
					setText(gauge.getValue() + "% Complete");
				}
				setDirty(true);
			case Characters.ESCAPE:
				close();
				break;
			default:
				retVal = super.keyChar(key, _status, time);
			}
			return retVal;
		}

	}

}
