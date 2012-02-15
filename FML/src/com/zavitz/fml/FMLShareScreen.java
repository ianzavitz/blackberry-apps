package com.zavitz.fml;

import javax.microedition.io.Connector;
import javax.wireless.messaging.MessageConnection;
import javax.wireless.messaging.TextMessage;

import net.rim.blackberry.api.invoke.*;
import net.rim.blackberry.api.mail.*;
import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.ui.container.*;

public class FMLShareScreen extends PopupScreen implements FieldChangeListener {

	private ButtonField sms, email, pin, cancel;
	private String text;

	public FMLShareScreen(String text) {
		super(new VerticalFieldManager(), Field.FOCUSABLE);
		this.text = text;

		sms = new ButtonField("Share by SMS", Field.FIELD_HCENTER
				| DrawStyle.HCENTER);
		sms.setChangeListener(this);
		email = new ButtonField("Share by Email", Field.FIELD_HCENTER
				| DrawStyle.HCENTER);
		email.setChangeListener(this);
		pin = new ButtonField("Share by PIN", Field.FIELD_HCENTER
				| DrawStyle.HCENTER);
		pin.setChangeListener(this);
		cancel = new ButtonField("Cancel", Field.FIELD_HCENTER
				| DrawStyle.HCENTER);
		cancel.setChangeListener(this);

		add(email);
		add(pin);
		add(sms);
		add(cancel);
	}

	public void fieldChanged(Field f, int arg1) {
		setDirty(false);
		close();
		if (f == sms) {
			try {
				if (text.length() > 160) {
					UiApplication.getUiApplication().invokeAndWait(new Runnable() {
						public void run() {
							Dialog.inform("Story longer than 160 characters, split into two text messages.");
						}
					});
					String temp = text.substring(160);
					text = text.substring(0,160);
					MessageConnection mc = (MessageConnection) Connector
							.open("sms://");
					TextMessage message = (TextMessage) mc
							.newMessage(MessageConnection.TEXT_MESSAGE);
					message.setPayloadText(temp);
					Invoke.invokeApplication(Invoke.APP_TYPE_MESSAGES,
							new MessageArguments(message));
				}
				MessageConnection mc = (MessageConnection) Connector
						.open("sms://");
				TextMessage message = (TextMessage) mc
						.newMessage(MessageConnection.TEXT_MESSAGE);
				message.setPayloadText(text);
				Invoke.invokeApplication(Invoke.APP_TYPE_MESSAGES,
						new MessageArguments(message));
			} catch (Exception e) {
			}
		} else if (f == email) {
			try {
				Invoke.invokeApplication(Invoke.APP_TYPE_MESSAGES,
						new MessageArguments(MessageArguments.ARG_NEW, "",
								"A Story on FMyLife", text));
			} catch (Exception e) {
			}
		} else if (f == pin) {
			try {
				Message m = new Message();
				m.setContent(text);
				m.setSubject("A Story on FMyLife");
				Invoke.invokeApplication(Invoke.APP_TYPE_MESSAGES,
						new MessageArguments(m));
			} catch (Exception e) {
			}
		}
	}

}