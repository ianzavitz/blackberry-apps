package com.zavitz.mybudget.fields;

import com.zavitz.mybudget.UiApp;
import com.zavitz.mybudget.Utilities;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.FontFamily;
import net.rim.device.api.ui.Graphics;

public class TitleField extends Field {

	private static Bitmap savings;
	private static Bitmap bg;
	private Font title[], sub;
	private String customTitle;

	static {
		savings = Bitmap.getBitmapResource("money.png");
		bg = Bitmap.getBitmapResource("bg.png");
	}

	public TitleField() {
		super();
		int height = getFont().getHeight();
		title = new Font[] { getFont().derive(Font.BOLD, height - 4),
				getFont().derive(Font.BOLD, height - 4) };

		FontFamily family = null;
		try {
			family = FontFamily.forName("TBBAlpha Sans Condensed");
			sub = family.getFont(Font.PLAIN, Display.getVerticalResolution() > 7500 ? 16 : 12);
		} catch (ClassNotFoundException e) {
			try {
				family = FontFamily.forName("BBCondensed");
				sub = title[1].derive(Font.PLAIN, Display.getVerticalResolution() > 7500 ? 16 : 12);
			} catch (Exception e1) {
				sub = title[1];
			}
		}
	}

	public TitleField(String title) {
		this();
		customTitle = title;
	}

	public void setTitle(String s) {
		customTitle = s;
		invalidate();
	}

	public int getPreferredHeight() {
		return title[0].getHeight() + 4 + sub.getHeight();
	}

	public int getPreferredWidth() {
		return Display.getWidth();
	}

	protected void layout(int width, int height) {
		setExtent(getPreferredWidth(), getPreferredHeight());
	}

	public void invalidate() {
		super.invalidate();
	}

	protected void paint(Graphics graphics) {
		graphics.setBackgroundColor(0);
		graphics.clear();
		graphics.drawBitmap(0, getPreferredHeight() - sub.getHeight() - 2,
				getPreferredWidth(), getPreferredHeight(), bg, 0, bg
						.getHeight()
						- sub.getHeight() - 2);
		graphics.drawLine(0, getPreferredHeight() - 1, getPreferredWidth(),
				getPreferredHeight() - 1);

		graphics.setColor(0x00FFFFFF);
		graphics.setFont(title[0]);
		graphics.drawText("myBudget", 2, 1, DrawStyle.TOP);

		graphics.setColor(0x00333333);
		graphics.setFont(sub);
		if (customTitle == null) {
			if (UiApp.activeManager.getSavingsTotal() != -1
					|| UiApp.activeManager.getIncome() != -1) {

				String s = "Projected Savings: $"
						+ Utilities.formatDouble(UiApp.activeManager
								.getProjectedSavings());
				int x = graphics.drawText(s, 2, getPreferredHeight() - 4,
						DrawStyle.BASELINE);
				graphics.drawText("Current Savings: $"
						+ Utilities.formatDouble(UiApp.activeManager
								.getSavingsTotal()), x + 6,
						getPreferredHeight() - 4, DrawStyle.BASELINE);
			}
		} else {
			graphics.drawText(customTitle, 2, getPreferredHeight() - 3,
					DrawStyle.BASELINE);
		}

	}

}
