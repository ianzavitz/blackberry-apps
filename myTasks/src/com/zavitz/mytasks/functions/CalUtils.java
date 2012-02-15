package com.zavitz.mytasks.functions;

import java.util.*;
import javax.microedition.pim.*;

import net.rim.blackberry.api.pdap.BlackBerryPIMList;
import net.rim.blackberry.api.pdap.PIMListListener;
import net.rim.device.api.i18n.SimpleDateFormat;

import com.zavitz.mytasks.elements.Task;
import com.zavitz.mytasks.fields.CategoryField;

public class CalUtils implements PIMListListener {

	public static Hashtable events = new Hashtable();

	public static void loadEvents() {
		try {
			Task[] tasks = TaskUtils.getTasks(-1, Long.MAX_VALUE);
			int total = 0;
			EventList _eventList = (EventList) PIM.getInstance().openPIMList(
					PIM.EVENT_LIST, PIM.READ_WRITE);
			Enumeration _enum = _eventList.items();
			while (_enum.hasMoreElements()) {
				PIMItem item = (PIMItem) _enum.nextElement();
				String uid = item.getString(Event.UID, 0);
				if (contains(tasks, uid)) {
					events.put(uid, item);
				}
				total++;
			}
		} catch (PIMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			((BlackBerryPIMList) PIM.getInstance().openPIMList(PIM.EVENT_LIST,
					PIM.READ_WRITE)).addListener(new CalUtils());
			System.out.println("\tAdded listener");
		} catch (PIMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static boolean contains(Task[] tasks, String uid) {
		System.out.println("Checking " + uid);
		for (int i = 0; i < tasks.length; i++) {
			System.out
					.println("\t" + tasks[i].getName() + " : "
							+ tasks[i].getUID() + " : "
							+ tasks[i].getUID().equals(uid));
			if (tasks[i].getUID().equals(uid))
				return true;
		}
		return false;
	}

	public static String format(String format) {
		return new SimpleDateFormat(format).formatLocal(System
				.currentTimeMillis());
	}

	public static long getTomorrowTime() {
		Calendar calendar = Calendar.getInstance();
		Date d = calendar.getTime();
		d.setTime(d.getTime() + (24 * 60 * 60 * 1000));
		calendar.setTime(d);
		calendar.set(Calendar.HOUR_OF_DAY, 9);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime().getTime();
	}

	public static long getToday() {
		Calendar calendar = Calendar.getInstance();
		Date d = calendar.getTime();
		calendar.setTime(d);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime().getTime();
	}

	public static long getFrom(int style) {
		switch (style) {
		case CategoryField.NO_DUE_DATE:
		case CategoryField.ALL:
			return -1;
		case CategoryField.TODAY:
			return getToday();
		case CategoryField.TOMORROW:
			return getToday() + daysToMs(1);
		case CategoryField.SOON:
			return getToday() + daysToMs(2);
		case CategoryField.OVERDUE:
			return getToday() - daysToMs(365);
		}
		return 0;
	}

	public static long getTo(int style) {
		switch (style) {
		case CategoryField.NO_DUE_DATE:
			return 0;
		case CategoryField.ALL:
			return Long.MAX_VALUE;
		case CategoryField.TODAY:
			return getToday() + daysToMs(1);
		case CategoryField.TOMORROW:
			return getToday() + daysToMs(2);
		case CategoryField.SOON:
			return getToday() + daysToMs(7);
		case CategoryField.OVERDUE:
			return getToday();
		}
		return 0;
	}

	public static boolean inTime(long from, long time, long to) {
		return from < time && time <= to;
	}

	public static long daysToMs(long days) {
		return days * (86400000);
	}

	public static long hoursToMs(long hours) {
		return hours * (3600000);
	}

	public static Event getEvent(Task task) {
		try {
			Event e = (Event) events.get(task.getUID());
			return e;
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return null;
	}

	public static Event getEventOld(Task task) {
		try {
			EventList _eventList = (EventList) PIM.getInstance().openPIMList(
					PIM.EVENT_LIST, PIM.READ_WRITE);
			Enumeration _events = _eventList.items(task.getUID());
			while (_events.hasMoreElements()) {
				Event e = (Event) _events.nextElement();
				if (e.getString(Event.UID, 0).equals(task.getUID())) {
					return e;
				}
			}
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return null;
	}

	public static boolean updateEvent(Event event, Task task) {
		try {
			if (task.getDateDue() == 0)
				return deleteEvent(event);
			event
					.setString(Event.SUMMARY, 0, PIMItem.ATTR_NONE, task
							.getName());
			event.setString(Event.NOTE, 0, PIMItem.ATTR_NONE,
					"This event was made by myTasks");
			event.setDate(Event.START, 0, PIMItem.ATTR_NONE, task.getDateDue());
			event.setDate(Event.END, 0, PIMItem.ATTR_NONE, task.getDateDue());
			if (task.getReminder() > 0) {
				if (event.countValues(Event.ALARM) > 0)
					event.setInt(Event.ALARM, 0, PIMItem.ATTR_NONE, (int) (task
							.getReminder()));
				else
					event.addInt(Event.ALARM, PIMItem.ATTR_NONE, (int) (task
							.getReminder()));
			} else if (event.countValues(Event.ALARM) > 0)
				event.removeValue(Event.ALARM, 0);
			event.commit();
			return true;
		} catch (Exception e) {
		}
		return false;
	}

	public static boolean deleteEvent(Event event) {
		try {
			EventList _eventList = (EventList) PIM.getInstance().openPIMList(
					PIM.EVENT_LIST, PIM.READ_WRITE);
			_eventList.removeEvent(event);
			return true;
		} catch (Exception e) {
		}
		return false;
	}

	public static Event createEvent(Task task) {
		try {
			EventList _eventList = (EventList) PIM.getInstance().openPIMList(
					PIM.EVENT_LIST, PIM.READ_WRITE);
			Event newEvent = _eventList.createEvent();
			newEvent
					.addString(Event.SUMMARY, PIMItem.ATTR_NONE, task.getName());
			newEvent.addString(Event.NOTE, PIMItem.ATTR_NONE,
					"This event was made by myTasks");
			newEvent.addDate(Event.START, PIMItem.ATTR_NONE, task.getDateDue());
			newEvent.addDate(Event.END, PIMItem.ATTR_NONE, task.getDateDue());
			if (task.getReminder() > 0)
				newEvent.addInt(Event.ALARM, PIMItem.ATTR_NONE, (int) (task
						.getReminder()));
			newEvent.commit();
			task.setUID(newEvent.getString(Event.UID, 0));
			return newEvent;
		} catch (Exception e) {
		}
		return null;
	}

	public void itemAdded(PIMItem item) {
		events.put(item.getString(Event.UID, 0), item);
	}

	public void itemRemoved(PIMItem item) {
		events.remove(item.getString(Event.UID, 0));
	}

	public void itemUpdated(PIMItem oldItem, PIMItem newItem) {
		itemRemoved(oldItem);
		itemAdded(newItem);
	}

}