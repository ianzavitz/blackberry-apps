package com.zavitz.mybudget.fields;

import com.zavitz.mybudget.elements.*;
import com.zavitz.mybudget.*;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Characters;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.XYRect;

/* This field will show the budget name, amount, amount spent and the gauge */
public class BudgetField extends Field {

	private Budget budget;
	private Font bold, plain, sub;
	private boolean even = true;
	private static Bitmap bg;

	static {
		bg = Bitmap.getBitmapResource("bg.png");
	}

	public BudgetField(Budget _budget) {
		budget = _budget;
		bold = getFont().derive(Font.BOLD, getFont().getHeight() - 2);
		plain = getFont().derive(Font.PLAIN, getFont().getHeight() - 2);
		sub = getFont().derive(Font.PLAIN, getFont().getHeight() - 6);
		setFont(plain);
	}

	public void setEven(boolean flag) {
		even = !flag;
		invalidate();
	}

	public Budget getBudget() {
		return budget;
	}

	public int getColor() {
		int percent = (int) ((100 * budget.getAmountSpent()) / budget
				.getAmount());
		if (percent <= 50)
			return 0x0027990a;
		else if (percent <= 75)
			return 0x00fbf34a;
		else if (percent <= 90)
			return 0x00fea432;
		else
			return 0x00b70000;
	}

	public int getDrawWidth(int boxWidth) {
		int w = (int) ((boxWidth * budget.getAmountSpent()) / budget
				.getAmount());
		return w > boxWidth ? boxWidth : w;
	}

	public int getPreferredWidth() {
		return Display.getWidth();
	}

	public int getPreferredHeight() {
		return getFont().getHeight() + 4 + 24;
	}

	protected void layout(int width, int height) {
		setExtent(getPreferredWidth(), getPreferredHeight());
	}

	public void drawFocus(Graphics graphics, boolean on) {
		graphics.setBackgroundColor(Design.BG_SELECTED);
		graphics.clear(5, 0, getPreferredWidth() - 10, getPreferredHeight());
		XYRect bgRect = new XYRect(5,
				getPreferredHeight() - 44 > 0 ? getPreferredHeight() - 44 : 0,
				getPreferredWidth() - 10, 44);
		graphics.drawBitmap(bgRect, bg, 0, Design.SELECTED_Y);
		paint(graphics);
	}

	protected void paint(Graphics graphics) {
		if (graphics.getBackgroundColor() != Design.BG_SELECTED) {
			graphics.setBackgroundColor(even ? Design.BG_ALT_1
					: Design.BG_ALT_2);
			graphics
					.clear(5, 0, getPreferredWidth() - 10, getPreferredHeight());
			XYRect bgRect = new XYRect(5,
					getPreferredHeight() - 44 > 0 ? getPreferredHeight() - 44
							: 0, getPreferredWidth() - 10, 44);
			graphics.drawBitmap(bgRect, bg, 0, even ? Design.ALT_1_Y
					: Design.ALT_2_Y);
		}

		int font[] = graphics.getBackgroundColor() == Design.BG_SELECTED ? new int[] {
				Design.TEXT_SELECTED, Design.SUBTEXT_SELECTED }
				: (even ? new int[] { Design.TEXT_ALT_1, Design.SUBTEXT_ALT_1 }
						: new int[] { Design.TEXT_ALT_2, Design.SUBTEXT_ALT_2 });

		graphics.setFont(bold);
		graphics.setColor(font[0]);
		int subOffset = graphics
				.drawText(budget.getName(), 7, 1, DrawStyle.TOP);

		graphics.setFont(plain);
		String amount = "$" + Utilities.formatDouble(budget.getAmountSpent())
				+ " of $" + Utilities.formatDouble(budget.getAmount());
		graphics.setColor(font[0]);
		graphics.drawText(amount, getPreferredWidth() - 8
				- getFont().getAdvance(amount), 1, DrawStyle.TOP
				| DrawStyle.LEFT);

		// graphics.setFont(sub);
		// graphics.setColor(font[1]);
		// graphics.drawText(budget.getCycleEnd(), 7 + 4 + subOffset, 1 +
		// (bold.getHeight() - sub.getHeight()) / 2, DrawStyle.TOP);

		graphics.setColor(0x00FFFFFF);
		graphics.fillRect(7, getPreferredHeight() - 24,
				getPreferredWidth() - 14, 20);
		graphics.setColor(getColor());
		int w = getDrawWidth(getPreferredWidth() - 14);
		graphics.fillRect(7, getPreferredHeight() - 24, w, 20);
		graphics.setColor(0x00FFFFFF);
		graphics.setGlobalAlpha(256 / 4);
		graphics.fillRect(7, getPreferredHeight() - 24, w, 10);
		graphics.setGlobalAlpha(255);
		graphics.setColor(0x00b0b0b0);
		graphics.drawRect(7, getPreferredHeight() - 24,
				getPreferredWidth() - 14, 20);
		graphics.setColor(0x00333333);
		graphics.drawLine(5, getPreferredHeight() - 1, getPreferredWidth() - 5,
				getPreferredHeight() - 1);
		graphics.drawLine(4, 0, 4, getPreferredHeight());
		graphics.drawLine(getPreferredWidth() - 5, 0, getPreferredWidth() - 5,
				getPreferredHeight());
		graphics.setBackgroundColor(0x00abdfbb);
	}

	public boolean isFocusable() {
		return true;
	}

	public boolean keyChar(char key, int status, int time) {
		return navigationClick(status, time);
	}

	class Design {
		public static final int BG_ALT_1 = 0x00FFFFFF;
		public static final int BG_ALT_2 = 0x00dedede;
		public static final int BG_SELECTED = 0x001474a4;
		public static final int TEXT_ALT_1 = 0x00333333;
		public static final int TEXT_ALT_2 = 0x00333333;
		public static final int TEXT_SELECTED = 0x00FFFFFF;
		public static final int SUBTEXT_ALT_1 = 0x00555555;
		public static final int SUBTEXT_ALT_2 = 0x00555555;
		public static final int SUBTEXT_SELECTED = 0x00CCCCCC;

		public static final int ALT_1_Y = 44;
		public static final int ALT_2_Y = 88;
		public static final int SELECTED_Y = 0;
	}

}
